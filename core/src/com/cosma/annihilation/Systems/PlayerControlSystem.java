package com.cosma.annihilation.Systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cosma.annihilation.Components.*;
import com.cosma.annihilation.Utils.Constants;
import com.cosma.annihilation.Utils.Animation.AnimationStates;
import com.cosma.annihilation.Utils.Enums.GameEvent;
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Bone;
import java.util.ArrayList;

public class PlayerControlSystem extends IteratingSystem implements InputProcessor {

    private ComponentMapper<PlayerComponent> playerMapper;
    private ComponentMapper<BodyComponent> bodyMapper;
    private ComponentMapper<AnimationComponent> animationMapper;
    private Signal<GameEvent> signal;
    private ArrayList<GameEvent> gameEventList = new ArrayList<>();
    private RayCastCallback noiseRayCallback;
    private OrthographicCamera camera;
    // false = left, true = right
    private boolean mouseCursorPosition = false;
    private World world;
    private Entity noiseTestEntity;
    private Vector2 temp1 = new Vector2(), temp2 = new Vector2(), temp3 = new Vector2();
    private Viewport viewport;
    private boolean isPlayerControlAvailable = true;

    public PlayerControlSystem(World world, OrthographicCamera camera, Viewport viewport) {
        super(Family.all(PlayerComponent.class).get(), Constants.PLAYER_CONTROL_SYSTEM);
        this.world = world;
        this.camera = camera;
        this.viewport = viewport;
        playerMapper = ComponentMapper.getFor(PlayerComponent.class);
        bodyMapper = ComponentMapper.getFor(BodyComponent.class);
        animationMapper = ComponentMapper.getFor(AnimationComponent.class);
        signal = new Signal<GameEvent>();

        noiseRayCallback = (fixture, point, normal, fraction) -> {
            if (fixture.getBody().getUserData() instanceof Entity && ((Entity) fixture.getBody().getUserData()).getComponent(AiComponent.class) != null) {
                noiseTestEntity = (Entity) fixture.getBody().getUserData();
            }
            return 0;
        };

    }

    @Override
    public void update(float deltaTime) {
        if(isPlayerControlAvailable){
            super.update(deltaTime);
        }
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

        for (GameEvent gameEvent : gameEventList) {
            signal.dispatch(gameEvent);
        }
        gameEventList.clear();


        if (!Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT) && playerComponent.onGround && animationComponent.isAnimationFinish) {
            playerBody.body.setLinearVelocity(0, playerBody.body.getLinearVelocity().y);
            animationComponent.animationState = AnimationStates.IDLE;
            if (!playerComponent.isWeaponHidden) {
                animationComponent.animationState = AnimationStates.IDLE_WEAPON_SMALL;
            }
        }

