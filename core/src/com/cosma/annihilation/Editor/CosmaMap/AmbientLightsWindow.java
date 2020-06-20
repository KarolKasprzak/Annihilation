package com.cosma.annihilation.Editor.CosmaMap;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.cosma.annihilation.Box2dLight.RayHandler;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import com.kotcrab.vis.ui.widget.spinner.SimpleFloatSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;

public class AmbientLightsWindow extends VisWindow {


    private Color selectedColor;
    private ColorPicker picker;

    public AmbientLightsWindow(GameMap map, RayHandler rayHandler) {
        super("Ambient light set.");
        addCloseButton();

        Drawable white = VisUI.getSkin().getDrawable("white");
        final Image image = new Image(white);
        image.setColor(map.getLightsMapLayer().getAmbientLightColor());
        picker = new ColorPicker("color picker", new ColorPickerAdapter() {
            @Override
            public void finished(Color newColor) {
                selectedColor = newColor;
                image.setColor(newColor);
            }
        });
        picker.setColor(map.getLightsMapLayer().getAmbientLightColor());
        image.setColor(map.getLightsMapLayer().getAmbientLightColor());
        selectedColor = map.getLightsMapLayer().getAmbientLightColor();

        final Spinner distanceSpinner = new Spinner("intensity", new SimpleFloatSpinnerModel(map.getLightsMapLayer().getAmbientLightIntensity(), 0f, 1f, 0.05f, 2));
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


        VisTextButton saveButton = new VisTextButton("save");
        VisTextButton cancelButton = new VisTextButton("cancel");

        add(image).top().size(25).center().top().expandX().expandY();
        add(distanceSpinner);
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
                map.getLightsMapLayer().setAmbientLightColor(selectedColor);
                map.getLightsMapLayer().setAmbientLightIntensity(((SimpleFloatSpinnerModel) distanceSpinner.getModel()).getValue());

                rayHandler.setAmbientLight(map.getLightsMapLayer().getAmbientLightColor());
                rayHandler.setAmbientLight(map.getLightsMapLayer().getAmbientLightIntensity());
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
