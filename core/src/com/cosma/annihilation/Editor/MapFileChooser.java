package com.cosma.annihilation.Editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.cosma.annihilation.Editor.CosmaMap.GameMap;
import com.cosma.annihilation.Screens.MapEditor;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

public class MapFileChooser extends VisWindow {


    public MapFileChooser(MapEditor mapEditor) {
        super("Load map:");
        addCloseButton();
        centerWindow();
        FileHandle fileHandle = Gdx.files.local("map");
        for(FileHandle file: fileHandle.list(".map")){
            VisTextButton button = new VisTextButton(file.name());
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    mapEditor.loadMap("map/"+button.getText());
                    close();
                }
            });
            add(button);
            row();
        }
        pack();


    }
}
