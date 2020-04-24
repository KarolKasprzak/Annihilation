package com.cosma.annihilation.Gui.MainMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.cosma.annihilation.Components.PlayerComponent;
import com.cosma.annihilation.Items.ItemType;

public class AmmoIndicatorWidget extends Image {

    Texture texture;
    TextureRegion textureRegion;
    TextureRegionDrawable textureRegionDrawable;

    public AmmoIndicatorWidget() {
        texture = new Texture(Gdx.files.internal("gfx/textures/ammo_icon.png"));
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        textureRegion = new TextureRegion(texture);
        textureRegionDrawable = new TextureRegionDrawable();
        textureRegion.setRegion(0, 0, texture.getWidth(), texture.getHeight() * 30);
        textureRegionDrawable.setRegion(textureRegion);
        this.setDrawable(textureRegionDrawable);
    }

    public void update(PlayerComponent playerComponent) {
        if (playerComponent.activeWeapon.getCategory() == ItemType.GUNS) {
            int ammo = playerComponent.activeWeapon.getAmmoInClip();
            textureRegion.setRegion(0, 0, texture.getWidth(), texture.getHeight() * ammo);
            textureRegionDrawable.setRegion(textureRegion);
            this.setHeight(textureRegion.getRegionHeight());
            this.setDrawable(textureRegionDrawable);
        } else {
            this.setDrawable(null);
        }
    }
}