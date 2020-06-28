package com.cosma.annihilation.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cosma.annihilation.Box2dLight.RayHandler;
import com.cosma.annihilation.Entities.EntityFactory;
import com.cosma.annihilation.EntityEngine.core.Engine;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.EntityEngine.core.EntityListener;
import com.cosma.annihilation.EntityEngine.core.EntitySystem;
import com.cosma.annihilation.EntityEngine.signals.Listener;
import com.cosma.annihilation.EntityEngine.signals.Signal;
import com.cosma.annihilation.Utils.StartStatus;
import com.cosma.annihilation.Systems.*;
import com.cosma.annihilation.Utils.Constants;
import com.cosma.annihilation.Utils.Enums.GameEvent;
import com.cosma.annihilation.Utils.LuaScript.ScriptManager;
import com.cosma.annihilation.Utils.StateManager;

public class WorldBuilder implements Disposable, EntityListener, Listener<GameEvent> {

    public World world;
    private Engine engine;
    private OrthographicCamera camera;
    private Viewport viewport;
    private RayHandler rayHandler;
    private InputManager inputManager;

    SpriteBatch batch;
    PolygonSpriteBatch polygonSpriteBatch;

    public WorldBuilder(StartStatus startStatus, InputMultiplexer inputMultiplexer) {


        //Game camera
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(15, 5,camera);

        viewport.apply(true);
//        batch = new SpriteBatch();
        batch = new SpriteBatch();
        polygonSpriteBatch = new PolygonSpriteBatch();
        //Box2d world & light handler
        world = new World(new Vector2(Constants.WORLD_GRAVITY), true);
        rayHandler = new RayHandler(world);
        rayHandler.setBlur(true);
        rayHandler.setShadows(true);
        RayHandler.useDiffuseLight(true);

        engine = new Engine(world, rayHandler, startStatus, camera);
        engine.addEntityListener(this);

        EntityFactory.getInstance().setEngine(engine);
        EntityFactory.getInstance().setWorld(world);
        Signal<GameEvent> signal = new Signal<GameEvent>();

        ScriptManager scriptManager = new ScriptManager(engine, world);
        scriptManager.runScript("script_test");

        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);

        engine.addSystem(new UserInterfaceSystem(engine));
        engine.addSystem(new ActionSystem(camera, batch));
        engine.addSystem(new ShootingSystem(world, rayHandler, batch, camera, viewport));
        engine.addSystem(new SpriteRenderSystem(camera, batch));

//        engine.addSystem(new RenderSystem(camera, world, batch, shapeRenderer));
//        engine.addSystem(new LightRenderSystem(camera, rayHandler));
//        engine.addSystem(new SkeletonRenderSystem(camera, world, polygonSpriteBatch));
//        engine.addSystem(new TileMapRender(camera, engine.getCurrentMap()));
        engine.addSystem(new UnifiedRenderSystem(batch,camera,world,polygonSpriteBatch,rayHandler));

        engine.addSystem(new HealthSystem(camera));
        engine.addSystem(new PhysicsSystem(world));
        engine.addSystem(new PlayerControlSystem(world, viewport));
        engine.addSystem(new CameraSystem(camera));

        engine.addSystem(new AnimationSystem());
        engine.addSystem(new DebugRenderSystem(camera, world));
        engine.addSystem(new AiSystem(world, batch, camera));
        engine.addSystem(new ParticleRenderSystem(world, batch));
        engine.addEntityListener(this);

        signal.add(getEngine().getSystem(ActionSystem.class));
        signal.add(getEngine().getSystem(ShootingSystem.class));
        signal.add(getEngine().getSystem(UserInterfaceSystem.class));

