package com.cosma.annihilation.Systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cosma.annihilation.Components.*;
import com.cosma.annihilation.Utils.Constants;
import com.cosma.annihilation.Utils.Animation.AnimationStates;
import com.cosma.annihilation.Utils.Enums.GameEvent;

import java.util.ArrayList;

public class PlayerControlSystem extends IteratingSystem {

    private ComponentMapper<PlayerComponent> playerMapper;
    private ComponentMapper<BodyComponent> bodyMapper;
    private ComponentMapper<AnimationComponent> animationMapper;
    private Signal<GameEvent> signal;
    private ArrayList<GameEvent> gameEventList = new ArrayList<>();
    private RayCastCallback noiseRayCallback;
    // false = left, true = right
    private boolean mouseCursorPosition = false;
    private World world;
    private Entity noiseTestEntity;



    public PlayerControlSystem(World world, Viewport viewport) {
        super(Family.all(PlayerComponent.class).get(), Constants.PLAYER_CONTROL_SYSTEM);
        this.world = world;
        playerMapper = ComponentMapper.getFor(PlayerComponent.class);
        bodyMapper = ComponentMapper.getFor(BodyComponent.class);
        animationMapper = ComponentMapper.getFor(AnimationComponent.class);
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
        BodyComponent playerBody = bodyMapper.get(entity);
        PlayerComponent playerComponent = playerMapper.get(entity);
        AnimationComponent animationComponent = animationMapper.get(entity);
        TextureComponent textureComponent = entity.getComponent(TextureComponent.class);
        SkeletonComponent skeletonComponent = entity.getComponent(SkeletonComponent.class);
        animationComponent.spriteDirection = mouseCursorPosition;
        skeletonComponent.skeletonDirection = mouseCursorPosition;
        textureComponent.direction = mouseCursorPosition;



        boolean isPlayerControlAvailable = playerComponent.isPlayerControlEnable;
        int x = Gdx.graphics.getWidth();
        mouseCursorPosition = Gdx.input.getX() >= x / 2;

        if (!Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT) && playerComponent.onGround) {
            playerBody.body.setLinearVelocity(0, playerBody.body.getLinearVelocity().y);
            animationComponent.animationState = AnimationStates.IDLE;
            if (!playerComponent.isWeaponHidden) {
                animationComponent.animationState = AnimationStates.IDLE_WEAPON_SMALL;
            }
        }
        // Jumping
        if (playerComponent.onGround && playerComponent.canJump && playerComponent.isWeaponHidden && isPlayerControlAvailable && playerComponent.jump) {

            playerComponent.onGround = false;
            playerComponent.canJump = false;
            skeletonComponent.setSkeletonAnimation(true, "jump",0, false);

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    float x;
                    if (animationComponent.spriteDirection) {
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
            playerComponent.canJump = false;
            playerBody.body.setGravityScale(0);
            playerBody.body.setLinearVelocity(new Vector2(0, 0));
        } else playerBody.body.setGravityScale(1);


        if (playerComponent.canClimb && playerBody.body.getLinearVelocity().x == 0) {
            if (Gdx.input.isKeyPressed(Input.Keys.W) || playerComponent.goUp) {
                playerComponent.climbing = true;
                if (playerBody.body.getLinearVelocity().x == 0f) {
                    playerBody.body.setLinearVelocity(new Vector2(0, 1));
                }
            }
        }

        if (playerComponent.canClimb || playerComponent.canClimbDown) {
            if (Gdx.input.isKeyPressed(Input.Keys.S) || playerComponent.goDown) {
                playerComponent.climbing = true;
                playerBody.body.setLinearVelocity(new Vector2(0, -1));
            }
        }

        //Stealth mode
//        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ) {
//            //TODO
//        }

        //Moving on side
        if (playerComponent.canMoveOnSide && playerComponent.onGround && playerComponent.isWeaponHidden && isPlayerControlAvailable) {
            if (Gdx.input.isKeyPressed(Input.Keys.D) || playerComponent.goRight) {
                float desiredSpeed = playerComponent.velocity;
                playerComponent.climbing = false;
                if (animationComponent.spriteDirection) {
                    animationComponent.animationState = AnimationStates.WALK;
                } else {
                    animationComponent.animationState = AnimationStates.WALK;
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
                playerComponent.climbing = false;
                if (animationComponent.spriteDirection) {
                    animationComponent.animationState = AnimationStates.WALK;
                    desiredSpeed = desiredSpeed * 0.7f;
                } else {
                    animationComponent.animationState = AnimationStates.WALK;
                }
                Vector2 vec = playerBody.body.getLinearVelocity();
                float speedX = desiredSpeed - vec.x;
                float impulse = playerBody.body.getMass() * speedX;
                playerBody.body.applyLinearImpulse(new Vector2(impulse, 0),
                        playerBody.body.getWorldCenter(), true);
            }
        }
        //Moving on side with weapon
        if (playerComponent.canMoveOnSide && playerComponent.onGround && !playerComponent.isWeaponHidden && playerComponent.activeWeapon != null && isPlayerControlAvailable) {
            if (Gdx.input.isKeyPressed(Input.Keys.D) || playerComponent.goRight) {
                float desiredSpeed = playerComponent.velocity * 0.8f;
                if (!skeletonComponent.skeletonDirection) {
                    desiredSpeed = desiredSpeed * 0.6f;
                }
                Vector2 vec = playerBody.body.getLinearVelocity();
                playerComponent.climbing = false;
                float speedX = desiredSpeed - vec.x;
                float impulse = playerBody.body.getMass() * speedX;
                playerBody.body.applyLinearImpulse(new Vector2(impulse, 0),
                        playerBody.body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A) || playerComponent.goLeft) {
                float desiredSpeed = -playerComponent.velocity * 0.8f;
                if (animationComponent.spriteDirection) {
//                    setPlayerAnimation(playerComponent, animationComponent);
                    desiredSpeed = desiredSpeed * 0.7f;
                } else {
//                    setPlayerAnimation(playerComponent, animationComponent);
                }
                Vector2 vec = playerBody.body.getLinearVelocity();
                float speedX = desiredSpeed - vec.x;
                playerComponent.climbing = false;
                float impulse = playerBody.body.getMass() * speedX;
                playerBody.body.applyLinearImpulse(new Vector2(impulse, 0),
                        playerBody.body.getWorldCenter(), true);
            }
        }

        //simulatingPlayerNoise
        if (playerBody.body.getLinearVelocity().x != 0 && !playerComponent.isPlayerCrouch) {

            world.rayCast(noiseRayCallback, playerBody.body.getPosition().x, playerBody.body.getPosition().y,
                    playerBody.body.getPosition().x + 3, playerBody.body.getPosition().y);

            world.rayCast(noiseRayCallback, playerBody.body.getPosition().x, playerBody.body.getPosition().y,
                    playerBody.body.getPosition().x - 3, playerBody.body.getPosition().y);
            if (noiseTestEntity != null) {
                AnimationComponent animationComponentAi = noiseTestEntity.getComponent(AnimationComponent.class);
                AiComponent aiComponent = noiseTestEntity.getComponent(AiComponent.class);
                if (animationComponentAi.spriteDirection == animationComponent.spriteDirection) {
                    aiComponent.isHearEnemy = true;
                    aiComponent.enemyPosition = playerBody.body.getPosition();
                    noiseTestEntity = null;
                }
            }
        }

        if (playerBody.body.getLinearVelocity().x != 0 && playerComponent.onGround && playerComponent.canJump) {
            if(playerComponent.isWeaponHidden){
                skeletonComponent.setSkeletonAnimation(false, "walk", 0, true);
            }else{
                skeletonComponent.setSkeletonAnimation(false, "weapon_walk", 3, true);
            }
        }
        if (playerBody.body.getLinearVelocity().x == 0 && playerComponent.onGround && playerComponent.canJump) {
            skeletonComponent.animationState.setEmptyAnimation(3,0.1f);
            skeletonComponent.setSkeletonAnimation(false, "idle",0, true);
        }
        skeletonComponent.animationState.apply(skeletonComponent.skeleton);






//        Bone headBone = skeletonComponent.skeleton.findBone("head");
//        Bone rightArm = skeletonComponent.skeleton.findBone("r_arm");
//        Bone leftArm = skeletonComponent.skeleton.findBone("l_arm");
//        Bone leftHand = skeletonComponent.skeleton.findBone("l_hand");
//        Bone rightHand = skeletonComponent.skeleton.findBone("r_hand");
//        Bone upperBody = skeletonComponent.skeleton.findBone("u_body");
//        Bone weaponBone = skeletonComponent.skeleton.findBone("weapon");
//
//        skeletonComponent.skeleton.setAttachment("weapon", "weapon_p38_silent");
//
//        //arm aiming
//        setSkeletonAnimation(true, "weapon_pistol_hold", skeletonComponent.animationState, 1);
//        skeletonComponent.animationState.apply(skeletonComponent.skeleton);
//        Vector2 mouse = temp1.set(Gdx.input.getX(), Gdx.input.getY());
//        viewport.unproject(mouse);
//
//        rightArm.setRotation(0);
//        skeletonComponent.skeleton.updateWorldTransform();
//
//        Vector2 armPosition = temp2.set(rightArm.getWorldX(), rightArm.getWorldY());
//
//        float angle = armPosition.sub(mouse).angle();
//        if (!skeletonComponent.skeletonDirection) {
//            angle = -angle;
//            angle += rightArm.getWorldRotationX() + 180;
//
//        } else {
//            angle -= rightArm.getWorldRotationX() + 180;
//        }
//        angle += weaponBone.getARotation();
//        rightArm.setRotation(angle);
//
//        //head rotation
//        float headAngle;
//        if (skeletonComponent.skeletonDirection) {
//            angle += 360;
//            if (angle > 270)
//                headAngle = 15 * Interpolation.pow2In.apply((Math.min(1, (angle - 270) / 50f)));
//            else
//                headAngle = -9 * Interpolation.pow2In.apply((Math.min(1, (angle - 270) / 50f)));
//        } else {
//            if (angle < 0) {
//                angle += 360;
//                headAngle = 15 * Interpolation.pow2In.apply((Math.min(1, (angle - 260) / 50f)));
//            } else {
//                headAngle = -10 * Interpolation.pow2In.apply((Math.min(1, (angle - 260) / 50f)));
//            }
//        }
//        headBone.setRotation(headAngle);
//        skeletonComponent.skeleton.updateWorldTransform();
    }
}