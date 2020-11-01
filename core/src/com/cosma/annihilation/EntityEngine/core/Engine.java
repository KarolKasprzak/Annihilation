package com.cosma.annihilation.EntityEngine.core;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Box2dLight.Light;
import com.cosma.annihilation.Box2dLight.RayHandler;
import com.cosma.annihilation.Components.*;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorObject.MapMaterialObject;
import com.cosma.annihilation.Editor.CosmaMap.CosmaMapLoader;
import com.cosma.annihilation.Editor.CosmaMap.GameMap;
import com.cosma.annihilation.EntityEngine.signals.Listener;
import com.cosma.annihilation.EntityEngine.signals.Signal;
import com.cosma.annihilation.EntityEngine.utils.ImmutableArray;
import com.cosma.annihilation.Items.Item;
import com.cosma.annihilation.Utils.CollisionID;
import com.cosma.annihilation.Utils.Enums.BodyID;
import com.cosma.annihilation.Utils.Serialization.GameEntitySerializer;
import com.cosma.annihilation.Utils.StartStatus;

import java.util.Arrays;


/**
 * The heart of the Entity framework. It is responsible for keeping track of {@link Entity} and
 * managing {@link EntitySystem} objects. The Engine should be updated every tick via the {@link #update(float)} method.
 * <p>
 * With the Engine you can:
 *
 * <ul>
 * <li>Add/Remove {@link Entity} objects</li>
 * <li>Add/Remove {@link EntitySystem}s</li>
 * <li>Obtain a list of entities for a specific {@link Family}</li>
 * <li>Update the main loop</li>
 * <li>Register/unregister {@link EntityListener} objects</li>
 * </ul>
 *
 * @author Stefan Bachmann
 */
public class Engine {
    private static Family empty = Family.all().get();

    private final Listener<Entity> componentAdded = new ComponentListener();
    private final Listener<Entity> componentRemoved = new ComponentListener();

    private SystemManager systemManager = new SystemManager(new EngineSystemListener());
    private EntityManager entityManager = new EntityManager(new EngineEntityListener());
    private ComponentOperationHandler componentOperationHandler = new ComponentOperationHandler(new EngineDelayedInformer());
    private FamilyManager familyManager = new FamilyManager(entityManager.getEntities());
    private boolean updating;
    private CosmaMapLoader mapLoader;
    private Json json;
    private World world;
    private RayHandler rayHandler;
    private Array<Body> bodiesToRemove;
    private OrthographicCamera gameCamera;
    private Array<Light> activeLights = new Array<>();

    private Vector3 lightPosition = new Vector3();
    private float[] lightPositionArray = new float[21];
    private float[] lightColorArray = new float[21];

    StartStatus startStatus;

    //only to map editor
    public Engine() {
    }

    public Engine(World world, RayHandler rayHandler, StartStatus startStatus, OrthographicCamera gameCamera) {
        this.gameCamera = gameCamera;
        this.rayHandler = rayHandler;
        this.world = world;
        this.startStatus = startStatus;
        bodiesToRemove = new Array<>();
        mapLoader = new CosmaMapLoader(world, rayHandler, this);
        json = new Json();
        json.setSerializer(Entity.class, new GameEntitySerializer(world, this));
        if (startStatus.isNewGame()) {
            mapLoader.loadMap("map/asd.map");
//         mapLoader.loadMap("map/bump_test.map");
//            mapLoader.loadMap("map/metro_test.map");
        } else {
            loadGame();
        }
    }

