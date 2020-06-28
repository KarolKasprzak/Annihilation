package com.cosma.annihilation.Editor.CosmaMap;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.cosma.annihilation.Box2dLight.RayHandler;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import com.kotcrab.vis.ui.widget.spinner.SimpleFloatSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;

public class AmbientLightsWindow extends VisWindow {


    private Color ambientColor;
    private ColorPicker picker;


    public AmbientLightsWindow(GameMap map, RayHandler rayHandler, boolean isShaderAmbient) {
        super("Ambient light set.");
        addCloseButton();

        VisCheckBox useIntensity = new VisCheckBox("use Intensity");


        picker = new ColorPicker("ambient color picker", new ColorPickerAdapter() {
            @Override
            public void finished(Color newColor) {
                ambientColor = newColor;
            }
        });

        picker.setColor(map.getLightsMapLayer().getAmbientLightColor());
        ambientColor = map.getLightsMapLayer().getAmbientLightColor();

        if (isShaderAmbient) {
            picker.setColor(map.getLightsMapLayer().getShaderAmbientLightColor());
            ambientColor = map.getLightsMapLayer().getShaderAmbientLightColor();
        }

        VisTextButton colorButton = new VisTextButton("ambient color");
        colorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getStage().addActor(picker.fadeIn());
            }
        });

        final Spinner distanceSpinner = new Spinner("intensity: ", new SimpleFloatSpinnerModel(map.getLightsMapLayer().getAmbientLightIntensity(), 0f, 1f, 0.05f, 2));
        distanceSpinner.getTextField().setFocusBorderEnabled(false);
        distanceSpinner.getTextField().addListener(new FocusListener() {
            @Override
            public void scrollFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                super.scrollFocusChanged(event, actor, focused);
                if (focused) {
                    getStage().setScrollFocus(null);
                }
            }
        });

        ((SimpleFloatSpinnerModel) distanceSpinner.getModel()).setValue(map.getLightsMapLayer().getAmbientLightIntensity());
        if (isShaderAmbient) {
            ((SimpleFloatSpinnerModel) distanceSpinner.getModel()).setValue(map.getLightsMapLayer().getShaderAmbientLightIntensity());
        }

        VisTextButton saveButton = new VisTextButton("save");
        VisTextButton cancelButton = new VisTextButton("cancel");


        add(colorButton);
        row();
        add(useIntensity);
        row();
        add(distanceSpinner);
        row();
        add(cancelButton);
        add(saveButton);

        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                if (isShaderAmbient) {
                    map.getLightsMapLayer().setShaderAmbientLightColor(ambientColor);
                    map.getLightsMapLayer().setShaderAmbientLightIntensity(((SimpleFloatSpinnerModel) distanceSpinner.getModel()).getValue());
                } else {
                    map.getLightsMapLayer().setAmbientLightColor(ambientColor);
                        map.getLightsMapLayer().setAmbientLightIntensity(((SimpleFloatSpinnerModel) distanceSpinner.getModel()).getValue());
                        if(RayHandler.isDiffuse){
                            rayHandler.setAmbientLight(0);
                        }else{
                            rayHandler.setAmbientLight(map.getLightsMapLayer().getAmbientLightColor());
                            rayHandler.setAmbientLight(map.getLightsMapLayer().getAmbientLightIntensity());
                        }
                }

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
