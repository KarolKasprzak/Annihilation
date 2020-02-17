package com.cosma.annihilation.Gui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Components.ContainerComponent;
import com.cosma.annihilation.Components.PlayerComponent;
import com.cosma.annihilation.Systems.ActionSystem;
import com.cosma.annihilation.Systems.ShootingSystem;
import com.cosma.annihilation.Utils.AssetLoader;
import com.cosma.annihilation.Utils.Enums.GameEvent;
import com.cosma.annihilation.Utils.StateManager;
import com.cosma.annihilation.Utils.Util;

public class Gui implements Screen {
    private Stage stage;
    private Skin skin;

    private Label fpsLabel;
    private Label footContact;
    private Label onground;
    private Label climbing;
    private Label canClimb;
    private Label canJump;
    private Label canClimbDown;
    private Label weaponHidden;
    private Label crouch;

    private String fpsNumber;
    private ImageButton actionButtonRightUp;
    private ImageButton actionButtonRightDown;
    private ImageButton actionButtonRightCenter;
    private ImageButton actionButtonUp;
    private ImageButton actionButtonDown;
    private ImageButton actionButtonLeft;
    private ImageButton actionButtonRight;
    private TextButton debugButton;
    private TextButton menuButton;
    private ProgressBar healthBar;
    private Engine engine;

    private Entity player;
    private PlayerComponent playerState;

    private World world;
    private Signal<GameEvent> signal;
    private Label actionNameDisplayed;
    private ContainerWindow containerWindow;
    private AssetLoader assetLoader;
    private GameMainMenuWindow menuWindow;
    private boolean rightUpButtonPressed = false;

