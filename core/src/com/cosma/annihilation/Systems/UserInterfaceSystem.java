package com.cosma.annihilation.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Components.PlayerComponent;
import com.cosma.annihilation.EntityEngine.core.ComponentMapper;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.EntityEngine.core.Family;
import com.cosma.annihilation.EntityEngine.signals.Listener;
import com.cosma.annihilation.EntityEngine.signals.Signal;
import com.cosma.annihilation.EntityEngine.systems.IteratingSystem;
import com.cosma.annihilation.Gui.MainMenu.AmmoIndicatorWidget;
import com.cosma.annihilation.Gui.MainMenu.PlayerMenuWindow;
import com.cosma.annihilation.Utils.Constants;
import com.cosma.annihilation.Utils.EntityEngine;
import com.cosma.annihilation.Utils.Enums.GameEvent;


public class UserInterfaceSystem extends IteratingSystem implements Listener<GameEvent> {

    private Stage stage;
//    private Label fpsLabel, onGround, canJump;
    private PlayerMenuWindow playerMainMenu;
    private ShaderProgram shader;
    private FrameBuffer fbo;
    private ComponentMapper<PlayerComponent> playerMapper;
    private Image weaponIcon;
    private AmmoIndicatorWidget ammoIndicatorWidget;
    private Table coreTable;

    public UserInterfaceSystem(EntityEngine engine) {
        super(Family.all(PlayerComponent.class).get(), Constants.USER_INTERFACE);

        playerMapper = ComponentMapper.getFor(PlayerComponent.class);

        //shader
        String vertexShader = Gdx.files.internal("shaders/scan_ver.glsl").readString();
        String fragmentShader = Gdx.files.internal("shaders/scan_frag.glsl").readString();
        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(vertexShader, fragmentShader);
        fbo = new FrameBuffer(Pixmap.Format.RGB888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        //shader end

        Skin skin = Annihilation.getAssets().get("gfx/interface/skin/skin.json", Skin.class);
        Camera camera = new OrthographicCamera();
        camera.update();
        Viewport viewport = new ScreenViewport();
        stage = new Stage(viewport);
        stage.getViewport().apply(true);

        coreTable = new Table();
        coreTable.setDebug(false);
        coreTable.setFillParent(true);
        stage.addActor(coreTable);

        playerMainMenu = new PlayerMenuWindow("", skin, engine);
//        fpsLabel = new Label("", skin);
//        onGround = new Label("", skin);
//        canJump = new Label("", skin);

        weaponIcon = new Image();
        ammoIndicatorWidget = new AmmoIndicatorWidget();
        float pad = getStage().getHeight()*0.01f;
        coreTable.add(weaponIcon).left().bottom().expandY().prefSize(64).maxSize(100).minSize(32).padBottom(pad).padLeft(pad);
        coreTable.add(ammoIndicatorWidget).left().bottom().expandX().padBottom(pad);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        stage.getViewport().apply(true);
        Batch batch = stage.getBatch();
        stage.act(deltaTime);
        fbo.begin();
        stage.draw();
        fbo.end();
        stage.draw();
        if (playerMainMenu.isOpen()) {
            Texture texture = fbo.getColorBufferTexture();
            int x = (int) (playerMainMenu.getX() + (playerMainMenu.getWindowTable().getX()));
            int y = (int) (playerMainMenu.getY() + (playerMainMenu.getWindowTable().getY()));
            int w = (int) playerMainMenu.getWindowTable().getWidth();
            int h = (int) playerMainMenu.getWindowTable().getHeight();
            TextureRegion textureRegion = new TextureRegion(texture, x, y, w, h);
            textureRegion.flip(false, true);
            batch.setShader(shader);
            batch.begin();
            batch.draw(textureRegion, x, y, textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
            batch.setShader(null);
            batch.end();
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerComponent playerComponent = playerMapper.get(entity);
        weaponIcon.setDrawable(playerComponent.activeWeapon.getDrawable());
        ammoIndicatorWidget.update(playerComponent);
        coreTable.getCell(ammoIndicatorWidget).height(ammoIndicatorWidget.getHeight());
    }


    public void openPlayerMenu(boolean openLootMenu) {
        PlayerComponent playerComponent = ((EntityEngine) getEngine()).getPlayerEntity().getComponent(PlayerComponent.class);
        if (stage.getActors().contains(playerMainMenu, true)) {
            playerMainMenu.close();
            playerMainMenu.setOpen(false);
            playerComponent.isPlayerControlEnable = true;
        } else {
            playerComponent.isPlayerControlEnable = false;
            stage.addActor(playerMainMenu);
            playerMainMenu.setOpen(true);
            if (openLootMenu) {
                playerMainMenu.openLootWindow();
            }
            playerMainMenu.moveToCenter();
        }
    }

    public void resizeHUD(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public Stage getStage() {
        return stage;
    }

    @Override
    public void receive(Signal<GameEvent> signal, GameEvent event) {
        if (event == GameEvent.OPEN_MENU) {
            openPlayerMenu(false);
        }
    }
}
