package com.cosma.annihilation.Components;

import com.badlogic.gdx.graphics.Texture;
import com.cosma.annihilation.EntityEngine.core.Component;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.*;

import java.security.PublicKey;

public class SkeletonComponent implements Component {
    public int drawOrder = 1;
    public Skeleton skeleton;
    public Texture diffuseTexture;
    public Texture normalTexture;
    public SkeletonBounds bounds;
    public AnimationState animationState;
    public Array<String> meleeAttackAnimations = new Array<>();
    public Array<String> deadAnimations = new Array<>();

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

    public void walk(){
        setSkeletonAnimation(false,"walk",3,true);
    }

    public void idle(){setSkeletonAnimation(false,"idle",3,true);}

    public void meleeAttack(){setSkeletonAnimation(false, meleeAttackAnimations.random(),6,false);}

    public void meleeIdle(){setSkeletonAnimation(false,"melee_idle",2,true);}

    public void climbIdle(){setSkeletonAnimation(false,"climb_idle",0,true);}

    public void climbUp(){
//        animationState.getData().setMix("walk","climb_up",0.1f);
//        animationState.getData().setMix("idle","climb_up",0.1f);
        setSkeletonAnimation(false,"climb_up",0,true);}

    public void dead(){
        setSkeletonAnimation(false, deadAnimations.random(),3,false);
        animationState.clearTrack(2);
    }


}
