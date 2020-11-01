package com.cosma.annihilation.Editor.CosmaMap.CosmaEditorComponentsWindows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Array;
import com.cosma.annihilation.Components.ActionComponent;
import com.cosma.annihilation.Components.GateComponent;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.Utils.Enums.EntityAction;
import com.cosma.annihilation.Utils.Util;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.spinner.SimpleFloatSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;

public class EditGateComponent extends VisWindow {


    public EditGateComponent(GateComponent gateComponent) {

        super("GateComponent:");

        final Spinner distanceSpinner = new Spinner("moveDistance", new SimpleFloatSpinnerModel(gateComponent.moveDistance, 0.1f, 5f, 0.1f, 2));
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

        VisTextField gateNameField = new VisTextField();
        if (gateComponent.gateName != null) {
            gateNameField.setText(gateComponent.gateName);
        }

        VisTextButton saveButton = new VisTextButton("save");
        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gateComponent.gateName = gateNameField.getText();
                gateComponent.moveDistance = Util.roundFloat(((SimpleFloatSpinnerModel) distanceSpinner.getModel()).getValue(),2);
                close();
            }
        });

        add(gateNameField).pad(10);
        row();
        add(distanceSpinner).pad(10);
        row();
        add(saveButton).width(150).pad(10);
        row();
        pack();
        addCloseButton();
        setSize(getWidth(), getHeight() * 1.3f);
        setCenterOnAdd(true);

    }
}
