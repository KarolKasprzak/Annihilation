package com.cosma.annihilation.Ai.Tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.cosma.annihilation.Components.AiComponent;
import com.cosma.annihilation.Components.BodyComponent;
import com.cosma.annihilation.Components.PlayerComponent;

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
        return enemy.getComponent(BodyComponent.class).body.getPosition();
    }


    @Override
    public void update(Entity entity, float deltaTime) {
        BodyComponent bodyComponent = entity.getComponent(BodyComponent.class);
        AiComponent aiComponent = entity.getComponent(AiComponent.class);

        World world = bodyComponent.body.getWorld();
        world.rayCast(sightRayCallback, bodyComponent.body.getPosition().x, bodyComponent.body.getPosition().y,
                bodyComponent.body.getPosition().x + sightRange * aiComponent.faceDirection, bodyComponent.body.getPosition().y);
    }
}
