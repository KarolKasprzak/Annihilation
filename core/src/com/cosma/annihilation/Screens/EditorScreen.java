package com.cosma.annihilation.Screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.*;
import com.cosma.annihilation.Box2dLight.RayHandler;
import com.cosma.annihilation.Components.PhysicsComponent;
import com.cosma.annihilation.Editor.*;
import com.cosma.annihilation.Editor.CosmaMap.*;
import com.cosma.annihilation.EntityEngine.core.Engine;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.Utils.Serialization.GameEntitySerializer;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.kotcrab.vis.ui.widget.*;


public class EditorScreen implements Screen, InputProcessor {
    private RayHandler rayHandler;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Stage stage;
    private Viewport viewportUi;
    private ExtendViewport viewport;
    private OrthographicCamera camera, cameraUi;
    private World world;
    private InputMultiplexer im;
    private boolean canCameraDrag = false;
    private float zoomLevel = 0.3f;
    private GameMap gameMap;
    private Engine engine;
    private MapCreatorWindow mapCreatorWindow;
    public EditModePanel editModePanel;
    public ObjectPanel objectPanel;
    private MapRender mapRender;
    public LightsPanel lightsPanel;
    public  MaterialPanel materialPanel;
    private String currentMapPatch;

    private boolean isSpriteLayerSelected, isObjectLayerSelected, isLightsLayerSelected, isLightsRendered, isMaterialLayerSelected,
            drawGrid = true, isDebugRenderEnabled = true;
    private VisLabel editorModeLabel;
    private VisTable rightTable;
    private Box2DDebugRenderer debugRenderer;

