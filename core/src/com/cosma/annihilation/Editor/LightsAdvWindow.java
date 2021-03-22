package com.cosma.annihilation.Editor;



import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.cosma.annihilation.Editor.CosmaMap.CosmaLights.Light;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import com.kotcrab.vis.ui.widget.spinner.SimpleFloatSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;

public class LightsAdvWindow extends VisWindow{

    private Color selectedColor;
    private ColorPicker picker;

    public LightsAdvWindow(final Light selectedLight) {
        super("Light settings: ");
        addCloseButton();

        Drawable white = VisUI.getSkin().getDrawable("white");
        final Image image = new Image(white);
        image.setColor(selectedLight.getColor());
        picker = new ColorPicker("color picker", new ColorPickerAdapter() {
            @Override
            public void finished(Color newColor) {
                selectedColor = newColor;
                image.setColor(newColor);
            }
        });
        picker.setColor(selectedLight.getColor());
        image.setColor(selectedLight.getColor());
        selectedColor = selectedLight.getColor();

        final Spinner distanceSpinner = new Spinner("distance", new SimpleFloatSpinnerModel(selectedLight.getLightRadius(), 0.1f, 45f, 0.5f, 2));
        distanceSpinner.getTextField().setFocusBorderEnabled(false);
        distanceSpinner.getTextField().addListener(new FocusListener() {
            @Override
            public void scrollFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                super.scrollFocusChanged(event, actor, focused);
                if(focused){
                    getStage().setScrollFocus(null);
                }
            }
        });
        distanceSpinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedLight.setLightRadius(((SimpleFloatSpinnerModel) distanceSpinner.getModel()).getValue());
            }
        });



//        if(selectedLight instanceof MapConeLight){
//            final Spinner degreeSpinner = new Spinner("degree", new SimpleFloatSpinnerModel(((MapConeLight) selectedLight).getConeDegree(), 1f, 360f, 1f, 1));
//            degreeSpinner.getTextField().setFocusBorderEnabled(false);
//            degreeSpinner.addListener(new ChangeListener() {
//                @Override
//                public void changed(ChangeEvent event, Actor actor) {
//                    ((MapConeLight) selectedLight).setConeDegree(((SimpleFloatSpinnerModel) degreeSpinner.getModel()).getValue());
//                    ((ConeLight) selectedBox2dLight).setConeDegree(((SimpleFloatSpinnerModel) degreeSpinner.getModel()).getValue());
//                }
//            });
//            degreeSpinner.getTextField().addListener(new FocusListener() {
//                @Override
//                public void scrollFocusChanged(FocusEvent event, Actor actor, boolean focused) {
//                    super.scrollFocusChanged(event, actor, focused);
//                    if(focused){
//                        getStage().setScrollFocus(null);
//                    }
//                }
//            });
//
//            final Spinner directSpinner = new Spinner("direct", new SimpleFloatSpinnerModel(((MapConeLight) selectedLight).getDirection(), 1f, 360f, 1f, 1));
//            directSpinner.getTextField().setFocusBorderEnabled(false);
//            directSpinner.getTextField().addListener(new FocusListener() {
//                @Override
//                public void scrollFocusChanged(FocusEvent event, Actor actor, boolean focused) {
//                    super.scrollFocusChanged(event, actor, focused);
//                    if(focused){
//                        getStage().setScrollFocus(null);
//                    }
//                }
//            });
//            directSpinner.addListener(new ChangeListener() {
//                @Override
//                public void changed(ChangeEvent event, Actor actor) {
//                    ((MapConeLight) selectedLight).setDirection(((SimpleFloatSpinnerModel) directSpinner.getModel()).getValue());
//                    ((ConeLight) selectedBox2dLight).setDirection(((SimpleFloatSpinnerModel) directSpinner.getModel()).getValue());
//                }
//            });
//            add(degreeSpinner);
//            add(directSpinner);
//        }

        VisTextButton saveButton = new VisTextButton("save");
        VisTextButton cancelButton = new VisTextButton("cancel");

        final VisCheckBox enabledButton = new VisCheckBox("active");
        if(selectedLight.isActive()){
            enabledButton.setChecked(true);
        }

        //shader Z spinner
        final Spinner lightZSpinner = new Spinner("light Z", new SimpleFloatSpinnerModel(selectedLight.getLightZ(), 0f, 0.9f, 0.01f, 2));
        lightZSpinner.getTextField().setFocusBorderEnabled(false);
        lightZSpinner.getTextField().addListener(new FocusListener() {
            @Override
            public void scrollFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                super.scrollFocusChanged(event, actor, focused);
                if(focused){
                    getStage().setScrollFocus(null);
                }
            }
        });
        lightZSpinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedLight.setLightZ(((SimpleFloatSpinnerModel) lightZSpinner.getModel()).getValue());
            }
        });

        //shader light intensity  spinner
        final Spinner intensitySpinner = new Spinner("light intensity", new SimpleFloatSpinnerModel(selectedLight.getIntensityForShader(), 0f, 2.0f, 0.05f, 2));
        intensitySpinner.getTextField().setFocusBorderEnabled(false);
        intensitySpinner.getTextField().addListener(new FocusListener() {
            @Override
            public void scrollFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                super.scrollFocusChanged(event, actor, focused);
                if(focused){
                    getStage().setScrollFocus(null);
                }
            }
        });
        intensitySpinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedLight.setIntensityForShader(((SimpleFloatSpinnerModel) intensitySpinner.getModel()).getValue());
            }
        });


        add(image).top().size(25).center().top().expandX().expandY();
        add(distanceSpinner);
        row();
        add(enabledButton);
        row();
        add(new VisLabel("Light shader settings: "));
        row();
        add(lightZSpinner);
        add(intensitySpinner);
        row();
        add(cancelButton);
        add(saveButton);


        enabledButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(enabledButton.isChecked()){
                    selectedLight.setActive(true);
                }else{
                    selectedLight.setActive(false);

                }
            }
        });

        image.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                getStage().addActor(picker.fadeIn());
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
            selectedLight.setColor(selectedColor);
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
        setMovable(true);
        setResizable(false);
        setPosition(200,200);
    }

    @Override
    protected void close() {
        super.close();
        this.remove();

    }
}
