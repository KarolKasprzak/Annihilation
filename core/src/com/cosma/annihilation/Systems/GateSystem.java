package com.cosma.annihilation.Systems;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.cosma.annihilation.Components.*;
import com.cosma.annihilation.EntityEngine.core.ComponentMapper;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.EntityEngine.core.Family;
import com.cosma.annihilation.EntityEngine.systems.IteratingSystem;
import com.cosma.annihilation.Utils.Constants;
import com.cosma.annihilation.Utils.Util;


public class GateSystem extends IteratingSystem {
    private ComponentMapper<GateComponent> gateMapper;
    private ComponentMapper<PhysicsComponent> bodyMapper;


    public GateSystem() {
        super(Family.all(GateComponent.class).get(), Constants.ACTION_SYSTEM);
        gateMapper = ComponentMapper.getFor(GateComponent.class);
        bodyMapper = ComponentMapper.getFor(PhysicsComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        GateComponent gateComponent = gateMapper.get(entity);
        Body body = bodyMapper.get(entity).body;

        if(gateComponent.isMoving){
            if (Util.roundFloat(gateComponent.targetPosition.y, 2) != Util.roundFloat(body.getPosition().y, 2)) {
                if (gateComponent.targetPosition.y < body.getPosition().y) {
                    body.setLinearVelocity(0, -1);
                } else {
                    body.setLinearVelocity(0, 1);
                }
            }else {
                gateComponent.isMoving = false;
                gateComponent.isOpen =! gateComponent.isOpen;
                body.setLinearVelocity(0, 0);
            }

        }


    }
}
