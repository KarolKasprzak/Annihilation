package com.cosma.annihilation.Editor;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorObject.MapObject;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisWindow;

public class ObjectEditWindow extends VisWindow {
    public ObjectEditWindow(Body body, MapObject mapObject) {
        super("Object edit: " + mapObject.getName());
        VisLabel bodyUserDate = new VisLabel("Body date: "+body.getUserData());
        row();
        this.add(bodyUserDate);

        int index = 0;
        for(Fixture fixture: body.getFixtureList()){
            VisLabel userDate = new VisLabel("fixture "+index+": "+fixture.getUserData());
            row();
            index ++;
            this.add(userDate);
        }
        row();

        VisTextField bodyTextField = new VisTextField("body userDate");
        this.add(bodyTextField).pad(15);
        row();
        VisTextButton bodySaveButton= new VisTextButton("save body userDat");
        this.add(bodySaveButton).pad(15);;

        bodySaveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                body.setUserData(bodyTextField.getText());
                mapObject.setName(bodyTextField.getText());
                mapObject.setBodyUserDate(bodyTextField.getText());
            }
        });
        row();

        VisTextField textField = new VisTextField("fixture userDate");
        this.add(textField).pad(15);;
        row();

        VisTextButton saveButton= new VisTextButton("save");
        this.add(saveButton).pad(15);;

        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                body.getFixtureList().first().setUserData(textField.getText());
                mapObject.setUserDate(textField.getText());
            }
        });

        pack();
        setCenterOnAdd(true);
        addCloseButton();
        setSize(getWidth(),getHeight()*1.2f);
    }
}
