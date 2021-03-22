package com.cosma.annihilation.Editor.CosmaMap;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;

public class AmbientLightsWindow extends VisWindow {


    private Color ambientColor;
    private ColorPicker picker;


    public AmbientLightsWindow(GameMap map) {
        super("Ambient light set.");
        addCloseButton();

        picker = new ColorPicker("ambient color picker", new ColorPickerAdapter() {
            @Override
            public void finished(Color newColor) {
                ambientColor = newColor;
            }
        });

        picker.setColor(map.getAmbientColor());
        ambientColor = map.getAmbientColor();


        VisTextButton colorButton = new VisTextButton("ambient color");
        colorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getStage().addActor(picker.fadeIn());
            }
        });




        VisTextButton saveButton = new VisTextButton("save");
        VisTextButton cancelButton = new VisTextButton("cancel");


        add(colorButton);
        row();
        add(cancelButton);
        add(saveButton);

        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                map.setAmbientColor(ambientColor);
                close();
            }
        });
        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                close();
            }
        });

        pack();
        setMovable(false);
        setModal(true);
        setResizable(false);
        setCenterOnAdd(true);
    }

    @Override
    protected void close() {
        super.close();
//        this.remove();

    }
}
