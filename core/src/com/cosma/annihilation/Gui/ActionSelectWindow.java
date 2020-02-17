package com.cosma.annihilation.Gui;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.cosma.annihilation.Utils.StateManager;


public class ActionSelectWindow extends Window {
    private Skin skin;
    private List list;
    public ActionSelectWindow(String title, Skin skin) {
        super(title, skin);
        this.skin = skin;
        list = new List(skin);
        list.setDebug(true);
//        list.setFillParent(true);

        Array<Label> labels = new Array<Label>();
        Label act1 = new Label("action 1", skin);
        act1.setText("act");
        Label act2 = new Label("action 2", skin);
        labels.add(act1,act2);
        list.setItems(labels);

        this.add(list);

    }

}

