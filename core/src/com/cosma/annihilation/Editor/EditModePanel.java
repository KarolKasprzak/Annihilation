package com.cosma.annihilation.Editor;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Screens.EditorScreen;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.*;

public class EditModePanel extends VisWindow {


    public EditModePanel(final EditorScreen editorScreen) {
        super("Edit mode:");
        TableUtils.setSpacingDefaults(this);
        columnDefaults(0).left();

        VisTable footerTable = new VisTable();
        footerTable.addSeparator();
        footerTable.add("");

        TextureAtlas iconAtlas = Annihilation.getAssets().get("gfx/atlas/editor_icon.atlas",TextureAtlas.class);

        VisImageButton objectsEditButton = new VisImageButton(new TextureRegionDrawable(iconAtlas.findRegion("geometry")));
        VisImageButton lightsEditButton =  new VisImageButton(new TextureRegionDrawable(iconAtlas.findRegion("cone_light_h")));
        VisImageButton spritesEditButton =  new VisImageButton(new TextureRegionDrawable(iconAtlas.findRegion("texture")));

        float size = Gdx.graphics.getHeight()*0.04f;

        add(objectsEditButton).size(size);
        add(lightsEditButton).size(size);
        add(spritesEditButton).size(size);

        pack();
        setMovable(false);
        setResizable(false);

        objectsEditButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                editorScreen.lightsPanel.setPanelButtonsDisable(true);
                editorScreen.objectPanel.setPanelButtonsDisable(false);
                editorScreen.setObjectLayerSelected();
            }
        });

        lightsEditButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                editorScreen.objectPanel.setPanelButtonsDisable(true);
                editorScreen.lightsPanel.setPanelButtonsDisable(false);
                editorScreen.setLightsLayerSelected();
            }
        });

        spritesEditButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                editorScreen.objectPanel.setPanelButtonsDisable(true);
                editorScreen.lightsPanel.setPanelButtonsDisable(true);
                editorScreen.setSpriteLayerSelected();
            }
        });

    }
}
