package com.cosma.annihilation.Editor;


import box2dLight.ConeLight;
import box2dLight.Light;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorLights.MapConeLight;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorLights.MapLight;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import com.kotcrab.vis.ui.widget.spinner.SimpleFloatSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;

public class LightsAdvWindow extends VisWindow{

    private MapLight selectedLight;
    private Light selectedBox2dLight;
    private VisTable layersTable;
    private Color selectedColor;
    private final Drawable white = VisUI.getSkin().getDrawable("white");
    private ColorPicker picker;

    public LightsAdvWindow(final MapLight selectedLight, final Light selectedBox2dLight) {
        super("Adv. light set.");
        addCloseButton();

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

        final Spinner distanceSpinner = new Spinner("distance", new SimpleFloatSpinnerModel(selectedLight.getLightDistance(), 1f, 45f, 0.5f, 2));
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
                selectedLight.setLightDistance(((SimpleFloatSpinnerModel) distanceSpinner.getModel()).getValue());
                selectedBox2dLight.setDistance(((SimpleFloatSpinnerModel) distanceSpinner.getModel()).getValue());
            }
        });

        final Spinner softDistanceSpinner = new Spinner("soft dist.", new SimpleFloatSpinnerModel(selectedLight.getSoftLength(), 0.1f, 10f, 0.1f, 2));
        softDistanceSpinner.getTextField().setFocusBorderEnabled(false);
        softDistanceSpinner.getTextField().addListener(new FocusListener() {
            @Override
            public void scrollFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                super.scrollFocusChanged(event, actor, focused);
                if(focused){
                    getStage().setScrollFocus(null);
                }
            }
        });
        softDistanceSpinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedLight.setSoftLength(((SimpleFloatSpinnerModel) softDistanceSpinner.getModel()).getValue());
                selectedBox2dLight.setSoftnessLength(((SimpleFloatSpinnerModel) softDistanceSpinner.getModel()).getValue());
            }
        });

        if(selectedLight instanceof MapConeLight){
            final Spinner degreeSpinner = new Spinner("degree", new SimpleFloatSpinnerModel(((MapConeLight) selectedLight).getConeDegree(), 1f, 360f, 1f, 1));
            degreeSpinner.getTextField().setFocusBorderEnabled(false);
            degreeSpinner.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    ((MapConeLight) selectedLight).setConeDegree(((SimpleFloatSpinnerModel) degreeSpinner.getModel()).getValue());
                    ((ConeLight) selectedBox2dLight).setConeDegree(((SimpleFloatSpinnerModel) degreeSpinner.getModel()).getValue());
                }
            });
            degreeSpinner.getTextField().addListener(new FocusListener() {
                @Override
                public void scrollFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                    super.scrollFocusChanged(event, actor, focused);
                    if(focused){
                        getStage().setScrollFocus(null);
                    }
                }
            });

            final Spinner directSpinner = new Spinner("direct", new SimpleFloatSpinnerModel(((MapConeLight) selectedLight).getDirection(), 1f, 360f, 1f, 1));
            directSpinner.getTextField().setFocusBorderEnabled(false);
            directSpinner.getTextField().addListener(new FocusListener() {
                @Override
                public void scrollFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                    super.scrollFocusChanged(event, actor, focused);
                    if(focused){
                        getStage().setScrollFocus(null);
                    }
                }
            });
            directSpinner.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    ((MapConeLight) selectedLight).setDirection(((SimpleFloatSpinnerModel) directSpinner.getModel()).getValue());
                    ((ConeLight) selectedBox2dLight).setDirection(((SimpleFloatSpinnerModel) directSpinner.getModel()).getValue());
                }
            });
            add(degreeSpinner);
            add(directSpinner);
        }

        VisTextButton saveButton = new VisTextButton("save");
        VisTextButton cancelButton = new VisTextButton("cancel");

        VisCheckBox staticButton = new VisCheckBox("static");
        if(selectedLight.isStaticLight()){
            staticButton.setChecked(true);
        }
        final VisCheckBox softButton = new VisCheckBox("soft");
        if(selectedLight.isSoftLight()){
            softButton.setChecked(true);
        }

        add(image).top().size(25).center().top().expandX().expandY();
        add(distanceSpinner);
        add(softDistanceSpinner);
        row();
        add(staticButton);
        add(softButton);
        row();
        add(cancelButton);
        add(saveButton);

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
            selectedBox2dLight.setColor(selectedColor);
            close();
            }
        });
        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
             close();
            }
        });

        softButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
            if(softButton.isChecked()){
                selectedLight.setSoftLight(true);
                selectedBox2dLight.setSoft(true);
            }else{
                selectedLight.setSoftLight(false);
                selectedBox2dLight.setSoft(false);
            }
            }
        });

        staticButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

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