        if (!Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT) && playerComponent.onGround) {
            playerBody.body.setLinearVelocity(0, playerBody.body.getLinearVelocity().y);
        }

        // Jumping
        if (playerComponent.onGround && playerComponent.canJump && playerComponent.isWeaponHidden) {

            if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || playerComponent.goUp) {
                float x;
                if (animationComponent.spriteDirection) {
                    x = 30;
                } else x = -30;
                playerBody.body.applyLinearImpulse(new Vector2(x, 10),
                        playerBody.body.getWorldCenter(), true);
            }
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
        if (playerComponent.canMoveOnSide && playerComponent.onGround && playerComponent.isWeaponHidden) {
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
        if (playerComponent.canMoveOnSide && playerComponent.onGround && !playerComponent.isWeaponHidden && playerComponent.activeWeapon != null) {
            if (Gdx.input.isKeyPressed(Input.Keys.D) || playerComponent.goRight) {
                float desiredSpeed = playerComponent.velocity * 0.8f;
                if (animationComponent.spriteDirection) {
//                    setPlayerAnimation(playerComponent, animationComponent);
                } else {
//                    setPlayerAnimation(playerComponent, animationComponent);
                    desiredSpeed = desiredSpeed * 0.7f;
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


        if (playerBody.body.getLinearVelocity().x != 0) {
            setSkeletonAnimation(false, "walk", skeletonComponent.animationState, 0);
        }
        if (playerBody.body.getLinearVelocity().x == 0) {
            setSkeletonAnimation(true, "idle", skeletonComponent.animationState, 0);
        }
        skeletonComponent.animationState.apply(skeletonComponent.skeleton);

//        if (playerComponent.isWeaponHidden) {
//
//            skeletonComponent.animationState.clearTrack(1);
//            skeletonComponent.skeleton.findSlot("weapon").setAttachment(null);
//
//        } else {

        Bone headBone = skeletonComponent.skeleton.findBone("head");
        Bone rightArm = skeletonComponent.skeleton.findBone("r_arm");
        Bone leftArm = skeletonComponent.skeleton.findBone("l_arm");
        Bone leftHand = skeletonComponent.skeleton.findBone("l_hand");
        Bone rightHand = skeletonComponent.skeleton.findBone("r_hand");
        Bone upperBody = skeletonComponent.skeleton.findBone("u_body");
        Bone weaponBone = skeletonComponent.skeleton.findBone("weapon");

        skeletonComponent.skeleton.setAttachment("weapon", "weapon_p38_silent");
        //arm aiming
        setSkeletonAnimation(true, "weapon_pistol_hold", skeletonComponent.animationState, 1);
        skeletonComponent.animationState.apply(skeletonComponent.skeleton);
        Vector2 mouse = temp1.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(mouse);

        rightArm.setRotation(0);
        skeletonComponent.skeleton.updateWorldTransform();

        Vector2 armPosition = temp2.set(rightArm.getWorldX(), rightArm.getWorldY());

        float angle = armPosition.sub(mouse).angle();
        if (!skeletonComponent.skeletonDirection) {
            angle = -angle;
            angle += rightArm.getWorldRotationX() + 180;

        } else {
            angle -= rightArm.getWorldRotationX() + 180;
        }
        angle += weaponBone.getARotation();
        rightArm.setRotation(angle);

        //head rotation
        float headAngle;
        if (skeletonComponent.skeletonDirection) {
            angle += 360;
            if (angle > 270)
                headAngle = 15 * Interpolation.pow2In.apply((Math.min(1, (angle - 270) / 50f)));
            else
                headAngle = -9 * Interpolation.pow2In.apply((Math.min(1, (angle - 270) / 50f)));
        } else {
            if (angle < 0) {
                angle += 360;
                headAngle = 15 * Interpolation.pow2In.apply((Math.min(1, (angle - 260) / 50f)));
            } else {
                headAngle = -10 * Interpolation.pow2In.apply((Math.min(1, (angle - 260) / 50f)));
            }
        }
        headBone.setRotation(headAngle);
        skeletonComponent.skeleton.updateWorldTransform();
    }
//    }

    public void addListenerSystems() {
        signal.add(getEngine().getSystem(ActionSystem.class));
        signal.add(getEngine().getSystem(ShootingSystem.class));
        signal.add(getEngine().getSystem(UserInterfaceSystem.class));
    }

    private void setSkeletonAnimation(boolean force, String animation, AnimationState animationState, int track) {
        Animation newAnimation = animationState.getData().getSkeletonData().findAnimation(animation);
        AnimationState.TrackEntry current = animationState.getCurrent(track);
        Animation currentAnimation = current == null ? null : current.getAnimation();
        if (force || currentAnimation != newAnimation) {
            animationState.setAnimation(track, animation, true);
        }
    }

    public void setPlayerControlAvailable(boolean playerControlAvailable) {
        isPlayerControlAvailable = playerControlAvailable;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.I || keycode == Input.Keys.ESCAPE) {
            setPlayerControlAvailable(false);
            signal.dispatch(GameEvent.OPEN_MENU);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT && isPlayerControlAvailable) {
            gameEventList.add(GameEvent.ACTION_BUTTON_TOUCH_DOWN);
            gameEventList.add(GameEvent.PERFORM_ACTION);
        }

        if (button == Input.Buttons.RIGHT && isPlayerControlAvailable) {
            gameEventList.add(GameEvent.WEAPON_TAKE_OUT);
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT && isPlayerControlAvailable) {
            gameEventList.add(GameEvent.ACTION_BUTTON_TOUCH_UP);
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        int x = Gdx.graphics.getWidth();
        mouseCursorPosition = screenX >= x / 2;
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}