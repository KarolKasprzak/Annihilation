package com.cosma.annihilation.Editor.CosmaMap.CosmaEditorComponentsWindows;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Components.ParallaxComponent;
import com.cosma.annihilation.Components.PhysicsComponent;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kotcrab.vis.ui.widget.spinner.SimpleFloatSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;

public class ParallaxWindow extends VisWindow {



    public ParallaxWindow(ParallaxComponent parallaxComponent, Entity entity) {
        super("Parallax component");

        VisLabel displayWLabel = new VisLabel("Display width: " + parallaxComponent.displayW);

        VisLabel displayHLabel = new VisLabel("Display height: " + parallaxComponent.displayH);

        VisLabel nameLabel = new VisLabel("parallax name: " + parallaxComponent.parallaxName);

        final Spinner displayWSpinner = new Spinner("displayed width:", new SimpleFloatSpinnerModel(parallaxComponent.displayW, 0.1f, 40f, 0.01f, 2));
        displayWSpinner.getTextField().setFocusBorderEnabled(false);
        displayWSpinner.getTextField().addListener(new FocusListener() {
            @Override
            public void scrollFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                super.scrollFocusChanged(event, actor, focused);
                if(focused){
                    getStage().setScrollFocus(null);
                }
            }
        });
        displayWSpinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parallaxComponent.displayW = ((SimpleFloatSpinnerModel) displayWSpinner.getModel()).getValue();
                displayWLabel.setText("Display width: " + parallaxComponent.displayW);
            }
        });

        final Spinner displayHSpinner = new Spinner("displayed height:", new SimpleFloatSpinnerModel(parallaxComponent.displayH, 0.1f, 40f, 0.01f, 2));
        displayHSpinner.getTextField().setFocusBorderEnabled(false);
        displayHSpinner.getTextField().addListener(new FocusListener() {
            @Override
            public void scrollFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                super.scrollFocusChanged(event, actor, focused);
                if(focused){
                    getStage().setScrollFocus(null);
                }
            }
        });
        displayHSpinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parallaxComponent.displayH = ((SimpleFloatSpinnerModel) displayHSpinner.getModel()).getValue();
                displayHLabel.setText("Display height: " + parallaxComponent.displayH);
            }
        });

        VisSelectBox<String> parallaxSelectBox = new VisSelectBox<>();
        parallaxSelectBox.setItems(Annihilation.getAvailableParallaxNames());
        parallaxSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parallaxComponent.parallaxName = parallaxSelectBox.getSelected();
                nameLabel.setText("parallax name: " + parallaxComponent.parallaxName);
                parallaxComponent.textures = Annihilation.getParallax(parallaxComponent.parallaxName);
            }
        });

        VisTextButton setNameButton = new VisTextButton("set");
        setNameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parallaxComponent.parallaxName = parallaxSelectBox.getSelected();
                nameLabel.setText("parallax name: " + parallaxComponent.parallaxName);
                parallaxComponent.textures = Annihilation.getParallax(parallaxComponent.parallaxName);
            }
        });

        this.add(parallaxSelectBox);
        this.add(setNameButton);
        row();
        this.add(displayWSpinner).colspan(2);
        row();
        this.add(displayHSpinner).colspan(2);
        row();
        this.add(displayWLabel).colspan(2);
        row();
        this.add(displayHLabel).colspan(2);
        row();
        this.add(nameLabel).colspan(2);

        setCenterOnAdd(true);
        addCloseButton();
        pack();
        setSize(getWidth()*1.5f, getHeight() * 1.5f);
        setModal(true);
    }
}
