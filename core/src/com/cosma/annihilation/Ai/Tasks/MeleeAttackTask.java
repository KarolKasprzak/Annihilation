package com.cosma.annihilation.Ai.Tasks;

import com.cosma.annihilation.EntityEngine.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;
import com.cosma.annihilation.Components.*;


public class MeleeAttackTask extends Task {
    private float weaponRange;
    private Entity enemy;
    private RayCastCallback attackRaycast;
    private Boolean isMeleeAttackFinish = true;
    private Vector2 raycastEnd = new Vector2();
    private float attackTimer = 0;
    private float timeBetweenAttack = 0;

    public MeleeAttackTask() {
        start();
        attackRaycast = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if (fixture.getBody().getUserData() instanceof Entity && ((Entity) fixture.getBody().getUserData()).getComponent(PlayerComponent.class) != null) {
                    enemy = (Entity) fixture.getBody().getUserData();
                    return 0;
                }
                enemy = null;
                return 0;
            }
        };
    }

    @Override
    public void reset() {
        start();
        enemy = null;
    }

    @Override
    protected void success() {
        super.success();
    }

    public Entity getEnemyEntity(){
        return enemy;
    }
    public Vector2 getEnemyPosition(){
        return enemy.getComponent(PhysicsComponent.class).body.getPosition();
    }


    @Override
    public void update(Entity entity, float deltaTime) {
        PhysicsComponent physicsComponent = entity.getComponent(PhysicsComponent.class);
        AiComponent aiComponent = entity.getComponent(AiComponent.class);
        SkeletonComponent skeletonComponent = entity.getComponent(SkeletonComponent.class);

        raycast(physicsComponent.body,aiComponent);

        if(enemy != null){
            skeletonComponent.meleeIdle();
            attackTimer += attackTimer + deltaTime;
            System.out.println(attackTimer);
            if(attackTimer > timeBetweenAttack)
            meleeAttack(physicsComponent.body,skeletonComponent,aiComponent);
            timeBetweenAttack = MathUtils.random(3600000,50000000);
        }else{
            skeletonComponent.animationState.clearTrack(2);
        }
        if(entity.getComponent(HealthComponent.class).hp <= 0){
//            skeletonComponent.animationState.clearTrack(4);
        }

    }

    private void raycast(Body body,AiComponent aiComponent){
        World world = body.getWorld();
        raycastEnd.set(body.getPosition().x + (aiComponent.aiWeapon.getRange()+0.5f) * aiComponent.faceDirection,body.getPosition().y);

        world.rayCast(attackRaycast,body.getPosition(),raycastEnd);
    }

    private void meleeAttack(Body body,SkeletonComponent skeletonComponent,AiComponent aiComponent) {
        if (isMeleeAttackFinish) {
            isMeleeAttackFinish = false;
            skeletonComponent.skeleton.updateWorldTransform();
            aiComponent.isPaused = true;
            skeletonComponent.meleeAttack();
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    aiComponent.isPaused = false;
                    isMeleeAttackFinish = true;
                        raycast(body,aiComponent);
                        if(enemy != null){
                            enemy.getComponent(HealthComponent.class).decreaseHp(aiComponent.aiWeapon.getDamage());
                        }
                        enemy = null;
                        attackTimer = 0;
                }
            }, skeletonComponent.animationState.getCurrent(6).getAnimation().getDuration());
            skeletonComponent.animationState.addEmptyAnimation(6, 0.2f, skeletonComponent.animationState.getCurrent(6).getAnimation().getDuration());
        }
    }
}
