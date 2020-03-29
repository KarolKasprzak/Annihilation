package com.cosma.annihilation.Gui.MainMenu;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Gui.GuiWindow;
import com.cosma.annihilation.Systems.PlayerControlSystem;
import com.cosma.annihilation.Utils.EntityEngine;
import com.cosma.annihilation.Utils.MenuButton;

public class PlayerMenuWindow extends GuiWindow {

    private Table windowTable;
    private Table buttonTable;
    private TextureAtlas textureAtlas;
    private InventoryWindow inventoryWindow;
    private OptionsWindow optionsWindow;
    private Engine engine;

    public PlayerMenuWindow(String title, Skin skin, Engine engine) {
        super(title, skin);
        this.engine = engine;
        textureAtlas = Annihilation.getAssets().get("gfx/atlas/gui_player_menu.atlas", TextureAtlas.class);
        this.background(new TextureRegionDrawable(textureAtlas.findRegion("tablet")));
        this.setWindowSizeToScreenSize();

        createTable();
        createButtons();
//        windowTable.debug();
//        buttonTable.debugAll();

        inventoryWindow = new InventoryWindow("Inventory",skin,engine,getWidth());
        inventoryWindow.addCloseButton();

        optionsWindow = new OptionsWindow("Options",skin, (EntityEngine) engine);
    }

    private void createTable(){
        windowTable = new Table();
        buttonTable = new Table();
        add(windowTable).center().size(getWidth()*0.8f,getHeight()*0.7f).padTop(getHeight()*0.15f);
        row();
        add(buttonTable).center().size(getWidth()*0.9f,getHeight()*0.1f).padBottom(getHeight()*0.05f).padTop(getHeight()*0.08f);
    }
    private void createButtons(){

        float size = getHeight()*0.0875f;
        float pad = getHeight()*0.02f;
        MenuButton playerStatusButton = new MenuButton(textureAtlas.findRegion("hea_button"),textureAtlas.findRegion("hea_button_press"));
        MenuButton offButton = new MenuButton(textureAtlas.findRegion("off_button"),textureAtlas.findRegion("off_button_press"));
        MenuButton inventoryButton = new MenuButton(textureAtlas.findRegion("inv_button"),textureAtlas.findRegion("off_button_press"));
        MenuButton diaryButton = new MenuButton(textureAtlas.findRegion("diary_button"),textureAtlas.findRegion("off_button_press"));
        MenuButton settingsButton = new MenuButton(textureAtlas.findRegion("sett_button"),textureAtlas.findRegion("off_button_press"));

        buttonTable.add(inventoryButton).size(size,size).pad(pad);
        buttonTable.add(playerStatusButton).size(size,size).pad(pad);
        buttonTable.add(diaryButton).size(size,size).pad(pad);
        buttonTable.add(settingsButton).size(size,size).pad(pad);
        buttonTable.add(offButton).size(size,size).pad(pad);


        inventoryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clearAndAddWindow(inventoryWindow);
                inventoryWindow.loadInventory();
            }
        });

        playerStatusButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

            }
        });

        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clearAndAddWindow(optionsWindow);
            }
        });

        offButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(actor.getParent().getParent() instanceof PlayerMenuWindow){
                    ((PlayerMenuWindow) actor.getParent().getParent()).close();
                    engine.getSystem(PlayerControlSystem.class).setPlayerControlAvailable(true);
                }
            }
        });
    }

    private void clearAndAddWindow(GuiWindow windowToDisplay){
        if(windowTable.hasChildren()){
            if(windowTable.getChildren().first() instanceof InventoryWindow){
                inventoryWindow.saveInventory();
            }
            windowTable.clearChildren();
        }
        windowTable.add(windowToDisplay);
    }

    @Override
    public void close() {
        super.close();
        windowTable.clearChildren();
        inventoryWindow.saveInventory();
    }
}
