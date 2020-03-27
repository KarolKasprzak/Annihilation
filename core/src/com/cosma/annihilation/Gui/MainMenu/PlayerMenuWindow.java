package com.cosma.annihilation.Gui.MainMenu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Gui.GuiWindow;
import com.cosma.annihilation.Utils.MenuButton;

public class PlayerMenuWindow extends GuiWindow {

    private Table windowTable;
    private Table buttonTable;
    private TextureAtlas textureAtlas;

    public PlayerMenuWindow(String title, Skin skin) {
        super(title, skin);
        textureAtlas = Annihilation.getAssets().get("gfx/atlas/gui_player_menu.atlas", TextureAtlas.class);
        this.background(new TextureRegionDrawable(textureAtlas.findRegion("tablet")));
        this.setWindowSize();

        createTable();
        createButtons();
        windowTable.debug();
        buttonTable.debugAll();

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



        MenuButton playerStatusButton = new MenuButton(textureAtlas.findRegion("hea_button"),textureAtlas.findRegion("hea_button_press"));
        MenuButton offButton = new MenuButton(textureAtlas.findRegion("off_button"),textureAtlas.findRegion("off_button_press"));

        buttonTable.add(playerStatusButton).size(size,size);
        buttonTable.add(offButton).size(size,size);



        offButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(actor.getParent().getParent() instanceof PlayerMenuWindow){
                    ((PlayerMenuWindow) actor.getParent().getParent()).close();
                }

            }
        });
    }






}
