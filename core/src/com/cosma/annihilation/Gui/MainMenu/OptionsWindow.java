package com.cosma.annihilation.Gui.MainMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Gui.GuiWindow;
import com.cosma.annihilation.Utils.EntityEngine;


public class OptionsWindow extends GuiWindow {
    EntityEngine engine;

    public OptionsWindow(String title, Skin skin, EntityEngine engine) {
        super(title, skin);
        this.engine = engine;

        Label label = new Label(Annihilation.getLocalText("g_options"), skin);

        TextButton exitButton = new TextButton(Annihilation.getLocalText("g_exit"), skin);
        TextButton saveButton = new TextButton(Annihilation.getLocalText("g_save"), skin);
        TextButton loadButton = new TextButton(Annihilation.getLocalText("g_load"), skin);
        add(label).pad(5);
        row();
        add(exitButton).pad(5);
        row();
        add(saveButton).pad(5);
        row();
        add(loadButton).pad(5);
        row();


        loadButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                engine.loadGame();
            }
        });
        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
//           dialog.show(getStage());
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