    public Gui(final Engine engine, World world) {
        this.world = world;
        this.engine = engine;
        this.assetLoader = Annihilation.getAssetsLoader();
        skin = assetLoader.manager.get("gfx/interface/uiskin.json", Skin.class);
        signal = new Signal<GameEvent>();
        Camera camera = new OrthographicCamera();
        camera.update();
        Viewport viewport = new ScreenViewport(camera);
        stage = new Stage(viewport);
        viewport.apply(true);

        containerWindow = new ContainerWindow("", skin, 4, engine);
        containerWindow.setSize(Util.setWindowWidth(0.4f), Util.setWindowHeight(0.5f));
        containerWindow.setVisible(false);
        containerWindow.setPosition(Gdx.graphics.getWidth() / 2 - (Util.setWindowWidth(0.4f) / 2), Gdx.graphics.getHeight() / 2 - (Util.setWindowHeight(0.5f) / 2));
        stage.addActor(containerWindow);

        fpsLabel = new Label(fpsNumber, skin);
        climbing = new Label("", skin);
        onground = new Label("", skin);
        footContact = new Label("", skin);
        crouch = new Label("", skin);
        canClimb = new Label("", skin);
        canJump = new Label("", skin);
        canClimbDown = new Label("", skin);
        weaponHidden = new Label("", skin);

        actionNameDisplayed = new Label("", skin);
//        actionButtonRightUp = new ImageButton(new TextureRegionDrawable(new TextureRegion(Annihilation.getAssets().get(OLDAssetDescriptors.gui_buttons).findRegion("button_b"))));
//        actionButtonRightDown = new ImageButton(new TextureRegionDrawable(new TextureRegion(Annihilation.getAssets().get(OLDAssetDescriptors.gui_buttons).findRegion("button_a"))));
//        actionButtonRightCenter = new ImageButton(new TextureRegionDrawable(new TextureRegion(Annihilation.getAssets().get(OLDAssetDescriptors.gui_buttons).findRegion("button_x"))));
//        actionButtonUp = new ImageButton(new TextureRegionDrawable(new TextureRegion(Annihilation.getAssets().get(OLDAssetDescriptors.gui_buttons).findRegion("button_up"))));
//        actionButtonDown = new ImageButton(new TextureRegionDrawable(new TextureRegion(Annihilation.getAssets().get(OLDAssetDescriptors.gui_buttons).findRegion("button_down"))));
//        actionButtonLeft = new ImageButton(new TextureRegionDrawable(new TextureRegion(Annihilation.getAssets().get(OLDAssetDescriptors.gui_buttons).findRegion("button_left"))));
//        actionButtonRight = new ImageButton(new TextureRegionDrawable(new TextureRegion(Annihilation.getAssets().get(OLDAssetDescriptors.gui_buttons).findRegion("button_right"))));

        actionButtonRightUp.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//                player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
//                if (player.getComponent(PlayerComponent.class).isWeaponHidden) {
//                    signal.dispatch(GameEvent.PERFORM_ACTION);
//                } else
//                    signal.dispatch(GameEvent.WEAPON_SHOOT);
                signal.dispatch(GameEvent.ACTION_BUTTON_TOUCH_DOWN);
                signal.dispatch(GameEvent.PERFORM_ACTION);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                   signal.dispatch(GameEvent.ACTION_BUTTON_TOUCH_UP);
            }
        });

        actionButtonRightCenter.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                signal.dispatch(GameEvent.WEAPON_TAKE_OUT);
                return true;
            }
        });
        actionButtonRightDown.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                playerState.goUp = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                playerState.goUp = false;
            }
        });
        actionButtonUp.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                {
                    playerState.goUp = true;
                    System.out.println("work");
                    System.out.println(playerState.goUp);
                    System.out.println(engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first().getComponent(PlayerComponent.class).goUp);
                }
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                playerState.goUp = false;
            }

        });
        actionButtonDown.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (playerState.canClimbDown) {
                    playerState.goDown = true;
                } else {
                    if (playerState.isPlayerCrouch) {
                        playerState.isPlayerCrouch = false;
                    } else
                        playerState.isPlayerCrouch = true;
                }

                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                playerState.goDown = false;
            }

        });
        actionButtonLeft.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                playerState.goLeft = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                playerState.goLeft = false;
            }

        });
        actionButtonRight.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                playerState.goRight = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                playerState.goRight = false;
            }
        });

        createHUD();
        createMenuWindow();
    }

    private void createMenuWindow() {
//        menuWindow = new GameMainMenuWindow("", skin, world, engine, Utilities.setWindowWidth(0.95f), Utilities.setWindowHeight(0.95f), this);
//        menuWindow.setPosition(Gdx.graphics.getWidth() / 2 - (Utilities.setWindowWidth(0.95f) / 2), Gdx.graphics.getHeight() / 2 - (Utilities.setWindowHeight(0.95f) / 2));
//        menuWindow.setMovable(true);
//        menuWindow.setVisible(false);
//        menuWindow.setFillParent(true);
//        stage.addActor(menuWindow);
    }

    /** do it after systems are added to engine */
    public void addSystemsReferences(Engine engine) {
        this.engine = engine;
        signal.add(engine.getSystem(ActionSystem.class));
        signal.add(engine.getSystem(ShootingSystem.class));
        player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
        playerState = player.getComponent(PlayerComponent.class);
    }


    public void showLootWindow(Entity entity) {
        containerWindow.setVisible(true);
        PlayerInventoryWindow.fillInventory(containerWindow.containerSlotsTable, entity.getComponent(ContainerComponent.class).itemLocations, containerWindow.dragAndDrop);
    }

    private void createHUD() {
        Table table = new Table();
        table.center();
        table.setFillParent(true);
        Table bTable = new Table();
        bTable.bottom().left();
        bTable.setFillParent(true);

        stage.addActor(table);
        stage.addActor(bTable);

        menuButton = new TextButton("Menu", skin);
        Util.setButtonColor(menuButton);
        menuButton.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!menuWindow.isVisible()) {
                    menuWindow.setVisible(true);
                }
                return true;
            }
        });
        debugButton = new TextButton("Debug mode ", skin);
        Util.setButtonColor(debugButton);

        debugButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (debugButton.isChecked()) {
                    debugButton.setText("Debug mode - enabled");
                    StateManager.debugMode = true;
                } else {
                    debugButton.setText("Debug mode");
                    StateManager.debugMode = false;
                }
            }

        });

        table.add(debugButton).padTop(10).padLeft(10).left().width(180).height(32);
        table.add(actionNameDisplayed).center().expandX();
        table.add(menuButton).padTop(10).padRight(10).right().width(180).height(32);
        table.row();
        table.add(fpsLabel).padTop(0).padLeft(10).left().width(150).height(32);
        table.row();
        table.add(climbing).padTop(0).padLeft(10).left().width(250).height(32);
        table.row();
        table.add(onground).padTop(0).padLeft(10).left().width(250).height(32);
        table.row();
        table.add(canClimb).padTop(0).padLeft(10).left().width(250).height(32);
        table.row();
        table.add(canJump).padTop(0).padLeft(10).left().width(250).height(32);
        table.row();
        table.add(crouch).padTop(10).padLeft(10).left().width(250).height(32);
        table.row();
        table.add(canClimbDown).padTop(10).padLeft(10).left().width(250).height(32);
        table.row();
        table.add(weaponHidden).padTop(0).padLeft(10).left().width(250).height(32);
        table.row();

        //-------------------Gui button table------------------------------------------------
        bTable.add().width(Util.setButtonWidth(1f)).height(Util.setButtonWidth(1f)).center().pad(10);
        bTable.add(actionButtonUp).width(Util.setButtonWidth(1f)).height(Util.setButtonWidth(1f)).center().pad(10);
        bTable.add().width(Util.setButtonWidth(1f)).height(Util.setButtonWidth(1f)).center().pad(10);
        bTable.add(actionButtonRightUp).expandX().right().padBottom(15).padLeft(15).width(Util.setButtonWidth(1f)).height(Util.setButtonWidth(1f)).padRight(25).colspan(2);
        bTable.row();
        bTable.add(actionButtonLeft).width(Util.setButtonWidth(1f)).height(Util.setButtonWidth(1f)).left().pad(10);
        bTable.add(actionButtonDown).width(Util.setButtonWidth(1f)).height(Util.setButtonWidth(1f)).left().pad(10);
        bTable.add(actionButtonRight).width(Util.setButtonWidth(1f)).height(Util.setButtonWidth(1f)).left().pad(10).expandX();
        bTable.add(actionButtonRightDown).right().padBottom(15).padLeft(15).width(Util.setButtonWidth(1f)).height(Util.setButtonWidth(1f)).padRight(25).expandX();
        bTable.add(actionButtonRightCenter).right().padBottom(15).padLeft(15).width(Util.setButtonWidth(1f)).height(Util.setButtonWidth(1f)).padRight(25);
        table.row();
        table.add(bTable).left().bottom().expandY().padBottom(15).padLeft(15);
    }

    void setPlayerEntity(){
        this.player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
        playerState = player.getComponent(PlayerComponent.class);
    }

    public Stage getStage() {
        return stage;
    }

    @Override
    public void show() {
      System.out.println("gui start");
    }

    @Override
    public void render(float delta) {
        float fpss = Gdx.graphics.getFramesPerSecond();
        fpsNumber = Float.toString(fpss);
        fpsLabel.setText(fpsNumber);
        crouch.setText("isPlayerCrouch " + playerState.isPlayerCrouch);
        climbing.setText("climbing " + playerState.climbing);
        onground.setText("onGround " + playerState.onGround);
        canClimb.setText("canclimb " + playerState.canClimb);
        canClimbDown.setText("canclimbdown " + playerState.canClimbDown);
        canJump.setText("can jump " + playerState.canJump);
        weaponHidden.setText("weapon is hidden " + player.getComponent(PlayerComponent.class).isWeaponHidden);
        fpsLabel.setColor(0, 82, 0, 255);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        stage.dispose();
    }

    public void loadGame() {
        menuWindow.loadGame();
    }


}




