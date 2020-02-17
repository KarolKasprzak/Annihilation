package com.cosma.annihilation.Systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.cosma.annihilation.Components.*;
import com.cosma.annihilation.Utils.Constants;
import com.cosma.annihilation.Utils.EntityEventSignal;
import com.cosma.annihilation.Utils.Animation.AnimationStates;
import com.cosma.annihilation.Utils.Enums.BodyID;
import com.cosma.annihilation.Utils.CollisionID;
import com.cosma.annihilation.Utils.Enums.GameEvent;
import com.cosma.annihilation.Utils.Util;
import com.cosma.annihilation.World.WorldBuilder;


public class CollisionSystem extends IteratingSystem implements ContactListener {

    private Signal<GameEvent> signal;
    private float ladderX;
    private float ladderY;
    private float ladderHeight;
    private ComponentMapper<PlayerComponent> playerMapper;
    private ComponentMapper<BodyComponent> bodyMapper;
    private ComponentMapper<AnimationComponent> animationMapper;
    private Filter goTroughFilter;
    private Filter normalFilter;
    Array<Body> bodiesToRemove;
    private EntityEventSignal entityEventSignal;

    public CollisionSystem(World world) {
        super(Family.all(PlayerComponent.class).get(), Constants.PHYSIC_SYSTEM);
        bodyMapper = ComponentMapper.getFor(BodyComponent.class);
        playerMapper = ComponentMapper.getFor(PlayerComponent.class);
        animationMapper = ComponentMapper.getFor(AnimationComponent.class);

        world.setContactListener(this);
        //Filter for one way wall
        goTroughFilter = new Filter();
        goTroughFilter.maskBits = CollisionID.SCENERY;
        goTroughFilter.groupIndex = -1;
        normalFilter = new Filter();
        normalFilter.categoryBits = CollisionID.SCENERY;
        bodiesToRemove = new Array<>();

        signal = new Signal<>();
        entityEventSignal = new EntityEventSignal();


    }

