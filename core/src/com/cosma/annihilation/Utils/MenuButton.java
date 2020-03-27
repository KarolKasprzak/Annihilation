package com.cosma.annihilation.Utils;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MenuButton extends Button {

    public MenuButton(TextureAtlas.AtlasRegion regionUp, TextureAtlas.AtlasRegion regionDown) {
        this.setStyle(new ButtonStyle(new TextureRegionDrawable(regionUp), new TextureRegionDrawable(regionDown), null));
    }

}