    public EditorScreen() {
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        world = new World(new Vector2(0, -10), true);
        engine = new Engine();

        cameraUi = new OrthographicCamera();
        cameraUi.update();
        viewportUi = new ScreenViewport(cameraUi);
        stage = new Stage(viewportUi);
        VisUI.load(VisUI.SkinScale.X1);
        rayHandler = new RayHandler(world, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        rayHandler.setBlur(true);
        rayHandler.setShadows(true);
        isLightsRendered = false;

        mapCreatorWindow = new MapCreatorWindow(this);
        debugRenderer = new Box2DDebugRenderer();

        final Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(10, 5, camera);
        camera.update();
        camera.zoom = 5;
        viewport.apply(true);

        im = new InputMultiplexer();
        im.addProcessor(stage);
        im.addProcessor(this);
        batch = new SpriteBatch();

        editorModeLabel = new VisLabel("");
        editorModeLabel.setColor(Color.ORANGE);

        rightTable = new VisTable();
        VisTable topTable = new VisTable();
        VisTable leftTable = new VisTable();

        MenuBar menuBar = new MenuBar();
        menuBar.getTable().add(editorModeLabel).center().expand();

        final VisCheckBox lightsButton = new VisCheckBox("Lights: ");
        lightsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isLightsRendered = lightsButton.isChecked();
            }
        });

        final VisCheckBox diffuseButton = new VisCheckBox("Use diffuse lights: ");
        diffuseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (diffuseButton.isChecked()) {
                    RayHandler.useDiffuseLight(true);
                } else {
                    RayHandler.useDiffuseLight(false);
                }
            }
        });


        final VisCheckBox gridButton = new VisCheckBox("Grid: ");
        gridButton.setChecked(true);
        gridButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                drawGrid = gridButton.isChecked();
            }
        });


        final VisCheckBox debugButton = new VisCheckBox("Debug: ");
        debugButton.setChecked(true);
        debugButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isDebugRenderEnabled = debugButton.isChecked();
            }
        });

        MapFileChooser mapFileChooser = new MapFileChooser(this);


        menuBar.getTable().add(debugButton);
        menuBar.getTable().add(gridButton);
        menuBar.getTable().add(lightsButton);
        menuBar.getTable().add(diffuseButton);
        topTable.add(menuBar.getTable()).expandX().fillX();
        root.add(topTable).fillX().expandX().colspan(2);
        root.row();
        root.add(leftTable).expand().fill();
        root.add(rightTable).fillY();
        leftTable.add().expand().fill();


        Menu fileMenu = new Menu("File");
        fileMenu.addItem(new MenuItem("New gameMap", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stage.addActor(mapCreatorWindow);
            }
        }));
        fileMenu.addItem(new MenuItem("Save", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                saveMap();
            }
        }).setShortcut("ctrl + s"));
        fileMenu.addItem(new MenuItem("Save as", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                saveAs();
            }
        }));
        fileMenu.addItem(new MenuItem("Load map", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stage.addActor(mapFileChooser);
            }
        }));
        fileMenu.addItem(new MenuItem("GameMap options"));
        fileMenu.addItem(new MenuItem("Exit", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        }));
        menuBar.addMenu(fileMenu);

    }


    public void createNewMap(int x, int y, int scale) {
        gameMap = new GameMap(x, y, scale);
        mapRender = new MapRender(shapeRenderer, gameMap, batch, rayHandler, camera, viewport);
        loadPanels();
        setCameraOnMapCenter();
    }

    public void loadMap(String path) {
        currentMapPatch = path;
        if (getMap() != null && !getMap().getEntityArrayList().isEmpty()) {
            for (Entity entity : getMap().getEntityArrayList()) {
                world.destroyBody(entity.getComponent(PhysicsComponent.class).body);
            }
            getMap().getEntityArrayList().clear();
            gameMap = null;
        }
        CosmaMapLoader loader = new CosmaMapLoader(world, rayHandler, engine);
        loader.loadMap(path);
        this.gameMap = loader.getMap();
        if (rightTable.hasChildren()) {
            rightTable.clear();
        }
        loadPanels();
        mapRender = new MapRender(shapeRenderer, gameMap, batch, rayHandler, camera, viewport);
    }

    private void saveAs() {
        Dialogs.showInputDialog(stage, "Enter file name", "name:", new InputDialogAdapter() {
            @Override
            public void finished(String input) {
                gameMap.setMapName(input + ".map");
                currentMapPatch = "map/" + input + ".map";
                FileHandle file = Gdx.files.local("map/" + input + ".map");
                checkAndSaveFile(file);
            }
        });
    }

    private void saveMap() {
        if (currentMapPatch != null) {
            FileHandle file = Gdx.files.local(currentMapPatch);
            checkAndSaveFile(file);
        } else {
            Dialogs.showOKDialog(stage, "Error", "First use 'save as'!");
        }

    }

    private void checkAndSaveFile(FileHandle file) {
        Json json = new Json();
        json.setIgnoreUnknownFields(false);
        json.setSerializer(Entity.class, new GameEntitySerializer(world, engine));
        if (file.exists()) {
            Dialogs.showOptionDialog(stage, "Save:", "file exist, overwrite?", Dialogs.OptionDialogType.YES_NO, new OptionDialogAdapter() {
                @Override
                public void yes() {
                    file.writeString(json.prettyPrint(gameMap), false);
                }

                @Override
                public void no() {
                    super.no();
                }
            });
        } else file.writeString(json.prettyPrint(gameMap), false);
    }


    private void setEditorModeLabel() {
        if (isMaterialLayerSelected) {
            editorModeLabel.setText("Material edit mode");
        } else {
            if (isLightsLayerSelected) {
                editorModeLabel.setText("Light edit mode");
            } else {
                if (isObjectLayerSelected) {
                    editorModeLabel.setText("Object edit mode");
                } else {
                    if (isSpriteLayerSelected) {
                        editorModeLabel.setText("Sprite edit mode");
                    } else {
                        editorModeLabel.setText("");
                    }
                }
            }
        }
    }

    public GameMap getMap() {
        return gameMap;
    }

    private void setCameraOnMapCenter() {
        camera.position.set(4, 4, 0);
    }

    @Override
    public void show() {

        Gdx.input.setInputProcessor(im);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        engine.update(delta);

        camera.update();
        cameraUi.update();
        batch.setProjectionMatrix(camera.combined);
        setEditorModeLabel();
        shapeRenderer.setProjectionMatrix(camera.combined);
        stage.act(delta);
        if (gameMap != null) {
            if (drawGrid) {
                mapRender.renderGrid();
            }
            Gdx.gl.glDisable(GL20.GL_BLEND);
            mapRender.renderMap(delta, isDebugRenderEnabled, isMaterialLayerSelected());
        }

        if (isLightsRendered) {
            rayHandler.setCombinedMatrix(camera);
            rayHandler.updateAndRender();
        }
        if (isDebugRenderEnabled) {
            debugRenderer.render(world, camera.combined);
        }
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
        camera.update();
        cameraUi.update();
        viewportUi.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean keyDown(int keycode) {

        if (keycode == Input.Keys.C) {
            camera.zoom = 1.0f;
        }

        if (keycode == Input.Keys.PLUS) {
            camera.zoom -= zoomLevel;
        }
        if (keycode == Input.Keys.MINUS) {
            camera.zoom += zoomLevel;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        FocusManager.resetFocus(stage);
        if (button == Input.Buttons.MIDDLE) {
            canCameraDrag = true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.MIDDLE) {
            canCameraDrag = false;

        }
        return false;
    }

    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (canCameraDrag) {
            float x = Gdx.input.getDeltaX();
            float y = Gdx.input.getDeltaY();
            camera.translate(-x * (camera.zoom * 0.02f), y * (camera.zoom * 0.02f));
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {


        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (amountY == 1) {
            camera.zoom += zoomLevel;
        } else if (amountY == -1) {
            camera.zoom -= zoomLevel;
        }

        return false;
    }


    private void loadPanels() {
        float height = 0.10f;

        editModePanel = new EditModePanel(this);
        objectPanel = new ObjectPanel(this);
        lightsPanel = new LightsPanel(this);
        materialPanel = new MaterialPanel(this);

        float width = objectPanel.getWidth();

        EntityTreeWindow entityTreeWindow = new EntityTreeWindow(world, this);
        SpriteTreeWindow spriteTreeWindow = new SpriteTreeWindow(this);

        rightTable.add(editModePanel).top().minHeight(stage.getHeight() * height).minWidth(width);
        rightTable.row();

        rightTable.add(materialPanel).fillX().top();
        rightTable.row();

        rightTable.add(objectPanel).fillX().top().minHeight(stage.getHeight() * height);
        rightTable.row();

        rightTable.add(lightsPanel).fillX().top().minHeight(stage.getHeight() * height);
        rightTable.row();

        rightTable.add(spriteTreeWindow).fillX().top().minHeight(stage.getHeight() * 0.4f);
        rightTable.row();



        stage.addActor(entityTreeWindow);

        im.addProcessor(entityTreeWindow);
        im.addProcessor(spriteTreeWindow);

        rightTable.add().expandY();
        im.addProcessor(objectPanel);
        im.addProcessor(materialPanel);
        im.addProcessor(lightsPanel);
        setCameraOnMapCenter();
    }

    public void setMaterialLayerSelected() {
        isMaterialLayerSelected = !isMaterialLayerSelected;
        materialPanel.disableButtons();
        isObjectLayerSelected = false;
        isLightsLayerSelected = false;
        isSpriteLayerSelected = false;
    }

    public void setObjectLayerSelected() {
        isObjectLayerSelected = !isObjectLayerSelected;
        materialPanel.disableButtons();
        isMaterialLayerSelected = false;
        isLightsLayerSelected = false;
        isSpriteLayerSelected = false;
    }

    public void setSpriteLayerSelected() {
        isMaterialLayerSelected = false;
        materialPanel.disableButtons();
        isObjectLayerSelected = false;
        isLightsLayerSelected = false;
        isSpriteLayerSelected = !isSpriteLayerSelected;
    }

    public void setLightsLayerSelected() {
        isMaterialLayerSelected = false;
        materialPanel.disableButtons();
        isObjectLayerSelected = false;
        isLightsLayerSelected = !isLightsLayerSelected;
        isSpriteLayerSelected = false;
    }

    public boolean isSpriteEditModeSelected() {
        return isSpriteLayerSelected;
    }

    public boolean isObjectEditModeSelected() {
        return isObjectLayerSelected;
    }

    public boolean isMaterialLayerSelected() {
        return isMaterialLayerSelected;
    }

    public RayHandler getRayHandler() {
        return rayHandler;
    }

    public boolean isLightsEditModeSelected() {
        return isLightsLayerSelected;
    }

    public World getWorld() {
        return world;
    }

}
