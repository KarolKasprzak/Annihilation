package com.cosma.annihilation.Ai.Tasks;

import com.cosma.annihilation.EntityEngine.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.cosma.annihilation.Components.AiComponent;
import com.cosma.annihilation.Components.PhysicsComponent;

public class Wander extends Task {
    private GoToPosition moveTo;
    private  AiSight aiSight;
    private float radius = 5;
    private float timeToTurn = 1;
    private float time = 0;
    private boolean randomTimeToTurn = false;
    private float minTime;
    private float maxTime;


    public Wander() {
        moveTo = new GoToPosition();

    }

    public Wander(float radius) {
        this.radius = radius;
        moveTo = new GoToPosition();
    }



    @Override
    public void reset() {
    }

    @Override
    public void update(Entity entity, float deltaTime) {
        PhysicsComponent physicsComponent = entity.getComponent(PhysicsComponent.class);
        AiComponent aiComponent = entity.getComponent(AiComponent.class);

        if(moveTo.isSuccess() || moveTo.isPrepared()){
            time += deltaTime;
            if(randomTimeToTurn){
                timeToTurn = MathUtils.random(minTime,maxTime);
            }
            if(time > timeToTurn){
                time = 0;
                moveTo.reset(findNewTarget(aiComponent));
            }
        }
        moveTo.update(entity,deltaTime);
    }

    private float findNewTarget(AiComponent aiComponent){
        return MathUtils.random(aiComponent.startPosition.x-radius,aiComponent.startPosition.x +radius);
    }

    public void setTimeToTurn(float timeToTurn) {
        this.timeToTurn = timeToTurn;
    }

    private void setRandomTimeToTurn(float min, float max){
        randomTimeToTurn = true;
        minTime = min;
        maxTime = max;
    }
}