        CollisionManager collisionManager = new CollisionManager(engine);
        world.setContactListener(collisionManager);
        inputManager = new InputManager(engine);
        inputMultiplexer.addProcessor(engine.getSystem(UserInterfaceSystem.class).getStage());
        inputMultiplexer.addProcessor(inputManager);
    }

    public void update(float delta) {
        debugInput();
        engine.removeAllBodies();
        engine.update(delta);
        inputManager.update(engine);
        camera.update();

    }

    public void resize(int w, int h) {
        viewport.update(w, h, false);

        engine.getSystem(UserInterfaceSystem.class).resizeHUD(w, h);
//        rayHandler.getLightMapTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    public OrthographicCamera getCamera() {
        return camera;
    }


    private void debugInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) camera.translate(0, 1);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) camera.translate(0, -1);
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) camera.translate(-1, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) camera.translate(1, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.Z)) camera.zoom = camera.zoom + 0.1f;
        if (Gdx.input.isKeyPressed(Input.Keys.X)) camera.zoom = camera.zoom - 0.1f;
        if (Gdx.input.isKeyPressed(Input.Keys.P)) {
        }
        if (Gdx.input.isKeyPressed(Input.Keys.V)) {
            StateManager.debugMode = !StateManager.debugMode;
        }

        camera.update();
    }


//    public void saveMap(boolean isPlayerGoToNewLocation) {
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
//    }

//    public void loadMap() {
//        isPaused = true;
//        System.out.println("e " + engine.getEntities().size());
//        System.out.println("b " + world.getBodyCount());
//        for (Entity entity : engine.getEntities()) {
//            for (Component component : entity.getComponents()) {
//                if (component instanceof PhysicsComponent) {
//                    world.destroyBody(((PhysicsComponent) component).body);
//                    ((PhysicsComponent) component).body = null;
//                }
//            }
//        }
//        Array<Body> bodies = new Array<>();
//        world.getBodies(bodies);
//        for (Body body : bodies) {
//            world.destroyBody(body);
//        }
//        rayHandler.removeAll();
//        bodies.clear();
//        engine.removeAllEntities();
//        System.out.println("e " + engine.getEntities().size());
//        System.out.println("b " + world.getBodyCount());
////        mapLoader.loadMap("save/save.json");
////
////
////
////        isPaused = false;
//
//        FileHandle playerFile = Gdx.files.local("save/player.json");
//        Entity playerEntity = json.fromJson(Entity.class, playerFile);
//
//        mapLoader.loadMap("map/" + playerEntity.getComponent(PlayerComponent.class).mapName);
//        mapLoader.getMap().getEntityArrayList().add(playerEntity);
//        isPaused = false;
//
//    }

//    public void goToMap() {
//        saveMap(true);
//        isPaused = true;
//        System.out.println("e " + engine.getEntities().size());
//        System.out.println("b " + world.getBodyCount());
//        for (Entity entity : engine.getEntities()) {
//            for (Component component : entity.getComponents()) {
//                if (component instanceof PhysicsComponent) {
//                    world.destroyBody(((PhysicsComponent) component).body);
//                    ((PhysicsComponent) component).body = null;
//                }
//            }
//        }
//        Array<Body> bodies = new Array<>();
//        world.getBodies(bodies);
//        for (Body body : bodies) {
//            world.destroyBody(body);
//        }
//        rayHandler.removeAll();
//        bodies.clear();
//        engine.removeAllEntities();
//        System.out.println("e " + engine.getEntities().size());
//        System.out.println("b " + world.getBodyCount());
//
//        FileHandle playerFile = Gdx.files.local("save/player.json");
//        Entity playerEntity = json.fromJson(Entity.class, playerFile);
//
//        mapLoader.loadMap("map/" + playerEntity.getComponent(PlayerComponent.class).mapName);
//        mapLoader.getMap().getEntityArrayList().add(playerEntity);
//        isPaused = false;
//
//    }


    public Engine getEngine() {
        return engine;
    }

    @Override
    public void entityAdded(Entity entity) {
    }

    @Override
    public void entityRemoved(Entity entity) {
    }

    @Override
    public void dispose() {
        for (EntitySystem entitySystem : engine.getSystems()) {
            engine.removeSystem(entitySystem);

            if (entitySystem instanceof Disposable) {
                ((Disposable) entitySystem).dispose();
            }
        }
    }

    @Override
    public void receive(Signal<GameEvent> signal, GameEvent object) {
        if (object.equals(GameEvent.PLAYER_GO_TO_NEW_MAP)) {
//            goToMap();
        }
    }
}