    public void loadGame() {
        for (Entity entity : this.getEntities()) {
            for (Component component : entity.getComponents()) {
                if (component instanceof PhysicsComponent) {
                    world.destroyBody(((PhysicsComponent) component).body);
                    ((PhysicsComponent) component).body = null;
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


        mapLoader.loadMap("save/slot" + startStatus.getSaveSlot() + "/metro_test.map");

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

    public GameMap getCurrentMap() {
        return mapLoader.getMap();
    }

    public void removePhysicBody(Body body) {
        if (!bodiesToRemove.contains(body, true)) {
            bodiesToRemove.add(body);
        }
    }


    public void removeAllBodies() {
        for (Body body : bodiesToRemove) {
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
     * @return active weapon based on player inventory
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


    public Array<Light> getActiveLights() {
        return activeLights;
    }

    /**
     * Use before batch.draw
     *
     * @param normalShader normal map shader
     * @param invertX      invert normal map X
     * @param invertY      invert normal map Y
     */
    public void prepareDataForNormalShaderRender(ShaderProgram normalShader, boolean invertX, boolean invertY) {
        Arrays.fill(lightColorArray, 0);
        Arrays.fill(lightPositionArray, 0);
        activeLights.clear();
        for (Light light : rayHandler.getLightList()) {
            activeLights.add(light);
//            if(light.isRenderWithShader()){
//
//            }

//                        if (gameCamera.frustum.sphereInFrustum(light.getX(), light.getY(), 0, light.getDistance() +3)) {
//
//                        }
        }
        for (int i = 0; i < activeLights.size; i++) {
            if (i < 7) {
                Light light = activeLights.get(i);
                lightPosition.x = light.getX();
                lightPosition.y = light.getY();
                lightPosition.z = 0f;

                gameCamera.project(lightPosition);

                lightPositionArray[i * 3] = lightPosition.x;
                lightPositionArray[1 + (i * 3)] = lightPosition.y;
                lightPositionArray[2 + (i * 3)] = light.getLightZPosition();

                lightColorArray[i * 3] = light.getColor().r;
                lightColorArray[1 + (i * 3)] = light.getColor().g;
                lightColorArray[2 + (i * 3)] = light.getColor().b;
            }
        }

        normalShader.setUniformi("arraySize", activeLights.size);
        normalShader.setUniform3fv("lightPosition[0]", lightPositionArray, 0, 21);
        normalShader.setUniform3fv("lightColor[0]", lightColorArray, 0, 21);

        if (invertX) {
            normalShader.setUniformi("xInvert", 1);
        } else {
            normalShader.setUniformi("xInvert", 0);
        }
        if (invertY) {
            normalShader.setUniformi("yInvert", 1);
        } else {
            normalShader.setUniformi("yInvert", 0);
        }

        normalShader.setUniformf("resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


        Color color = getCurrentMap().getLightsMapLayer().getShaderAmbientLightColor();

        normalShader.setUniformf("ambientColor", color.r, color.g, color.b, getCurrentMap().getLightsMapLayer().getShaderAmbientLightIntensity());
    }


    // todo

    public boolean isPointInDrawField(float x, float y) {
        for (MapMaterialObject mapMaterialObject : getCurrentMap().getMapMaterialObjects()) {
            if (mapMaterialObject.getRectangle().contains(x, y)) {
                return true;
            }
        }
        return false;
    }


    public void spawnBulletEntity(float x, float y, float angle, float speed, boolean flip) {
        Entity entity = this.createEntity();
        PhysicsComponent physicsComponent = this.createComponent(PhysicsComponent.class);
        BulletComponent bulletComponent = this.createComponent(BulletComponent.class);
        TextureComponent textureComponent = this.createComponent(TextureComponent.class);

        textureComponent.texture = Annihilation.getAssets().get("gfx/textures/bullet_trace.png");
        textureComponent.renderAfterLight = false;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        physicsComponent.body = world.createBody(bodyDef);
        physicsComponent.body.setUserData(entity);
        physicsComponent.body.setBullet(true);
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
        physicsComponent.body.createFixture(fixtureDef).setUserData(BodyID.BULLET);


        float cos = MathUtils.cosDeg(angle), sin = MathUtils.sinDeg(angle);
        float vx = cos * speed;
        float vy = sin * speed;
        physicsComponent.body.setLinearVelocity(vx, vy);
        if (flip) {
            textureComponent.flipTexture = true;
        }
        physicsComponent.body.setTransform(physicsComponent.body.getPosition(), MathUtils.atan2(physicsComponent.body.getLinearVelocity().y, physicsComponent.body.getLinearVelocity().x));

        entity.add(textureComponent);
        entity.add(physicsComponent);
        entity.add(bulletComponent);
        this.addEntity(entity);
    }


    /**
     * Creates a new Entity object.
     *
     * @return @{@link Entity}
     */

    public Entity createEntity() {
        return new Entity();
    }

    /**
     * Creates a new {@link Component}. To use that method your components must have a visible no-arg constructor
     */
    public <T extends Component> T createComponent(Class<T> componentType) {
        try {
            return ClassReflection.newInstance(componentType);
        } catch (ReflectionException e) {
            return null;
        }
    }

    /**
     * Adds an entity to this Engine.
     * This will throw an IllegalArgumentException if the given entity
     * was already registered with an engine.
     */
    public void addEntity(Entity entity) {
        boolean delayed = updating || familyManager.notifying();
        entityManager.addEntity(entity, delayed);
    }

    /**
     * Removes an entity from this Engine.
     */
    public void removeEntity(Entity entity) {
        boolean delayed = updating || familyManager.notifying();
        entityManager.removeEntity(entity, delayed);
    }

    /**
     * Removes all entities of the given {@link Family}.
     */
    public void removeAllEntities(Family family) {
        boolean delayed = updating || familyManager.notifying();
        entityManager.removeAllEntities(getEntitiesFor(family), delayed);
    }

    /**
     * Removes all entities registered with this Engine.
     */
    public void removeAllEntities() {
        boolean delayed = updating || familyManager.notifying();
        entityManager.removeAllEntities(delayed);
    }

    /**
     * Returns an {@link ImmutableArray} of {@link Entity} that is managed by the the Engine
     * but cannot be used to modify the state of the Engine. This Array is not Immutable in
     * the sense that its contents will not be modified, but in the sense that it only reflects
     * the state of the engine.
     * <p>
     * The Array is Immutable in the sense that you cannot modify its contents through the API of
     * the {@link ImmutableArray} class, but is instead "Managed" by the Engine itself. The engine
     * may add or remove items from the array and this will be reflected in the returned array.
     * <p>
     * This is an important note if you are looping through the returned entities and calling operations
     * that may add/remove entities from the engine, as the underlying iterator of the returned array
     * will reflect these modifications.
     * <p>
     * The returned array will have entities removed from it if they are removed from the engine,
     * but there is no way to introduce new Entities through the array's interface, or remove
     * entities from the engine through the array interface.
     * <p>
     * Discussion of this can be found at https://github.com/libgdx/ashley/issues/224
     *
     * @return An unmodifiable array of entities that will match the state of the entities in the
     * engine.
     */
    public ImmutableArray<Entity> getEntities() {
        return entityManager.getEntities();
    }

    /**
     * Adds the {@link EntitySystem} to this Engine.
     * If the Engine already had a system of the same class,
     * the new one will replace the old one.
     */
    public void addSystem(EntitySystem system) {
        systemManager.addSystem(system);
    }

    /**
     * Removes the {@link EntitySystem} from this Engine.
     */
    public void removeSystem(EntitySystem system) {
        systemManager.removeSystem(system);
    }

    /**
     * Removes all systems from this Engine.
     */
    public void removeAllSystems() {
        systemManager.removeAllSystems();
    }

    /**
     * Quick {@link EntitySystem} retrieval.
     */
    @SuppressWarnings("unchecked")
    public <T extends EntitySystem> T getSystem(Class<T> systemType) {
        return systemManager.getSystem(systemType);
    }

    /**
     * @return immutable array of all entity systems managed by the {@link Engine}.
     */
    public ImmutableArray<EntitySystem> getSystems() {
        return systemManager.getSystems();
    }

    /**
     * Returns immutable collection of entities for the specified {@link Family}. Will return the same instance every time.
     */
    public ImmutableArray<Entity> getEntitiesFor(Family family) {
        return familyManager.getEntitiesFor(family);
    }

    /**
     * Adds an {@link EntityListener}.
     * <p>
     * The listener will be notified every time an entity is added/removed to/from the engine.
     */
    public void addEntityListener(EntityListener listener) {
        addEntityListener(empty, 0, listener);
    }

    /**
     * Adds an {@link EntityListener}. The listener will be notified every time an entity is added/removed
     * to/from the engine. The priority determines in which order the entity listeners will be called. Lower
     * value means it will get executed first.
     */
    public void addEntityListener(int priority, EntityListener listener) {
        addEntityListener(empty, priority, listener);
    }

    /**
     * Adds an {@link EntityListener} for a specific {@link Family}.
     * <p>
     * The listener will be notified every time an entity is added/removed to/from the given family.
     */
    public void addEntityListener(Family family, EntityListener listener) {
        addEntityListener(family, 0, listener);
    }

    /**
     * Adds an {@link EntityListener} for a specific {@link Family}. The listener will be notified every time an entity is
     * added/removed to/from the given family. The priority determines in which order the entity listeners will be called. Lower
     * value means it will get executed first.
     */
    public void addEntityListener(Family family, int priority, EntityListener listener) {
        familyManager.addEntityListener(family, priority, listener);
    }

    /**
     * Removes an {@link EntityListener}
     */
    public void removeEntityListener(EntityListener listener) {
        familyManager.removeEntityListener(listener);
    }

    /**
     * Updates all the systems in this Engine.
     *
     * @param deltaTime The time passed since the last frame.
     */
    public void update(float deltaTime) {
        if (updating) {
            throw new IllegalStateException("Cannot call update() on an Engine that is already updating.");
        }

        updating = true;
        ImmutableArray<EntitySystem> systems = systemManager.getSystems();
        try {
            for (int i = 0; i < systems.size(); ++i) {
                EntitySystem system = systems.get(i);

                if (system.checkProcessing()) {
                    system.update(deltaTime);
//                    activeLights.clear();
//                    for (Light light : rayHandler.getLightList()) {
//                        if (gameCamera.frustum.sphereInFrustum(light.getX(), light.getY(), 0, light.getDistance() / 2)) {
//                            activeLights.add(light);
//                        }
//                    }
                }

                while (componentOperationHandler.hasOperationsToProcess() || entityManager.hasPendingOperations()) {
                    componentOperationHandler.processOperations();
                    entityManager.processPendingOperations();
                }
            }
        } finally {
            updating = false;
        }
    }

    protected void addEntityInternal(Entity entity) {
        entity.componentAdded.add(componentAdded);
        entity.componentRemoved.add(componentRemoved);
        entity.componentOperationHandler = componentOperationHandler;

        familyManager.updateFamilyMembership(entity);
    }

    protected void removeEntityInternal(Entity entity) {
        familyManager.updateFamilyMembership(entity);

        entity.componentAdded.remove(componentAdded);
        entity.componentRemoved.remove(componentRemoved);
        entity.componentOperationHandler = null;
    }


    private class ComponentListener implements Listener<Entity> {
        @Override
        public void receive(Signal<Entity> signal, Entity object) {
            familyManager.updateFamilyMembership(object);
        }
    }

    private class EngineSystemListener implements SystemManager.SystemListener {
        @Override
        public void systemAdded(EntitySystem system) {
            system.addedToEngineInternal(Engine.this);
        }

        @Override
        public void systemRemoved(EntitySystem system) {
            system.removedFromEngineInternal(Engine.this);
        }
    }

    private class EngineEntityListener implements EntityListener {
        @Override
        public void entityAdded(Entity entity) {
            addEntityInternal(entity);
        }

        @Override
        public void entityRemoved(Entity entity) {
            removeEntityInternal(entity);
        }
    }

    private class EngineDelayedInformer implements ComponentOperationHandler.BooleanInformer {
        @Override
        public boolean value() {
            return updating;
        }
    }
}
