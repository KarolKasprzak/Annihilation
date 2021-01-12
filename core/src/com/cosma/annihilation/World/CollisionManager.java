package com.cosma.annihilation.World;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Timer;
import com.cosma.annihilation.Components.PlayerComponent;
import com.cosma.annihilation.EntityEngine.core.Component;
import com.cosma.annihilation.EntityEngine.core.Engine;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.Systems.FxSystem;
import com.cosma.annihilation.Utils.Enums.BodyID;

public class CollisionManager implements ContactListener {
    private Engine engine;

    public CollisionManager(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        bulletCollision(fa,fb);
        bulletCollision(fb,fa);

        addEntityToActionList(fa, fb);

        if (fa.getUserData() == BodyID.BULLET_SHELL) {
            removeShellAfterTime(fa);
        }
        if (fb.getUserData() == BodyID.BULLET_SHELL) {
            removeShellAfterTime(fb);
        }
        //Player contacts
        if (isPlayerFixture(fa) || isPlayerFixture(fb)) {
            PlayerComponent playerComponent = getPlayerComponent(fa, fb);

            //Player ground contact
            if (fb.getUserData() == BodyID.PLAYER_FOOT && !fa.isSensor() || fa.getUserData() == BodyID.PLAYER_FOOT && !fb.isSensor()) {
                playerComponent.numFootContacts++;
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        removeEntityFromActionList(fa, fb);

        //Player contacts
        if (isPlayerFixture(fa) || isPlayerFixture(fb)) {
            PlayerComponent playerComponent = getPlayerComponent(fa, fb);
            //Player ground contact
            if (fb.getUserData() == BodyID.PLAYER_FOOT && !fa.isSensor() || fa.getUserData() == BodyID.PLAYER_FOOT && !fb.isSensor()) {
                playerComponent.onGround = false;
                playerComponent.numFootContacts--;
                delayJump(0.6f, playerComponent);
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
//        Fixture fa = contact.getFixtureA();
//        Fixture fb = contact.getFixtureB();
//        if (isPlayerFixture(fa)){
//            PlayerComponent playerComponent = getPlayerComponent(fa);
//            if(playerComponent.climbing){
//                if(fa.getBody().getPosition().y < fb.getBody().getPosition().y){
//                    contact.setEnabled(false);
//                }
//            }
//        }
//        if (isPlayerFixture(fb)){
//            PlayerComponent playerComponent = getPlayerComponent(fb);
//            if(playerComponent.climbing){
//                if(fb.getBody().getPosition().y < fa.getBody().getPosition().y){
//                    contact.setEnabled(false);
//                }
//            }
//        }


    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }



    private boolean isPlayerFixture(Fixture fixture) {
        if (fixture.getBody().getUserData() instanceof Entity) {
            Entity entity = (Entity) fixture.getBody().getUserData();
            return entity.getComponent(PlayerComponent.class) != null;
        }
        return false;
    }

    private boolean isFixtureHaveComponent(Fixture fixture, Class<? extends Component> componentClass) {
        if (fixture.getBody().getUserData() instanceof Entity) {
            Entity entity = (Entity) fixture.getBody().getUserData();
            return entity.getComponent(componentClass) != null;
        }
        return false;
    }

    private void removeShellAfterTime(final Fixture fixture) {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (fixture.getBody().getUserData() != null) {
                    engine.removePhysicBody(fixture.getBody());
                    engine.removeEntity((Entity) fixture.getBody().getUserData());
                    fixture.getBody().setUserData(null);
                }
            }
        }, 8);

    }

    private void delayJump(float delay, PlayerComponent playerComponent) {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                playerComponent.canJump = true;
            }
        }, delay);
    }

    private void bulletCollision(Fixture bulletFixture,Fixture collidedFixture) {
        if (bulletFixture.getUserData() == BodyID.BULLET) {
            engine.removePhysicBody(bulletFixture.getBody());

//            engine.getSystem(ParticleRenderSystem.class).spawnParticleEffect();
            engine.getSystem(FxSystem.class).spawnParticleEffect("gun_spark.p",bulletFixture.getBody().getPosition().x,bulletFixture.getBody().getPosition().y);
            engine.removeEntity((Entity) bulletFixture.getBody().getUserData());
        }
    }

    private PlayerComponent getPlayerComponent(Fixture fixture) {
            Entity entity = (Entity) fixture.getBody().getUserData();
            return entity.getComponent(PlayerComponent.class);
    }

    private PlayerComponent getPlayerComponent(Fixture fa, Fixture fb) {
        if (isPlayerFixture(fa)) {
            Entity entity = (Entity) fa.getBody().getUserData();
            return entity.getComponent(PlayerComponent.class);
        } else {
            Entity entity = (Entity) fb.getBody().getUserData();
            return entity.getComponent(PlayerComponent.class);
        }
    }

    private void addEntityToActionList(Fixture fa, Fixture fb) {
        if(fa.getUserData() == BodyID.ACTION_TRIGGER && fb.getUserData() == BodyID.PLAYER_BODY || fb.getUserData() == BodyID.ACTION_TRIGGER && fa.getUserData() == BodyID.PLAYER_BODY ){
            Entity playerEntity = (fa.getUserData() == BodyID.PLAYER_BODY) ? (Entity)fa.getBody().getUserData() : (Entity)fb.getBody().getUserData();
            Entity actionEntity = (fa.getUserData() == BodyID.ACTION_TRIGGER) ? (Entity)fa.getBody().getUserData() : (Entity)fb.getBody().getUserData();
            PlayerComponent playerComponent = playerEntity.getComponent(PlayerComponent.class);
            if (!playerComponent.collisionEntityArray.contains(actionEntity,true)) {
                playerComponent.collisionEntityArray.add(actionEntity);
            }

        }
    }

    private void removeEntityFromActionList(Fixture fa, Fixture fb) {
        if(fa.getUserData() == BodyID.ACTION_TRIGGER && fb.getUserData() == BodyID.PLAYER_BODY || fb.getUserData() == BodyID.ACTION_TRIGGER && fa.getUserData() == BodyID.PLAYER_BODY ){
            Entity playerEntity = (fa.getUserData() == BodyID.PLAYER_BODY) ? (Entity)fa.getBody().getUserData() : (Entity)fb.getBody().getUserData();
            Entity actionEntity = (fa.getUserData() == BodyID.ACTION_TRIGGER) ? (Entity)fa.getBody().getUserData() : (Entity)fb.getBody().getUserData();
            PlayerComponent playerComponent = playerEntity.getComponent(PlayerComponent.class);
            if (playerComponent.collisionEntityArray.contains(actionEntity,true)) {
                playerComponent.collisionEntityArray.removeValue(actionEntity,true);
            }
        }
    }
}
