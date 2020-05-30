package com.cosma.annihilation.Ai.Tasks;

import com.cosma.annihilation.EntityEngine.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.cosma.annihilation.Components.AiComponent;
import com.cosma.annihilation.Components.BodyComponent;
import com.cosma.annihilation.Components.SkeletonComponent;

public class SkeletonAnimation extends Task {

    public SkeletonAnimation() {
    }

    @Override
    public void reset() {

    }

    @Override
    public void update(Entity entity, float deltaTime) {

        SkeletonComponent skeletonComponent = entity.getComponent(SkeletonComponent.class);
        Body body = entity.getComponent(BodyComponent.class).body;
        AiComponent aiComponent = entity.getComponent(AiComponent.class);

        skeletonComponent.skeletonDirection = aiComponent.faceDirection > 0;



        if (body.getLinearVelocity().x != 0) {
            skeletonComponent.walk();
        }
        if (body.getLinearVelocity().x == 0) {
            skeletonComponent.idle();
        }
//        skeletonComponent.animationState.apply(skeletonComponent.skeleton);
    }
}
