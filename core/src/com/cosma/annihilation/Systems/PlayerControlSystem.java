package com.cosma.annihilation.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cosma.annihilation.Components.*;
import com.cosma.annihilation.EntityEngine.core.ComponentMapper;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.EntityEngine.core.Family;
import com.cosma.annihilation.EntityEngine.signals.Signal;
import com.cosma.annihilation.EntityEngine.systems.IteratingSystem;
import com.cosma.annihilation.Utils.Constants;
import com.cosma.annihilation.Utils.Enums.GameEvent;

import java.util.ArrayList;

public class PlayerControlSystem extends IteratingSystem {

    private ComponentMapper<PlayerComponent> playerMapper;
    private ComponentMapper<PhysicsComponent> physicsMapper;
    private Signal<GameEvent> signal;
    private ArrayList<GameEvent> gameEventList = new ArrayList<>();
    private RayCastCallback noiseRayCallback;
    // false = left, true = right
    private boolean mouseCursorPosition = false;

    private Entity noiseTestEntity;



    public PlayerControlSystem(World world, Viewport viewport) {
        super(Family.all(PlayerComponent.class).get(), Constants.PLAYER_CONTROL_SYSTEM);
        playerMapper = ComponentMapper.getFor(PlayerComponent.class);
        physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);
        signal = new Signal<>();

        noiseRayCallback = (fixture, point, normal, fraction) -> {
            if (fixture.getBody().getUserData() instanceof Entity && ((Entity) fixture.getBody().getUserData()).getComponent(AiComponent.class) != null) {
                noiseTestEntity = (Entity) fixture.getBody().getUserData();
            }
            return 0;
        };

    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PhysicsComponent playerBody = physicsMapper.get(entity);
        PlayerComponent playerComponent = playerMapper.get(entity);
        SkeletonComponent skeletonComponent = entity.getComponent(SkeletonComponent.class);
        skeletonComponent.skeletonDirection = mouseCursorPosition;

        playerComponent.currentHorizontalSpeed = playerBody.body.getLinearVelocity().x;

        if (entity.getComponent(PlayerComponent.class).numFootContacts >= 1) {
            playerComponent.onGround = true;
        }

        boolean isPlayerControlAvailable = playerComponent.isPlayerControlEnable;
        int x = Gdx.graphics.getWidth();
        mouseCursorPosition = Gdx.input.getX() >= x / 2;


