package com.cosma.annihilation.Components;

import com.cosma.annihilation.EntityEngine.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


import java.util.HashMap;

public class AnimationComponent implements Component {
    public float time = 0f;

    public Animation<TextureRegion> currentAnimation;
    public HashMap<String,Animation<TextureRegion>> animationMap = new HashMap<>();
    public boolean isAnimationFinish = true;
    /** right = true, left = false  */
    public boolean spriteDirection = true;
}
