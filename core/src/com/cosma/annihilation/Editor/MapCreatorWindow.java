package com.cosma.annihilation.Editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.cosma.annihilation.Screens.MapEditor;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.form.SimpleFormValidator;
import com.kotcrab.vis.ui.widget.*;


public class MapCreatorWindow extends VisWindow {

    private MapCreatorWindow window;
    public MapCreatorWindow(final MapEditor mapEditor) {
        super("");

        window = this;
        TableUtils.setSpacingDefaults(this);
        defaults().padRight(1);
        defaults().padLeft(1);
        columnDefaults(0).left();

        addCloseButton();
        closeOnEscape();

        VisTextButton cancelButton = new VisTextButton("Cancel");
        VisTextButton acceptButton = new VisTextButton("Accept");

        final VisValidatableTextField mapWidth = new VisValidatableTextField();
        final VisValidatableTextField mapHeight = new VisValidatableTextField();
        final VisValidatableTextField mapUnit = new VisValidatableTextField();

        VisLabel errorLabel = new VisLabel();
        errorLabel.setColor(Color.RED);

        VisTable buttonTable = new VisTable(true);
        buttonTable.add(errorLabel).expand().fill();
        buttonTable.add(cancelButton);
        buttonTable.add(acceptButton);

        add(new VisLabel("Map width: "));
        add(mapWidth).expand().fill();
        row();
        add(new VisLabel("Map height: "));
        add(mapHeight).expand().fill();
        row();
        add(new VisLabel("Tile size/pixel per unit: "));
        add(mapUnit).expand().fill();
        row();
        add(buttonTable).fill().expand().colspan(2).padBottom(3);

        SimpleFormValidator validator;
        validator = new SimpleFormValidator(acceptButton, errorLabel, "smooth");
        validator.setSuccessMessage("all good!");
        validator.notEmpty(mapWidth, "Map width cannot be empty");
        validator.notEmpty(mapHeight, "Map height cannot be empty");
        validator.notEmpty(mapUnit, "Map unit cannot be empty");

        validator.integerNumber(mapWidth, "Map width must be a number");
        validator.integerNumber(mapHeight, "Map height must be a number");
        validator.integerNumber(mapUnit, "Map unit must be a number");

        acceptButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                mapEditor.createNewMap(Integer.valueOf(mapWidth.getText()),Integer.valueOf(mapHeight.getText()),Integer.valueOf(mapUnit.getText()));
                System.out.println(mapWidth.getText() + mapHeight.getText() );
                window.close();
            }
        });

        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
            window.close();
            }
        });

        pack();
        setSize(getWidth() + 60, getHeight());
        setPosition(Gdx.graphics.getWidth()/2-getWidth()/2, Gdx.graphics.getHeight()/2-getHeight()/2);
    }
}
