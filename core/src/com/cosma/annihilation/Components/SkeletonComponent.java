package com.cosma.annihilation.Components;

import com.badlogic.ashley.core.Component;
import com.esotericsoftware.spine.*;

public class SkeletonComponent implements Component {
    public Skeleton skeleton;
    public SkeletonBounds bounds;
    public AnimationState animationState;
    /** right = true, left = false  */
    public boolean skeletonDirection = true;

    public void setSkeletonAnimation(boolean force, String animation, int track, boolean loop) {
        Animation newAnimation = animationState.getData().getSkeletonData().findAnimation(animation);
        AnimationState.TrackEntry current = animationState.getCurrent(track);
        Animation currentAnimation = current == null ? null : current.getAnimation();
        if (force || currentAnimation != newAnimation) {
            animationState.setAnimation(track, animation, loop);
        }
    }

}
