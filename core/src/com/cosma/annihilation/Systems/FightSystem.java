package com.cosma.annihilation.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Box2dLight.RayHandler;
import com.cosma.annihilation.Components.*;
import com.cosma.annihilation.EntityEngine.core.ComponentMapper;
import com.cosma.annihilation.EntityEngine.core.Engine;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.EntityEngine.core.Family;
import com.cosma.annihilation.EntityEngine.systems.IteratingSystem;
import com.cosma.annihilation.Items.ItemType;
import com.cosma.annihilation.Utils.Constants;
import com.cosma.annihilation.Utils.FxEntityCreator;
import com.cosma.annihilation.Utils.ShootEngine;
import com.esotericsoftware.spine.Bone;

public class FightSystem extends IteratingSystem {
    private ComponentMapper<PhysicsComponent> bodyMapper;
    private ComponentMapper<PlayerComponent> playerMapper;
    private ComponentMapper<PlayerInventoryComponent> playerDateMapper;
    private ComponentMapper<PlayerStatsComponent> playerStatsMapper;
    private ComponentMapper<SkeletonComponent> skeletonMapper;


    private Vector2 vector2temp = new Vector2();
    private Viewport viewport;
    private ShootEngine shootEngine;

    public FightSystem(World world, RayHandler rayHandler, Viewport viewport) {
        super(Family.all(PlayerComponent.class).get(), Constants.SHOOTING_SYSTEM);
        this.viewport = viewport;
        FxEntityCreator fxEntityCreator = new FxEntityCreator(world);
        shootEngine = new ShootEngine(world, viewport,fxEntityCreator,rayHandler);
        bodyMapper = ComponentMapper.getFor(PhysicsComponent.class);
        playerMapper = ComponentMapper.getFor(PlayerComponent.class);
        playerDateMapper = ComponentMapper.getFor(PlayerInventoryComponent.class);
        playerStatsMapper = ComponentMapper.getFor(PlayerStatsComponent.class);
        skeletonMapper = ComponentMapper.getFor(SkeletonComponent.class);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        shootEngine.setEngine(engine);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerComponent playerComponent = playerMapper.get(entity);
        PlayerInventoryComponent playerInventoryComponent = playerDateMapper.get(entity);
        PlayerStatsComponent statsComponent = playerStatsMapper.get(entity);
        PhysicsComponent physicsComponent = bodyMapper.get(entity);
        SkeletonComponent skeletonComponent = skeletonMapper.get(entity);

        Body body = bodyMapper.get(entity).body;

        Bone root = skeletonComponent.skeleton.getRootBone();
        vector2temp.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(vector2temp);
        vector2temp.y = MathUtils.clamp(vector2temp.y,skeletonComponent.skeleton.getY()+0.3f,skeletonComponent.skeleton.getY()+2.8f);
        if(skeletonComponent.skeletonDirection){
            vector2temp.x = MathUtils.clamp(vector2temp.x,skeletonComponent.skeleton.getX()+0.8f,skeletonComponent.skeleton.getX()+9f);
        }else{
            vector2temp.x = MathUtils.clamp(vector2temp.x,skeletonComponent.skeleton.getX()-9,skeletonComponent.skeleton.getX()-0.8f);
        }

        Bone bodyTarget = skeletonComponent.skeleton.findBone("bodyTarget");
        vector2temp.set(root.worldToLocal(vector2temp));
        bodyTarget.setPosition(vector2temp.x, vector2temp.y);

        if (playerComponent.prepareWeapon && playerComponent.isPlayerControlEnable) {
            playerComponent.isWeaponHidden = !playerComponent.isWeaponHidden;
            if (!playerComponent.isWeaponHidden) {
                skeletonComponent.setSkeletonAnimation(false, playerComponent.activeWeapon.getHoldAnimation(), 2, true);
            } else {
                Annihilation.setArrowCursor();
                skeletonComponent.skeleton.findSlot("weapon_pistol").setAttachment(null);
                skeletonComponent.skeleton.findSlot("weapon_rifle").setAttachment(null);
                skeletonComponent.animationState.addEmptyAnimation(2, 0.3f, 0);
            }
        }
        playerComponent.prepareWeapon = false;

        if(!playerComponent.isWeaponHidden){
//            if (playerComponent.activeWeapon.getCategory() == ItemType.MELEE && playerComponent.canUseWeapon) {
//
//        }
                if (playerComponent.activeWeapon.getCategory() == ItemType.GUNS && playerComponent.canUseWeapon) {
                    shootEngine.update(playerComponent,skeletonComponent,playerInventoryComponent,deltaTime);
                }
        }

        if (!playerComponent.isWeaponHidden && playerComponent.canShoot) {
            Annihilation.setWeaponCursor();
            skeletonComponent.setSkeletonAnimation(false, playerComponent.activeWeapon.getHoldAnimation(), 2, true);
            Bone rArmTarget = skeletonComponent.skeleton.findBone("r_hand_target");
            Bone lArmTarget = skeletonComponent.skeleton.findBone("l_hand_target");
            Bone flash = skeletonComponent.skeleton.findBone("flash");
            Bone grip = skeletonComponent.skeleton.findBone("grip");
            rArmTarget.setPosition(vector2temp.x, vector2temp.y);
            vector2temp.set(grip.getWorldX(), grip.getWorldY());
            vector2temp.set(root.worldToLocal(vector2temp));
            lArmTarget.setPosition(vector2temp.x, vector2temp.y);
        }

        skeletonComponent.skeleton.updateWorldTransform();
//        if (!playerComponent.isWeaponHidden) {
//            world.rayCast(callback, body.getPosition(), raycastEnd.set(body.getPosition().x + 15 * direction, body.getPosition().y));
//            if (targetEntity != null) {
//                PhysicsComponent targetBody = targetEntity.getComponent(PhysicsComponent.class);
//                Vector3 worldPosition = worldCamera.project(new Vector3(targetBody.body.getPosition().x, targetBody.body.getPosition().y, 0));
//                batch.setProjectionMatrix(camera.combined);
//                batch.begin();
//                //show accuracy on target
//                font.draw(batch, Math.round(calculateAttackAccuracyFloat() * 100) + "%", worldPosition.x + 45, worldPosition.y + 50);
//                batch.end();
//            }
//        }
    }

}


