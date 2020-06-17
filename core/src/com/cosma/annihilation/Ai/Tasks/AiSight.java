package com.cosma.annihilation.Ai.Tasks;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.cosma.annihilation.Components.AiComponent;
import com.cosma.annihilation.Components.PhysicsComponent;
import com.cosma.annihilation.Components.PlayerComponent;
import com.cosma.annihilation.EntityEngine.core.Entity;

public class AiSight extends Task {
    private float sightRange = 5;
    private Entity enemy;
    private RayCastCallback sightRayCallback;

    public AiSight(float sightRange) {
        super();
        this.sightRange = sightRange;

    }

    public AiSight() {
        start();
        sightRayCallback = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if (fixture.getBody().getUserData() instanceof Entity && ((Entity) fixture.getBody().getUserData()).getComponent(PlayerComponent.class) != null) {
                    enemy = (Entity) fixture.getBody().getUserData();
                    success();
                    return 0;
                }
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
        entity.getComponent(PhysicsComponent.class);
        PhysicsComponent physicsComponent = entity.getComponent(PhysicsComponent.class);
        AiComponent aiComponent = entity.getComponent(AiComponent.class);

        World world = physicsComponent.body.getWorld();
        world.rayCast(sightRayCallback, physicsComponent.body.getPosition().x, physicsComponent.body.getPosition().y,
                physicsComponent.body.getPosition().x + sightRange * aiComponent.faceDirection, physicsComponent.body.getPosition().y);
    }
}
