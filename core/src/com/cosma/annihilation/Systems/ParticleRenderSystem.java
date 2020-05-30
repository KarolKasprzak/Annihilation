package com.cosma.annihilation.Systems;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.cosma.annihilation.Components.ParticleComponent;
import com.cosma.annihilation.EntityEngine.core.ComponentMapper;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.EntityEngine.core.Family;
import com.cosma.annihilation.EntityEngine.systems.IteratingSystem;
import com.cosma.annihilation.Utils.Constants;

public class ParticleRenderSystem extends IteratingSystem {

    private SpriteBatch batch;
    private World world;
    private ComponentMapper<ParticleComponent> particleMapper;

    public ParticleRenderSystem(World world, SpriteBatch batch) {
        super(Family.all(ParticleComponent.class).get(), Constants.PARTICLE_RENDER);
        this.batch = batch;
        this.world = world;
        particleMapper = ComponentMapper.getFor(ParticleComponent.class);
    }

    @Override
    public void update(float deltaTime) {

        super.update(deltaTime);
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        ParticleComponent particleComponent = particleMapper.get(entity);
        batch.begin();
        particleComponent.particleEffect.draw(batch,deltaTime);
        batch.end();
        if(particleComponent.particleEffect.isComplete()){
            getEngine().removeEntity(entity);
        }
    }
    public void spawnParticleEffect(float x, float y, Body body){
        spawnParticleEffect("gun_spark.p",x,y);
    }

    public void spawnParticleEffect(String name,float x, float y){
        Entity entity = getEngine().createEntity();
        ParticleComponent particleComponent = new ParticleComponent();
        particleComponent.loadDate(name);
        particleComponent.particleEffect.setPosition(x,y);
        particleComponent.particleEffect.start();
        entity.add(particleComponent);
        getEngine().addEntity(entity);
    }
}