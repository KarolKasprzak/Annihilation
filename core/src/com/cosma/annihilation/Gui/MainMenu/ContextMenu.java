package com.cosma.annihilation.Gui.MainMenu;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Gui.GuiWindow;

public class ContextMenu extends GuiWindow {
    public ContextMenu(String title, Skin skin) {

        super(title, skin);
        setModal(true);
        TextureAtlas textureAtlas = Annihilation.getAssets().get("gfx/atlas/game_icon.atlas", TextureAtlas.class);

        ImageButton unloadButton = new ImageButton(new TextureRegionDrawable(textureAtlas.findRegion("unload")));
        ImageButton examineButton = new ImageButton(new TextureRegionDrawable(textureAtlas.findRegion("examine")));
        ImageButton cancelButton = new ImageButton(new TextureRegionDrawable(textureAtlas.findRegion("cancel")));


        this.add(unloadButton);
        this.add(examineButton);
        this.add(cancelButton);

        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                close();
            }
        });
        pack();
    }
}
