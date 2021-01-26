package com.cosma.annihilation.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cosma.annihilation.Components.PhysicsComponent;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.EntityEngine.core.Family;
import com.cosma.annihilation.EntityEngine.systems.IteratingSystem;
import com.cosma.annihilation.Utils.Constants;


public class LightsSystem extends IteratingSystem {
    private static final float MAX_STEP_TIME = 1/45f;
    private static float accumulator = 0f;
    private World world;


    public LightsSystem(World world, OrthographicCamera camera) {
        super(Family.all(PhysicsComponent.class).get(), Constants.PHYSIC_SYSTEM);
        this.world = world;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        world.step(Gdx.graphics.getDeltaTime(), 6, 8);

//        float frameTime = Math.min(deltaTime, 0.25f);
//        accumulator += frameTime;
//        if(accumulator >= MAX_STEP_TIME &! StateManager.pause) {
//            world.step(MAX_STEP_TIME, 6, 2);
//            accumulator -= MAX_STEP_TIME;
//        }
    }
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
    }
}
