package com.cosma.annihilation.Ai.Tasks;


import com.badlogic.gdx.physics.box2d.Body;
import com.cosma.annihilation.Components.AiComponent;
import com.cosma.annihilation.Components.PhysicsComponent;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.Utils.Util;

public class ChaseEnemy extends Task {
    private float chaseRange = 10;
    private float enemyPosition;
    private float tolerance = 1f;
    private GoToPosition goToPosition = new GoToPosition(true);

    public ChaseEnemy() {
        start();
    }

    public ChaseEnemy(float chaseRange) {
        this.chaseRange = chaseRange;
    }

    public void setChaseRange(float chaseRange) {
        this.chaseRange = chaseRange;
    }

    @Override
    public void reset() {

    }

    public void setEnemyPosition(float enemyPosition) {
        if(enemyPosition < 0){
            enemyPosition -= tolerance;
        }else{
            enemyPosition += tolerance;
        }
        this.enemyPosition = enemyPosition;
    }

    @Override
    public void update(Entity entity, float deltaTime) {
        if (isWorking()) {
            goToPosition.reset(enemyPosition);
            goToPosition.update(entity, deltaTime);
        }
        Body body = entity.getComponent(PhysicsComponent.class).body;
        AiComponent aiComponent = entity.getComponent(AiComponent.class);
        if(enemyPosition == Util.roundFloat(body.getPosition().x,1)){
           success();
        }
        if(body.getPosition().x < aiComponent.startPosition.x -chaseRange || body.getPosition().x > aiComponent.startPosition.x + chaseRange){
            fail();
        }
    }
}
