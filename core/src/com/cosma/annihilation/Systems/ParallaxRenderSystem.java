package com.cosma.annihilation.Systems;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cosma.annihilation.Components.ParallaxComponent;
import com.cosma.annihilation.Components.PhysicsComponent;
import com.cosma.annihilation.EntityEngine.core.ComponentMapper;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.EntityEngine.core.Family;
import com.cosma.annihilation.EntityEngine.systems.IteratingSystem;

public class ParallaxRenderSystem extends IteratingSystem {

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private ComponentMapper<ParallaxComponent> parallaxMapper;
    private ComponentMapper<PhysicsComponent> physicMapper;

    public ParallaxRenderSystem(SpriteBatch batch, OrthographicCamera camera) {
        super(Family.all(ParallaxComponent.class,PhysicsComponent.class).get(), 7);
        this.batch = batch;
        this.camera = camera;

        parallaxMapper = ComponentMapper.getFor(ParallaxComponent.class);
        physicMapper = ComponentMapper.getFor(PhysicsComponent.class);
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        ParallaxComponent parallaxComponent = parallaxMapper.get(entity);
        PhysicsComponent physicsComponent = physicMapper.get(entity);
        batch.setShader(null);
        batch.begin();
        float x,y,width,height;
        x = physicsComponent.body.getPosition().x - parallaxComponent.displayW/2;
        y = physicsComponent.body.getPosition().y - parallaxComponent.displayH/2;

        float xSpeed = this.getEngine().getPlayerEntity().getComponent(PhysicsComponent.class).body.getLinearVelocity().x;
        parallaxComponent.scroll += xSpeed/15;

        for(int i = 0;i<parallaxComponent.textures.size;i++) {

            width = parallaxComponent.displayW;
            height = parallaxComponent.displayH;
            int srcX = (int)parallaxComponent.scroll*i;

            batch.draw(parallaxComponent.textures.get(i), x, y, 0, 0, width, height,1,1,0,srcX,0,
                    parallaxComponent.textures.get(i).getWidth()/2,parallaxComponent.textures.get(i).getHeight()/2,false,false);
        }

        batch.end();
    }
}