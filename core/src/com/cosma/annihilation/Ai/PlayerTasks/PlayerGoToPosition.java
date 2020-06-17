package com.cosma.annihilation.Ai.PlayerTasks;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.cosma.annihilation.Ai.Tasks.Task;
import com.cosma.annihilation.Components.PhysicsComponent;
import com.cosma.annihilation.Components.PlayerComponent;
import com.cosma.annihilation.Components.SkeletonComponent;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.Utils.Util;

public class PlayerGoToPosition extends Task {

    private float destX;

    public PlayerGoToPosition(float destX) {
        super();
        this.destX = destX;
    }

    public PlayerGoToPosition(Vector2 targetPosition) {
        super();
        this.destX = targetPosition.x;
        start();
    }

    public PlayerGoToPosition() {
    }

    public PlayerGoToPosition(boolean run) {
        if(run){
            start();
        }
    }

    @Override
    public void reset() {
        start();
    }

    public void reset(float newTarget) {
        start();
        destX = newTarget;
    }

    public void setTarget(float targetX){
        this.destX = targetX;
    }

    @Override
    public void update(Entity entity, float deltaTime) {
        PhysicsComponent physicsComponent = entity.getComponent(PhysicsComponent.class);
        PlayerComponent playerComponent = entity.getComponent(PlayerComponent.class);
        SkeletonComponent skeletonComponent = entity.getComponent(SkeletonComponent.class);


        if(isSuccess() || isPrepared()){
            return;
        }
        Body aiBody = physicsComponent.body;
        Vector2 aiPosition = aiBody.getPosition();
        if (Util.roundFloat(destX, 1) != Util.roundFloat(aiPosition.x, 1)) {
            if (destX < aiPosition.x) {
                aiBody.setLinearVelocity(playerComponent.velocity * -1, 0);
                skeletonComponent.skeletonDirection = false;
            } else {
                aiBody.setLinearVelocity(playerComponent.velocity, 0);
                skeletonComponent.skeletonDirection = true;
            }
        }else {
            success();
            aiBody.setLinearVelocity(0, 0);
        }
    }
}
