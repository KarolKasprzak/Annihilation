package com.cosma.annihilation.Gui.MainMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.cosma.annihilation.Gui.GuiWindow;
import com.cosma.annihilation.Utils.EntityEngine;



public class OptionsWindow extends GuiWindow {
    EntityEngine engine;
    public OptionsWindow(String title, Skin skin, EntityEngine engine) {
        super(title, skin);
        this.engine = engine;
        TextButton exitButton = new TextButton("exit",skin);
        TextButton saveButton = new TextButton("save",skin);
        TextButton loadButton = new TextButton("load",skin);
        add(exitButton).padTop(50);
        row();
        add(saveButton);
        row();
        add(loadButton);
        row();


        Dialog dialog = new Dialog("", skin, "dialog");
        dialog.text("game saved");
        dialog.button("ok", true);

        loadButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                engine.loadGame();
            }
        });

       saveButton.addListener(new ChangeListener() {
           @Override
           public void changed(ChangeEvent event, Actor actor) {
           dialog.show(getStage());
           engine.saveGame();
           }
       });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

    }
}
