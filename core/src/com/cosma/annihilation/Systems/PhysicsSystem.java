package com.cosma.annihilation.Systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.cosma.annihilation.Components.BodyComponent;
import com.cosma.annihilation.Utils.Constants;
import com.cosma.annihilation.Utils.StateManager;


public class PhysicsSystem extends IteratingSystem {
    private static final float MAX_STEP_TIME = 1/45f;
    private static float accumulator = 0f;
    private World world;
    private ComponentMapper<BodyComponent> bodyMapper = ComponentMapper.getFor(BodyComponent.class);

    @SuppressWarnings("unchecked")
    public PhysicsSystem(World world) {
        super(Family.all(BodyComponent.class).get(), Constants.PHYSIC_SYSTEM);
        this.world = world;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        if(accumulator >= MAX_STEP_TIME &! StateManager.pause) {
            world.step(MAX_STEP_TIME, 6, 2);
            accumulator -= MAX_STEP_TIME;
            if(getEngine().getSystem(CollisionSystem.class).bodiesToRemove.size > 0 && !world.isLocked()){
                for(Body body: getEngine().getSystem(CollisionSystem.class).bodiesToRemove){
                    world.destroyBody(body);
                    getEngine().getSystem(CollisionSystem.class).bodiesToRemove.removeValue(body,true);
                }
            }
        }
    }
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
    }
}
