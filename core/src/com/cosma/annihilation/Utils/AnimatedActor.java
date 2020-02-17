package com.cosma.annihilation.Utils;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;


public class AnimatedActor extends Image {
    private float stateTime = 0;
    private Animation<TextureRegion> animation;

    public AnimatedActor(Animation animation) {
        this.animation = animation;
    }

    @Override
    public void act(float delta) {
        stateTime = stateTime +  delta ;
        setDrawable(new TextureRegionDrawable(new TextureRegion(animation.getKeyFrame(stateTime))));
        super.act(delta);
    }
}
