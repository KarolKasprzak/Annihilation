package com.cosma.annihilation.Editor.CosmaMap.CosmaEditorComponentsWindows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.cosma.annihilation.Components.ActionComponent;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.Utils.Enums.EntityAction;
import com.kotcrab.vis.ui.widget.*;

public class EditActionComponent extends VisWindow {
    private boolean waitForClick = false;
    private ActionComponent actionComponent;
    private OrthographicCamera camera;


    @Override
    public void act(float delta) {
        super.act(delta);
        if (waitForClick) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                Vector3 tempCoords = new Vector3();
                tempCoords.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(tempCoords);
                actionComponent.actionTargetPosition = new Vector2(tempCoords.x, tempCoords.y);
                waitForClick = false;
            }
        }
    }

    public EditActionComponent(ActionComponent actionComponent, Entity entity, OrthographicCamera camera) {

        super("");
        this.actionComponent = actionComponent;
        this.camera = camera;

        VisLabel targetLabel = new VisLabel("Target: ");
        VisTextField targetTextField = new VisTextField();
        if (actionComponent.actionTargetName != null) {
            targetTextField.setText(actionComponent.actionTargetName);
        }
        VisTextButton saveButton = new VisTextButton("save");
        Array<String> actionList = new Array<>();
        for (EntityAction action : EntityAction.values()) {
            actionList.add(action.toString());
        }
        VisSelectBox<String> actionSelectBox = new VisSelectBox<>();
        actionSelectBox.setItems(actionList);
        if(actionComponent.action != null){
            actionSelectBox.setSelected(actionComponent.action.toString());
        }

        VisLabel actionLabel = new VisLabel("action: ");

        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                actionComponent.actionTargetName = targetTextField.getText();
                if (!actionSelectBox.getSelected().equals("NOTHING")) {
                    actionComponent.action = EntityAction.valueOf(actionSelectBox.getSelected());
                }
                close();
            }
        });

        VisTextButton setActionTargetButton = new VisTextButton("set target position");

        setActionTargetButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                waitForClick = true;
            }
        });

        VisTextButton resetTarget = new VisTextButton("reset target");

        resetTarget.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                actionComponent.actionTargetPosition = null;
            }
        });

        add(targetLabel).pad(10);
        add(targetTextField).width(150);
        row();
        add(actionLabel).pad(10);
        add(actionSelectBox).pad(10);
        row();
        add(setActionTargetButton).pad(10);
        add(resetTarget).pad(10);
        row();
        add(saveButton).pad(10);
        pack();
        addCloseButton();
        setSize(getWidth(), getHeight() * 1.3f);
        setCenterOnAdd(true);

    }
}
