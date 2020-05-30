package com.cosma.annihilation.Gui.MainMenu;


import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Gui.GuiWindow;
import com.cosma.annihilation.Utils.EntityEngine;
import com.cosma.annihilation.Utils.MenuButton;

public class PlayerMenuWindow extends GuiWindow {

    private Table windowTable;
    private Table buttonTable;
    private TextureAtlas textureAtlas;
    private InventoryWindow inventoryWindow;
    private OptionsWindow optionsWindow;
    private LootWindow lootWindow;
    private EntityEngine engine;
    private boolean isOpen = false;

    public PlayerMenuWindow(String title, Skin skin, EntityEngine engine) {
        super(title, skin);
        this.engine = engine;
        this.setModal(true);
        textureAtlas = Annihilation.getAssets().get("gfx/atlas/gui_player_menu.atlas", TextureAtlas.class);
        this.background(new TextureRegionDrawable(textureAtlas.findRegion("tablet")));
        this.setWindowSizeToScreenSize();

        createTable();
        createButtons();

        lootWindow = new LootWindow(skin,engine,getWidth());
        inventoryWindow = new InventoryWindow("Inventory:",skin,engine,getWidth());
        optionsWindow = new OptionsWindow("",skin,engine);
    }

    public Table getWindowTable() {
        return windowTable;
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
                clearWindow();
                addWindow(inventoryWindow);
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
                clearWindow();
                addWindow(optionsWindow);
            }
        });

        offButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(actor.getParent().getParent() instanceof PlayerMenuWindow){
                    ((PlayerMenuWindow) actor.getParent().getParent()).close();
                    engine.getPlayerComponent().isPlayerControlEnable = true;
                }
            }
        });
    }

    private void clearWindow(){
        if(windowTable.hasChildren()){
            if(windowTable.getChildren().size >1){
                System.out.println("error");
            }
            Actor window = windowTable.getChildren().first();
            if(window instanceof GuiWindow){
                ((GuiWindow) window).close();
            }
            windowTable.clearChildren();
        }
    }
    private void addWindow(GuiWindow windowToDisplay){
        windowTable.add(windowToDisplay);
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }

    public void openLootWindow(){
        clearWindow();
        addWindow(lootWindow);
        lootWindow.initialize();
    }

    @Override
    public void close() {
        clearWindow();
        super.close();
        isOpen = false;
    }
}
