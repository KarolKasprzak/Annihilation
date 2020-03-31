package com.cosma.annihilation.Systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Components.DialogueComponent;
import com.cosma.annihilation.Components.HealthComponent;
import com.cosma.annihilation.Components.PlayerComponent;
import com.cosma.annihilation.Gui.*;
import com.cosma.annihilation.Gui.MainMenu.PlayerMenuWindow;
import com.cosma.annihilation.Utils.Constants;
import com.cosma.annihilation.Utils.Enums.GameEvent;
import com.cosma.annihilation.Utils.Util;
import com.cosma.annihilation.World.WorldBuilder;

public class UserInterfaceSystem extends IteratingSystem implements Listener<GameEvent> {

    private Stage stage;
    private Label fpsLabel;
    private ContainerWindow containerWindow;
    private Image playerHealthStatusIcon;
    private PlayerComponent playerComponent;
    private ComponentMapper<PlayerComponent> playerMapper;
    private BitmapFont font;
    private DialogueWindow dialogueWindow;
    private Skin skin;
    private LootWindow lootWindow;
    private PlayerMenuWindow playerMenuWindow;
    public UserInterfaceSystem(Engine engine, World world, WorldBuilder worldBuilder) {
        super(Family.all(PlayerComponent.class).get(), Constants.USER_INTERFACE);

        playerMapper = ComponentMapper.getFor(PlayerComponent.class);

        skin = Annihilation.getAssets().get("gfx/interface/skin/skin.json", Skin.class);
        Camera camera = new OrthographicCamera();
        camera.update();
        Viewport viewport = new ScreenViewport();
        stage = new Stage(viewport);
        stage.getViewport().apply(true);

        Table coreTable = new Table();
        coreTable.setDebug(false);
        coreTable.setFillParent(true);
        stage.addActor(coreTable);

        Signal<GameEvent> signal = new Signal<GameEvent>();

        dialogueWindow = new DialogueWindow(skin, engine);

        lootWindow = new LootWindow(skin,engine);
        playerMenuWindow = new PlayerMenuWindow("",skin,engine);
        fpsLabel = new Label("", skin);
        playerHealthStatusIcon = new Image(Annihilation.getAssets().get("gfx/textures/player_health.png", Texture.class));

        coreTable.add(fpsLabel).left().top().expandX().expandY();
        coreTable.add(playerHealthStatusIcon).top();
        font = new BitmapFont();
        font.setColor(Color.RED);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        fpsLabel.setText(Float.toString(Gdx.graphics.getFramesPerSecond()));
        stage.act(deltaTime);
        stage.draw();

    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        playerMapper.get(entity);
        playerComponent = playerMapper.get(entity);
        if (entity.getComponent(HealthComponent.class).hp < entity.getComponent(HealthComponent.class).maxHP / 2) {
            playerHealthStatusIcon.setDrawable(new TextureRegionDrawable(new TextureRegion(Annihilation.getAssets().get("gfx/textures/player_health_bad.png", Texture.class))));
        }

    }


    void showLootWindow(Entity entity) {
        if(!lootWindow.isOpen()){
            System.out.println("open");
            stage.addActor(lootWindow);
            lootWindow.open(entity);
        }

    }

    void showDialogWindow(Entity entity) {

      if(!stage.getActors().contains(dialogueWindow,true)){
          if (Util.hasComponent(entity, DialogueComponent.class) && playerComponent.canPerformAction) {
              playerComponent.canPerformAction = false;
              playerComponent.canMoveOnSide = false;
              DialogueComponent dialogueComponent = entity.getComponent(DialogueComponent.class);
              stage.addActor(dialogueWindow);
              dialogueWindow.setVisible(true);
              dialogueWindow.displayDialogue(dialogueComponent);
          }
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
        switch (event) {
            case OPEN_MENU:
                    if(stage.getActors().contains(playerMenuWindow,true)){
                        playerMenuWindow.close();
                        getEngine().getSystem(PlayerControlSystem.class).setPlayerControlAvailable(true);
                    }else{
                        stage.addActor(playerMenuWindow);
                        playerMenuWindow.moveToCenter();
                    }
                break;
        }
    }
}
