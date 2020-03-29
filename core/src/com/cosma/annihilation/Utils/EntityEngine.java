package com.cosma.annihilation.Utils;

import box2dLight.RayHandler;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.cosma.annihilation.Components.BodyComponent;
import com.cosma.annihilation.Editor.CosmaMap.CosmaMapLoader;
import com.cosma.annihilation.Utils.Serialization.GameEntitySerializer;

public class EntityEngine extends PooledEngine {

    private CosmaMapLoader mapLoader;
    private Json json;
    private World world;
    private RayHandler rayHandler;
    StartStatus startStatus;
    public CosmaMapLoader getMapLoader() {
        return mapLoader;
    }

    public EntityEngine(World world, RayHandler rayHandler,StartStatus startStatus) {
        this.rayHandler = rayHandler;
        this.world = world;
        this.startStatus = startStatus;
        mapLoader = new CosmaMapLoader(world, rayHandler, this);
        json = new Json();
        json.setSerializer(Entity.class, new GameEntitySerializer(world,this));
        if(startStatus.isNewGame()){
            mapLoader.loadMap("map/forest_test.map");
        }else{
            loadGame();
        }
    }

    public void loadGame(){
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

//        mapLoader.loadMap("save/save.json");
//
//
//
//        isPaused = false;

//        FileHandle playerFile = Gdx.files.local("save/player.json");
//        Entity playerEntity = json.fromJson(Entity.class, playerFile);

        mapLoader.loadMap("save/slot"+startStatus.getSaveSlot()+"/forest_test.map");
//        mapLoader.getMap().getEntityArrayList().add(playerEntity);
    }


    public void saveGame(){
        FileHandle mapFile = Gdx.files.local("save/slot"+startStatus.getSaveSlot()+"/"+mapLoader.getMap().getMapName());
        json.setIgnoreUnknownFields(false);
        for (Entity entity : this.getEntities()) {
            if (!mapLoader.getMap().getEntityArrayList().contains(entity)) {
                mapLoader.getMap().getEntityArrayList().add(entity);
            }
        }
//        mapLoader.getMap().getEntityArrayList().remove(playerEntity);
        mapFile.writeString(json.prettyPrint(mapLoader.getMap()), false);




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
}
