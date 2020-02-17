package com.cosma.annihilation.Gui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Utils.Util;
import com.cosma.annihilation.World.WorldBuilder;

public class GameMainMenuWindow extends Window {

    private Skin skin;
    private PlayerInventoryWindow playerInventoryWindow;
    private OptionsMenuWindow optionsMenuWindow;
    private Table leftTable;
    private Table rightTable;
    private Table screenTable;
    private Button exitButton;
    private Button inventoryButton;
    private Button menuButton;
    private Button characterButton;
    private Engine engine;
    private WorldBuilder worldBuilder;

    public GameMainMenuWindow(String title, Skin skin, Engine engine, float x, float y, World world, WorldBuilder worldBuilder) {
        super(title, skin);

        this.worldBuilder = worldBuilder;
        this.skin = skin;
        this.setVisible(false);
        this.engine = engine;
        this.getTitleLabel().setColor(0, 82, 0, 255);
        this.setSize(x, y);
        this.setMovable(false);
        this.setFillParent(true);
        this.background(new TextureRegionDrawable(new TextureRegion(Annihilation.getAssets().get("gfx/interface/tablet.png",Texture.class))));

        leftTable = new Table();
        rightTable = new Table();
        screenTable = new Table();

        playerInventoryWindow = new PlayerInventoryWindow("", skin, engine);
        playerInventoryWindow.setVisible(false);

        optionsMenuWindow = new OptionsMenuWindow("", skin, this);
        optionsMenuWindow.setVisible(false);

        createButtons(this);

        System.out.println(Gdx.graphics.getDensity());
        rightTable.add(inventoryButton).size(Util.setButtonWidth(1.3f), Util.setButtonHeight(1.8f)).pad( Math.round(Gdx.graphics.getDensity() * 12) );
        rightTable.row();
        rightTable.add(characterButton).size(Util.setButtonWidth(1.3f), Util.setButtonHeight(1.8f)).pad( Math.round(Gdx.graphics.getDensity() * 12) );
        rightTable.row();
        rightTable.add(menuButton).size(Util.setButtonWidth(1.3f), Util.setButtonHeight(1.8f)).pad( Math.round(Gdx.graphics.getDensity() * 12) );
        rightTable.row();
        rightTable.add(exitButton).size(Util.setButtonWidth(1.3f), Util.setButtonHeight(1.8f)).pad( Math.round(Gdx.graphics.getDensity() * 12) );

        leftTable.add(screenTable).center().size(this.getWidth() * 0.7f, this.getHeight() * 0.8f);

        this.add(leftTable).center().size(this.getWidth() * 0.82f, this.getHeight());
        this.add(rightTable).center().size(this.getWidth() * 0.18f, this.getHeight());

    }

    private void createButtons(final GameMainMenuWindow menu) {
        exitButton = new Button(skin);
        exitButton.getStyle().up = new TextureRegionDrawable(new TextureRegion(Annihilation.getAssets().get("gfx/interface/table_button.png",Texture.class)));
        exitButton.getStyle().down = new TextureRegionDrawable(new TextureRegion(Annihilation.getAssets().get("gfx/interface/table_button_pressed.png",Texture.class)));

        menuButton = new Button(skin);

        inventoryButton = new Button(skin);
        inventoryButton.getStyle().up = new TextureRegionDrawable(new TextureRegion(Annihilation.getAssets().get("gfx/interface/table_button1.png",Texture.class)));
//        inventoryButton.getStyle().up = new TextureRegionDrawable(new TextureRegion (Annihilation.getAssets().get(GfxAssetDescriptors.gui_button)));
//        inventoryButton.getStyle().down = new TextureRegionDrawable(new TextureRegion (Annihilation.getAssets().get(GfxAssetDescriptors.gui_button_down)));

//        inventoryButton.getImage().setFillParent(true);


        characterButton = new Button( skin);


        buttonsController();
    }

    void saveGame() {
          System.out.println("save");
//        playerInventoryWindow.saveInventory(engine);
          worldBuilder.saveMap(false);
    }


    void autoSave() {
        //TODO
    }

    void loadGame() {
        System.out.println("load");
        worldBuilder.loadMap();
        playerInventoryWindow.loadInventory(engine);

    }


    private void buttonsController() {
        final GameMainMenuWindow menu = this;

        menuButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                clearAndAddWindow(optionsMenuWindow);
                return true;
            }
        });

        exitButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (screenTable.getChildren().contains(playerInventoryWindow, true)) {
                    playerInventoryWindow.saveInventory(engine);
                }
               screenTable.clearChildren();
                menu.setVisible(false);
                return true;
            }
        });

        inventoryButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                clearAndAddWindow(playerInventoryWindow);
                playerInventoryWindow.loadInventory(engine);
                return true;
            }
        });
    }

    private void clearAndAddWindow(Window window) {
        if (leftTable.getChildren().contains(playerInventoryWindow, true)) {
            playerInventoryWindow.saveInventory(engine);
        }
        screenTable.clearChildren();
        screenTable.add(window).size(this.getWidth() * 0.7f, this.getHeight() * 0.7f);
        window.setVisible(true);

    }


    private void setWindowSize(Window window) {
        window.setSize(this.getWidth() * 0.9f, this.getHeight() * 0.7f);
        window.setFillParent(true);
    }
}
