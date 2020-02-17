package com.cosma.annihilation.Components;

import com.badlogic.ashley.core.Component;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonBounds;

public class SkeletonComponent implements Component {
    public Skeleton skeleton;
    public SkeletonBounds bounds;
    public AnimationState animationState;
    /** right = true, left = false  */
    public boolean skeletonDirection = true;

}
