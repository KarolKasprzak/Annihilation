package com.cosma.annihilation.Ai;

import com.badlogic.ashley.core.Entity;
import com.cosma.annihilation.Ai.Tasks.*;


public class HumanAiTest extends Task {
    private Wander wander;
    private AiSight aiSight;
    private GoToPosition goToPosition;
    private ChaseEnemy chaseEnemy;
    private SkeletonAnimation skeletonAnimation;


    public HumanAiTest() {
        wander  = new Wander();
        aiSight = new AiSight();
        goToPosition = new GoToPosition();
        chaseEnemy = new ChaseEnemy();
        skeletonAnimation = new SkeletonAnimation();
        goToPosition.start();
    }

    @Override
    public void reset() {

    }

    @Override
    public void update(Entity entity, float deltaTime) {
        aiSight.update(entity, deltaTime);
        if(aiSight.isWorking()){
            wander.update(entity, deltaTime);
        }
        if(aiSight.isSuccess()){
            chaseEnemy.setEnemyPosition(aiSight.getEnemyPosition().x);
            chaseEnemy.update(entity, deltaTime);
            aiSight.reset();
            if(chaseEnemy.isSuccess()){
            }
        }
        skeletonAnimation.update(entity, deltaTime);
    }
}
