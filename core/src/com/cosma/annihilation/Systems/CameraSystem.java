package com.cosma.annihilation.Systems;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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
    private Vector3 vector3tmp = new Vector3();
    private float cameraLookahead = 1f;

    public CameraSystem(OrthographicCamera camera) {

        super(Family.all(PlayerComponent.class).get(),Constants.CAMERA_SYSTEM);
        bodyMapper = ComponentMapper.getFor(PhysicsComponent.class);
        this.camera = camera;
    }
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PhysicsComponent physicsComponent = bodyMapper.get(entity);
        Vector2 playerPosition = physicsComponent.body.getPosition();
        vector3tmp.set(playerPosition.x, playerPosition.y+1,0);
        camera.position.lerp(vector3tmp,0.5f);

//        camera.position.set(body.body.getPosition().x,body.body.getPosition().y + 1,0);
        camera.update();
    }
}
