package com.cosma.annihilation.Gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Utils.Util;

class OptionsMenuWindow extends Window {
    private GameMainMenuWindow gameMainMenuWindow;
    private TextButton saveButton;
    private TextButton loadButton;
    private TextButton exitButton;
    private TextButton pauseButton;
    private Skin skin;

    OptionsMenuWindow(String title, Skin skin, GameMainMenuWindow gameMainMenuWindow) {
        super(title, skin);
        this.skin = skin;
        this.gameMainMenuWindow = gameMainMenuWindow;

//        this.debugAll();

        this.background(new TextureRegionDrawable(new TextureRegion(Annihilation.getAssets().get("gfx/interface/clear.png",Texture.class))));
        addButtons();
        addAction();
    }

    private void addButtons(){

        saveButton = new TextButton("save",skin);
        Util.setButtonColor(saveButton);
        this.add(saveButton).size(Util.setButtonWidth(1.8f),Util.setButtonHeight(1.8f));
        this.row();

        loadButton = new TextButton("load",skin);
        Util.setButtonColor(loadButton);
        this.add(loadButton).size(Util.setButtonWidth(1.8f),Util.setButtonHeight(1.8f));
        this.row();

        exitButton = new TextButton("Exit game",skin);
        Util.setButtonColor(exitButton);
        this.add(exitButton).size(Util.setButtonWidth(1.8f),Util.setButtonHeight(1.8f));
        this.row();
    }

    private void addAction(){

        saveButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                gameMainMenuWindow.saveGame();
                return true;
            }
        });

        loadButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                gameMainMenuWindow.loadGame();
                return true;
            }
        });

        exitButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
                return true;
            }
        });
    }
}