    public void addListenerSystems(WorldBuilder worldBuilder) {
        signal.add(this.getEngine().getSystem(HealthSystem.class));
        signal.add(worldBuilder);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Body playerBody = bodyMapper.get(entity).body;
        PlayerComponent playerComponent = playerMapper.get(entity);
        AnimationComponent animationComponent = animationMapper.get(entity);

        if (entity.getComponent(PlayerComponent.class).numFootContacts >= 1) {
            playerComponent.onGround = true;

        } else {
            playerComponent.onGround = false;
            animationComponent.animationState = AnimationStates.JUMP;
        }

        //Setting player on ladder center
        if (playerComponent.climbing) {
            float b1 = playerBody.getPosition().x;
            float b2 = ladderX;

            if (b1 == b2) {
            } else {
                if (b1 < (b2 - 0.03f)) {
                    playerBody.setLinearVelocity(1, 0);
                }
                if (b1 > (b2 + 0.03f)) {
                    playerBody.setLinearVelocity(-1, 0);
                }
            }
        }
        //Player go through wall
        if (playerComponent.climbing) {
            System.out.println("1");
            if (playerBody.getPosition().y > ladderY - (ladderHeight / 2 - 2)) {
                System.out.println("2");
                playerBody.getFixtureList().get(0).setFilterData(goTroughFilter);
                if (playerBody.getPosition().y < ladderY - (ladderHeight / 2 - 2)) {
                    System.out.println("3");
                    playerBody.getFixtureList().get(0).setFilterData(normalFilter);
                }
            } else {
                playerBody.getFixtureList().get(0).setFilterData(normalFilter);
            }
        }
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        bulletCollision(fa,fb);
        bulletCollision(fb,fa);

        addEntityToActionList(fa, fb);

        //Bullet shell contact

//        if (fa.getUserData() == BodyID.PLAYER_BODY && fb.getUserData() == BodyID.GATE || fb.getUserData() == BodyID.PLAYER_BODY && fa.getUserData() == BodyID.GATE) {
//            Entity playerEntity;
//            Entity gateEntity;
//            if (fa.getUserData() == BodyID.PLAYER_BODY) {
//                playerEntity = (Entity) fa.getBody().getUserData();
//                gateEntity = (Entity) fb.getBody().getUserData();
//            } else {
//                playerEntity = (Entity) fb.getBody().getUserData();
//                gateEntity = (Entity) fa.getBody().getUserData();
//            }
//            playerEntity.getComponent(PlayerComponent.class).mapName = gateEntity.getComponent(GateComponent.class).targetMapPath;
//
//
//            signal.dispatch(GameEvent.PLAYER_GO_TO_NEW_MAP);
//
//
////           playerEntity.getComponent(BodyComponent.class).body.setTransform(gateEntity.getComponent(GateComponent.class).playerPositionOnTargetMap,0);
//
//        }


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
            //Ladder climb down
            if (fb.getUserData() == BodyID.PLAYER_CENTER && fa.getUserData() == BodyID.GROUND ||
                    fa.getUserData() == BodyID.PLAYER_CENTER && fb.getUserData() == BodyID.GROUND) {
                if (playerComponent.climbing) {
                    playerComponent.canMoveOnSide = false;
                }
            }

            if (fb.getUserData() == BodyID.PLAYER_FOOT && fa.getUserData() == BodyID.DESCENT_LADDER || fa.getUserData() == BodyID.PLAYER_FOOT && fb.getUserData() == BodyID.DESCENT_LADDER) {
                playerComponent.canClimbDown = true;
                if (fa.getUserData() == BodyID.DESCENT_LADDER) {
                    ladderX = fa.getBody().getPosition().x;
                    ladderY = fa.getBody().getPosition().y;
                    ladderHeight = (Float) fa.getBody().getUserData();
                } else {

                    ladderX = fb.getBody().getPosition().x;
                    ladderY = fb.getBody().getPosition().y;
                    ladderHeight = (Float) fb.getBody().getUserData();
                }

            }
            //Ladder climb up
            if (fb.getUserData() == BodyID.PLAYER_CENTER && fa.getUserData() == BodyID.LADDER ||
                    fa.getUserData() == BodyID.PLAYER_CENTER && fb.getUserData() == BodyID.LADDER) {

                if (playerComponent.onGround) {
                    playerComponent.canClimb = true;
                    playerComponent.canJump = false;
                }

                if (fa.getUserData() == BodyID.LADDER) {
                    ladderX = fa.getBody().getPosition().x;
                    ladderY = fa.getBody().getPosition().y;
                    ladderHeight = (Float) fa.getBody().getUserData();
                } else {
                    ladderX = fb.getBody().getPosition().x;
                    ladderY = fb.getBody().getPosition().y;
                    ladderHeight = (Float) fb.getBody().getUserData();
                }
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

            //------------------------Ladder contacts--------------
            if (fb.getUserData() == BodyID.PLAYER_CENTER && fa.getUserData() == BodyID.GROUND ||
                    fa.getUserData() == BodyID.PLAYER_CENTER && fb.getUserData() == BodyID.GROUND) {

                playerComponent.canMoveOnSide = true;
                playerComponent.canJump = false;
                delayJump(0.4f, playerComponent);
            }

            if (fb.getUserData() == BodyID.PLAYER_FOOT && fa.getUserData() == BodyID.DESCENT_LADDER || fa.getUserData() == BodyID.PLAYER_FOOT && fb.getUserData() == BodyID.DESCENT_LADDER) {
                playerComponent.canClimbDown = false;

            }

            //Ladder climb
            if (fb.getUserData() == BodyID.PLAYER_CENTER && fa.getUserData() == BodyID.LADDER ||
                    fa.getUserData() == BodyID.PLAYER_CENTER && fb.getUserData() == BodyID.LADDER) {
                delayJump(0.4f, playerComponent);
                playerComponent.canClimb = false;
                playerComponent.climbing = false;
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

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
                    bodiesToRemove.add(fixture.getBody());
                    getEngine().removeEntity((Entity) fixture.getBody().getUserData());
                    fixture.getBody().setUserData(null);
                }
            }
        }, 3);

    }

    private void delayJump(float delay, PlayerComponent playerComponent) {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                playerComponent.canJump = true;
            }
        }, delay);
    }

    private void bulletCollision(Fixture fa, Fixture fb) {
        if (fa.getUserData() == BodyID.BULLET) {
            if (!bodiesToRemove.contains(fa.getBody(), true)) {
                bodiesToRemove.add(fa.getBody());
                getEngine().removeEntity((Entity) fa.getBody().getUserData());
            }
        }
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
            if (!playerComponent.collisionEntityList.contains(actionEntity)) {
                  playerComponent.collisionEntityList.add(actionEntity);
            }

        }
    }

    private void removeEntityFromActionList(Fixture fa, Fixture fb) {
        if(fa.getUserData() == BodyID.ACTION_TRIGGER && fb.getUserData() == BodyID.PLAYER_BODY || fb.getUserData() == BodyID.ACTION_TRIGGER && fa.getUserData() == BodyID.PLAYER_BODY ){
            Entity playerEntity = (fa.getUserData() == BodyID.PLAYER_BODY) ? (Entity)fa.getBody().getUserData() : (Entity)fb.getBody().getUserData();
            Entity actionEntity = (fa.getUserData() == BodyID.ACTION_TRIGGER) ? (Entity)fa.getBody().getUserData() : (Entity)fb.getBody().getUserData();
            PlayerComponent playerComponent = playerEntity.getComponent(PlayerComponent.class);
            if (playerComponent.collisionEntityList.contains(actionEntity)) {
                playerComponent.collisionEntityList.remove(actionEntity);
                if(Util.hasComponent(actionEntity,TextureComponent.class)){
                    actionEntity.getComponent(TextureComponent.class).renderWithShader = false;
                }
            }
        }
    }
}
