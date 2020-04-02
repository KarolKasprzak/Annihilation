package com.cosma.annihilation.World;

import box2dLight.RayHandler;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cosma.annihilation.Entities.EntityFactory;
import com.cosma.annihilation.Utils.StartStatus;
import com.cosma.annihilation.Systems.*;
import com.cosma.annihilation.Utils.Constants;
import com.cosma.annihilation.Utils.EntityEngine;
import com.cosma.annihilation.Utils.Enums.GameEvent;
import com.cosma.annihilation.Utils.LuaScript.ScriptManager;
import com.cosma.annihilation.Utils.StateManager;
import com.esotericsoftware.spine.*;


public class WorldBuilder implements Disposable, EntityListener, Listener<GameEvent> {


    private EntityEngine engine;
    public World world;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Signal<GameEvent> signal;
    private boolean isPaused = false;
    private RayHandler rayHandler;



    TextureAtlas atlas;
    Skeleton skeleton;
    SkeletonBounds bounds;
    AnimationState state;
    SpriteBatch batch;
    public WorldBuilder(StartStatus startStatus, InputMultiplexer inputMultiplexer) {



        //Game camera
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(16, 9, camera);

        viewport.apply(true);
        batch = new SpriteBatch();
        //Box2d world & light handler
        world = new World(new Vector2(Constants.WORLD_GRAVITY), true);
        rayHandler = new RayHandler(world);
        RayHandler.useDiffuseLight(true);
        rayHandler.resizeFBO(Gdx.graphics.getWidth()/7,Gdx.graphics.getHeight()/7);
        rayHandler.setBlur(true);


        final String vertexShader =
                "attribute vec4 vertex_positions;\n" //
                        + "attribute vec4 quad_colors;\n" //
                        + "attribute float s;\n"
                        + "uniform mat4 u_projTrans;\n" //
                        + "varying vec4 v_color;\n" //
                        + "void main()\n" //
                        + "{\n" //
                        + "   v_color = s *0.8* quad_colors;\n" //
                        + "   gl_Position =  u_projTrans * vertex_positions;\n" //
                        + "}\n";
        final String fragmentShader = "#ifdef GL_ES\n" //
                + "precision lowp float;\n" //
                + "#define MED mediump\n"
                + "#else\n"
                + "#define MED \n"
                + "#endif\n" //
                + "varying vec4 v_color;\n" //
                + "void main()\n"//
                + "{\n" //
                + "  gl_FragColor = "+"sqrt"+"(v_color);\n" //
                + "}";

        ShaderProgram.pedantic = false;
        ShaderProgram lightShader = new ShaderProgram(vertexShader,
                fragmentShader);
//        rayHandler.setLightShader(lightShader);

        rayHandler.setShadows(true);


        camera.zoom = camera.zoom -0.2f;
        engine = new EntityEngine(world,rayHandler,startStatus);
        engine.addEntityListener(this);

        EntityFactory.getInstance().setEngine(engine);
        EntityFactory.getInstance().setWorld(world);
        signal = new Signal<GameEvent>();


//        mapLoader.loadMap("map/lab.map");
//        mapLoader.loadMap("map/forest_test.map");


        ScriptManager scriptManager = new ScriptManager(engine,world);
        scriptManager.runScript("script_test");

        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);

        engine.addSystem(new UserInterfaceSystem(engine, world, this));
        engine.addSystem(new ActionSystem(camera,batch));
        engine.addSystem(new ShootingSystem(world, rayHandler, batch, camera));
        engine.addSystem(new SpriteRenderSystem(camera, batch));
        engine.addSystem(new RenderSystem(camera, world, batch,shapeRenderer));
        engine.addSystem(new LightRenderSystem(camera, rayHandler));
        engine.addSystem(new SkeletonRenderSystem(camera,world,batch));
        engine.addSystem(new HealthSystem(camera));
        engine.addSystem(new CollisionSystem(world));
        engine.addSystem(new PhysicsSystem(world));
        engine.addSystem(new PlayerControlSystem(world,camera,viewport));
        engine.addSystem(new CameraSystem(camera));
        engine.addSystem(new TileMapRender(camera, engine.getMapLoader().getMap()));
        engine.addSystem(new AnimationSystem());
        engine.addSystem(new DebugRenderSystem(camera, world));
        engine.addSystem(new AiSystem(world, batch, camera));

        engine.addEntityListener(this);



        engine.getSystem(PlayerControlSystem.class).addListenerSystems();
        engine.getSystem(CollisionSystem.class).addListenerSystems(this);

        signal.add(getEngine().getSystem(ActionSystem.class));
        signal.add(getEngine().getSystem(ShootingSystem.class));
        signal.add(getEngine().getSystem(UserInterfaceSystem.class));

        inputMultiplexer.addProcessor(engine.getSystem(UserInterfaceSystem.class).getStage());
        inputMultiplexer.addProcessor(engine.getSystem(PlayerControlSystem.class));

    }

    public void update(float delta) {
        debugInput();
        if (!isPaused) {
            engine.update(delta);
            camera.update();
        }
    }

    public void resize(int w, int h) {
        viewport.update(w, h, false);
        engine.getSystem(UserInterfaceSystem.class).resizeHUD(w, h);
        rayHandler.getLightMapTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
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
//                if (component instanceof BodyComponent) {
//                    world.destroyBody(((BodyComponent) component).body);
//                    ((BodyComponent) component).body = null;
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
//                if (component instanceof BodyComponent) {
//                    world.destroyBody(((BodyComponent) component).body);
//                    ((BodyComponent) component).body = null;
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
