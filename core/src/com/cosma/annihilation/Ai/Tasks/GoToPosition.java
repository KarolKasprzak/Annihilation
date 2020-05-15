package com.cosma.annihilation.Ai.Tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.cosma.annihilation.Components.AiComponent;
import com.cosma.annihilation.Components.BodyComponent;
import com.cosma.annihilation.Utils.Util;

public class GoToPosition extends Task {

    private float destX;

    public GoToPosition(float destX) {
        super();
        this.destX = destX;
    }

    public GoToPosition() {
    }

    public GoToPosition(boolean run) {
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
    public void update(Entity entity,  float deltaTime) {
        BodyComponent bodyComponent = entity.getComponent(BodyComponent.class);
        AiComponent aiComponent = entity.getComponent(AiComponent.class);


        if(isSuccess() || isPrepared()){
            return;
        }
        Body aiBody = bodyComponent.body;
        Vector2 aiPosition = aiBody.getPosition();
        if (Util.roundFloat(destX, 1) != Util.roundFloat(aiPosition.x, 1)) {
            if (destX < aiPosition.x) {
                aiBody.setLinearVelocity(aiComponent.speed * -1, 0);
                aiComponent.faceDirection = -1;
            } else {
                aiBody.setLinearVelocity(aiComponent.speed, 0);
                aiComponent.faceDirection = 1;
            }
        }else {
            success();
            aiBody.setLinearVelocity(0, 0);
        }
    }
}
