package com.cosma.annihilation.Systems;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.cosma.annihilation.Components.PhysicsComponent;
import com.cosma.annihilation.Components.PlayerComponent;
import com.cosma.annihilation.EntityEngine.core.ComponentMapper;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.EntityEngine.core.Family;
import com.cosma.annihilation.EntityEngine.systems.IteratingSystem;
import com.cosma.annihilation.Utils.Constants;

public class CameraSystem extends IteratingSystem {

    private ComponentMapper<PhysicsComponent> bodyMapper;
    private OrthographicCamera camera;

    public CameraSystem(OrthographicCamera camera) {


        super(Family.all(PlayerComponent.class).get(),Constants.CAMERA_SYSTEM);
        bodyMapper = ComponentMapper.getFor(PhysicsComponent.class);
        this.camera = camera;
    }
    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        PhysicsComponent body = bodyMapper.get(entity);
        camera.position.set(body.body.getPosition().x,body.body.getPosition().y + 1,0);
        camera.update();
    }
}
