package com.cosma.annihilation.Utils;

import box2dLight.RayHandler;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Components.*;
import com.cosma.annihilation.Editor.CosmaMap.CosmaMapLoader;
import com.cosma.annihilation.Editor.CosmaMap.GameMap;
import com.cosma.annihilation.Items.Item;
import com.cosma.annihilation.Utils.Enums.BodyID;
import com.cosma.annihilation.Utils.Serialization.GameEntitySerializer;

public class EntityEngine extends PooledEngine {

    private CosmaMapLoader mapLoader;
    private Json json;
    private World world;
    private RayHandler rayHandler;
    private Array<Body> bodiesToRemove;
    StartStatus startStatus;

    public CosmaMapLoader getMapLoader() {
        return mapLoader;
    }

    public EntityEngine(World world, RayHandler rayHandler, StartStatus startStatus) {
        this.rayHandler = rayHandler;
        this.world = world;
        this.startStatus = startStatus;
        bodiesToRemove = new Array<>();
        mapLoader = new CosmaMapLoader(world, rayHandler, this);
        json = new Json();
        json.setSerializer(Entity.class, new GameEntitySerializer(world, this));
        if (startStatus.isNewGame()) {
            mapLoader.loadMap("map/metro_test.map");
        } else {
            loadGame();
        }
    }

    public void loadGame() {
        for (Entity entity : this.getEntities()) {
            for (Component component : entity.getComponents()) {
                if (component instanceof BodyComponent) {
                    world.destroyBody(((BodyComponent) component).body);
                    ((BodyComponent) component).body = null;
                }
            }
        }
        Array<Body> bodies = new Array<>();
        world.getBodies(bodies);
        for (Body body : bodies) {
            world.destroyBody(body);
        }
        rayHandler.removeAll();
        bodies.clear();
        this.removeAllEntities();


        mapLoader.loadMap("save/slot" + startStatus.getSaveSlot() + "/forest_test.map");

        getPlayerComponent().activeWeapon = getPlayerActiveWeapon();
//        mapLoader.getMap().getEntityArrayList().add(playerEntity);
    }

    public void saveGame() {
        FileHandle mapFile = Gdx.files.local("save/slot" + startStatus.getSaveSlot() + "/" + mapLoader.getMap().getMapName());
        json.setIgnoreUnknownFields(false);
        for (Entity entity : this.getEntities()) {
            if (!mapLoader.getMap().getEntityArrayList().contains(entity)) {
                mapLoader.getMap().getEntityArrayList().add(entity);
            }
        }
//        mapLoader.getMap().getEntityArrayList().remove(playerEntity);
        mapFile.writeString(json.prettyPrint(mapLoader.getMap()), false);
    }

    public GameMap getCurrentMap(){
        return mapLoader.getMap();
    }

    public void removePhysicBody(Body body){
        if(!bodiesToRemove.contains(body, true)){
            bodiesToRemove.add(body);
        }
    }

    public void removeAllBodies(){
        for(Body body: bodiesToRemove){
            world.destroyBody(body);
        }
        bodiesToRemove.clear();
    }

    public Entity getPlayerEntity() {
        return getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
    }

    public PlayerComponent getPlayerComponent() {
        return getPlayerEntity().getComponent(PlayerComponent.class);
    }

    /**
     * return active weapon based on player inventory
     * if null return default "fist" weapon
     **/

    public Item getPlayerActiveWeapon() {
        PlayerInventoryComponent playerInventory = getEntitiesFor(Family.all(PlayerComponent.class).get()).first().getComponent(PlayerInventoryComponent.class);
        if (playerInventory.equippedWeapon == null) {
            return Annihilation.getItem("fist");
        } else {
            return playerInventory.equippedWeapon;
        }
    }

    public Array<Item> getPlayerInventory() {
        return getEntitiesFor(Family.all(PlayerComponent.class).get()).first().getComponent(PlayerInventoryComponent.class).inventoryItems;
    }

    public void spawnBulletEntity(float x, float y,float angle, float speed, boolean flip) {
        Entity entity = this.createEntity();
        BodyComponent bodyComponent = this.createComponent(BodyComponent.class);
        BulletComponent bulletComponent = this.createComponent(BulletComponent.class);
        TextureComponent textureComponent = this.createComponent(TextureComponent.class);

        textureComponent.texture = Annihilation.getAssets().get("gfx/textures/bullet_trace.png");
        textureComponent.renderAfterLight = false;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyComponent.body = world.createBody(bodyDef);
        bodyComponent.body.setUserData(entity);
        bodyComponent.body.setBullet(true);
        //Physic fixture
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.05f, 0.01f);
        FixtureDef fixtureDef = new FixtureDef();
//        fixtureDef.isSensor = true;
        fixtureDef.shape = shape;
        fixtureDef.density = 0.2f;
        fixtureDef.friction = 0.2f;
        fixtureDef.restitution = 0.2f;
        fixtureDef.filter.categoryBits = CollisionID.BULLET;
        fixtureDef.filter.maskBits = CollisionID.MASK_BULLET;
        bodyComponent.body.createFixture(fixtureDef).setUserData(BodyID.BULLET);


        float cos = MathUtils.cosDeg(angle), sin = MathUtils.sinDeg(angle);
        float vx = cos * speed;
        float vy = sin * speed;
        bodyComponent.body.setLinearVelocity(vx, vy);
        if (flip) {
            textureComponent.flipTexture = true;
        }
        bodyComponent.body.setTransform(bodyComponent.body.getPosition(), MathUtils.atan2(bodyComponent.body.getLinearVelocity().y, bodyComponent.body.getLinearVelocity().x));

        entity.add(textureComponent);
        entity.add(bodyComponent);
        entity.add(bulletComponent);
        this.addEntity(entity);
    }


//
//        System.out.println(file.getAbsolutePath());

//        FileHandle mapFile = Gdx.files.local("save/" + mapLoader.getMap().getMapName());
//        FileHandle playerFile = Gdx.files.local("save/player.json");
//        Entity playerEntity = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
//        if (!isPlayerGoToNewLocation) {
//            playerEntity.getComponent(PlayerComponent.class).mapName = mapLoader.getMap().getMapName();
//        }
//        playerFile.writeString(json.prettyPrint(playerEntity), false);
//
//
//        json.setIgnoreUnknownFields(false);
//        System.out.println("save entity");
//        for (Entity entity : engine.getEntities()) {
//            if (!mapLoader.getMap().getEntityArrayList().contains(entity)) {
//                mapLoader.getMap().getEntityArrayList().add(entity);
//            }
//        }
//        System.out.println("entity saved");
//        mapLoader.getMap().getEntityArrayList().remove(playerEntity);
//        System.out.println("write file");
//        mapFile.writeString(json.prettyPrint(mapLoader.getMap()), false);


}