        if(playerComponent.activeTask != null){
            //ai control player
            playerComponent.activeTask.update(entity,deltaTime);
            if(playerComponent.activeTask.isSuccess()){
                playerComponent.activeTask = null;
            }
        }else{
            //normal control
            //prevent slip
            if (!Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT) && playerComponent.onGround) {
                playerBody.body.setLinearVelocity(0, playerBody.body.getLinearVelocity().y);
            }
            //moving on side
            if (playerComponent.canMoveOnSide && playerComponent.onGround && playerComponent.isWeaponHidden && isPlayerControlAvailable) {

                if (Gdx.input.isKeyPressed(Input.Keys.D) || playerComponent.goRight) {
                    float desiredSpeed = playerComponent.velocity;
                    if(playerComponent.isPlayerCrouch){
                        desiredSpeed = desiredSpeed * 0.7f;
                    }
                    Vector2 vec = playerBody.body.getLinearVelocity();
                    float speedX = desiredSpeed - vec.x;
                    float impulse = playerBody.body.getMass() * speedX;
                    playerBody.body.applyLinearImpulse(new Vector2(impulse, 0),
                            playerBody.body.getWorldCenter(), true);
                }
                if (Gdx.input.isKeyPressed(Input.Keys.A) || playerComponent.goLeft) {
                    float desiredSpeed = -playerComponent.velocity;
                    if(playerComponent.isPlayerCrouch){
                        desiredSpeed = desiredSpeed * 0.7f;
                    }
                    Vector2 vec = playerBody.body.getLinearVelocity();
                    float speedX = desiredSpeed - vec.x;
                    float impulse = playerBody.body.getMass() * speedX;
                    playerBody.body.applyLinearImpulse(new Vector2(impulse, 0),
                            playerBody.body.getWorldCenter(), true);
                }



            }
            // Moving on side with weapon
            if (playerComponent.canMoveOnSide && playerComponent.onGround && !playerComponent.isWeaponHidden && playerComponent.activeWeapon != null && isPlayerControlAvailable) {
                if (Gdx.input.isKeyPressed(Input.Keys.D) || playerComponent.goRight) {
                    float desiredSpeed = playerComponent.velocity * 0.8f;
                    if (!skeletonComponent.skeletonDirection) {
                        desiredSpeed = desiredSpeed * 0.6f;
                    }
                    Vector2 vec = playerBody.body.getLinearVelocity();
                    float speedX = desiredSpeed - vec.x;
                    float impulse = playerBody.body.getMass() * speedX;
                    playerBody.body.applyLinearImpulse(new Vector2(impulse, 0),
                            playerBody.body.getWorldCenter(), true);
                }
                if (Gdx.input.isKeyPressed(Input.Keys.A) || playerComponent.goLeft) {
                    float desiredSpeed = -playerComponent.velocity * 0.8f;
                    if (skeletonComponent.skeletonDirection) {
                        desiredSpeed = desiredSpeed * 0.7f;
                    }
                    Vector2 vec = playerBody.body.getLinearVelocity();
                    float speedX = desiredSpeed - vec.x;
                    float impulse = playerBody.body.getMass() * speedX;
                    playerBody.body.applyLinearImpulse(new Vector2(impulse, 0),
                            playerBody.body.getWorldCenter(), true);
                }
            }
            // Jumping
            if (playerComponent.onGround && playerComponent.canJump && playerComponent.isWeaponHidden && isPlayerControlAvailable && playerComponent.jump) {

                playerComponent.onGround = false;
                playerComponent.canJump = false;
                skeletonComponent.setSkeletonAnimation(true, "jump", 0, false);

                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        float x;
                        if (skeletonComponent.skeletonDirection) {
                            x = 0;
                        } else x = -0;
                        playerBody.body.applyLinearImpulse(new Vector2(x, 75),
                                playerBody.body.getWorldCenter(), true);

                    }
                }, 0.2f);
                playerComponent.jump = false;
            }
            //Climbing
            if (playerComponent.climbing) {
                skeletonComponent.skeletonDirection = true;
                playerBody.body.setGravityScale(0);
                playerBody.body.getFixtureList().get(0).setSensor(true);
                playerBody.body.setLinearVelocity(new Vector2(0, 0));
                skeletonComponent.climbUp();

                if (Gdx.input.isKeyPressed(Input.Keys.W) || playerComponent.goUp) {
                    playerComponent.climbing = true;
                    if (playerBody.body.getLinearVelocity().x == 0f) {
                        playerBody.body.setLinearVelocity(new Vector2(0, 1));
                    }
                }
                if (Gdx.input.isKeyPressed(Input.Keys.S) || playerComponent.goDown) {
                    playerComponent.climbing = true;
                    playerBody.body.setLinearVelocity(new Vector2(0, -1));
                    if (playerBody.body.getPosition().y <= playerComponent.climbingStartPosition.y) {
                        endClimb(playerBody,playerComponent);
                    }
                }
                if (playerBody.body.getLinearVelocity().y == 0) {
                    skeletonComponent.animationState.setTimeScale(0);
                }else{
                    skeletonComponent.animationState.setTimeScale(1);
                }
                if (playerBody.body.getPosition().y >= playerComponent.climbingTargetPosition.y) {
                    endClimb(playerBody,playerComponent);
                }
            }
        }
        //apply correct animation
        if (playerBody.body.getLinearVelocity().x != 0 && playerComponent.onGround && playerComponent.canJump ) {
            //move animations
            if(playerComponent.isPlayerCrouch){
                if (playerComponent.isWeaponHidden) {
                    skeletonComponent.setSkeletonAnimation(false, "walk_crouch", 0, true);
                } else {
                    skeletonComponent.setSkeletonAnimation(false, "walk_crouch", 3, true);
                }
            }else{
                if (playerComponent.isWeaponHidden) {
                    skeletonComponent.setSkeletonAnimation(false, "walk", 0, true);
                } else {
                    skeletonComponent.setSkeletonAnimation(false, "weapon_walk", 3, true);
                }
            }
        }
        if (playerBody.body.getLinearVelocity().x == 0 && playerComponent.onGround && playerComponent.canJump && !playerComponent.climbing) {
            //idle animations
            skeletonComponent.animationState.setEmptyAnimation(3, 0.1f);
            if(playerComponent.isPlayerCrouch){
                skeletonComponent.animationState.setEmptyAnimation(3, 0.1f);
                skeletonComponent.setSkeletonAnimation(false, "idle_crouch", 0, true);
            }else{
                skeletonComponent.setSkeletonAnimation(false, "idle", 0, true);
            }
        }
        skeletonComponent.animationState.apply(skeletonComponent.skeleton);

        //        //simulatingPlayerNoise
//        if (playerBody.body.getLinearVelocity().x != 0 && !playerComponent.isPlayerCrouch) {
//
//            world.rayCast(noiseRayCallback, playerBody.body.getPosition().x, playerBody.body.getPosition().y,
//                    playerBody.body.getPosition().x + 3, playerBody.body.getPosition().y);
//
//            world.rayCast(noiseRayCallback, playerBody.body.getPosition().x, playerBody.body.getPosition().y,
//                    playerBody.body.getPosition().x - 3, playerBody.body.getPosition().y);
//            if (noiseTestEntity != null) {
//                AnimationComponent animationComponentAi = noiseTestEntity.getComponent(AnimationComponent.class);
//                AiComponent aiComponent = noiseTestEntity.getComponent(AiComponent.class);
//                if (animationComponentAi.spriteDirection == animationComponent.spriteDirection) {
//                    aiComponent.isHearEnemy = true;
//                    aiComponent.enemyPosition = playerBody.body.getPosition();
//                    noiseTestEntity = null;
//                }
//            }
//        }
    }

    public void endClimb(PhysicsComponent playerPhysicsComponent, PlayerComponent playerComponent){
        playerComponent.climbing = false;
        playerPhysicsComponent.body.getFixtureList().get(0).setSensor(false);
        playerPhysicsComponent.body.setGravityScale(1);
        playerComponent.canMoveOnSide = true;
        playerComponent.canUseWeapon = true;
    }
}