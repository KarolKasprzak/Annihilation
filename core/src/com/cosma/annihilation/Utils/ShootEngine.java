package com.cosma.annihilation.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Box2dLight.PointLight;
import com.cosma.annihilation.Box2dLight.RayHandler;
import com.cosma.annihilation.Components.*;
import com.cosma.annihilation.EntityEngine.core.Engine;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.EntityEngine.core.Family;
import com.cosma.annihilation.EntityEngine.utils.ImmutableArray;
import com.cosma.annihilation.Items.Item;
import com.esotericsoftware.spine.Bone;

public class ShootEngine {
    private World world;
    private Engine engine;
    private Viewport viewport;
    private float weaponReloadTimer = 0;
    private int burstSize = 0;
    private Vector2 vector2tmp;
    private boolean isWeaponReadyToShoot = false;
    private FxEntityCreator fxEntityCreator;
    private PointLight weaponLight;

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public ShootEngine(World world, Viewport viewport, FxEntityCreator fxEntityCreator, RayHandler rayHandler) {
        this.fxEntityCreator = fxEntityCreator;
        this.world = world;
        this.viewport = viewport;
        vector2tmp = new Vector2();
        weaponLight = new PointLight(rayHandler, 45);
        weaponLight.setColor(Color.GREEN);
        weaponLight.setDistance(1);
        weaponLight.setLightDistanceForShader(0.4f);
        weaponLight.setIntensityForShader(1.5f);
        weaponLight.setLightZPosition(0.02f);
    }

    private void calculateFinalShootTargetPosition(PlayerComponent playerComponent) {
        float movement = Math.abs(MathUtils.norm(0, 2, playerComponent.currentHorizontalSpeed) * 0.1f);
        float weapon = playerComponent.activeWeapon.getAccuracy() * 0.2f;
        float burst = 0;
        if (playerComponent.activeWeapon.isAutomatic()) {
            burst = MathUtils.norm(0, playerComponent.activeWeapon.getMaxAmmoInClip(), burstSize) * 1.2f;
////            int y = Gdx.input.getY()-(int)(Gdx.graphics.getHeight()*0.01f)*burstSize/2;
//            System.out.println(y);
//            Gdx.input.setCursorPosition(Gdx.input.getX(),y);
        }

        float accuracy = Math.abs(0 + movement - weapon + burst + MathUtils.random(0, 0.3f));
        vector2tmp.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(vector2tmp);
        vector2tmp.set(MathUtils.random(vector2tmp.x - accuracy, vector2tmp.x + accuracy), MathUtils.random(vector2tmp.y - accuracy, vector2tmp.y + accuracy));
    }


    public void update(PlayerComponent playerComponent, SkeletonComponent skeletonComponent, PlayerInventoryComponent playerInventoryComponent, float delta) {
        weaponReloadTimer += delta;

        if (weaponReloadTimer > 0.15f) {
            weaponLight.setActive(false);
        }

        Item weapon = playerComponent.activeWeapon;
        if (weaponReloadTimer > weapon.getReloadTime() + 0.25) {
            if (weapon.isAutomatic()) {
                isWeaponReadyToShoot = true;
                weaponReloadTimer = 0;
            }
            if (!playerComponent.isLeftMouseButtonPressed) {
                isWeaponReadyToShoot = true;
                burstSize = 0;
                weaponReloadTimer = 0;
            }
        }

        if (playerComponent.reloadWeapon) {
            weaponReload(playerComponent, playerInventoryComponent, skeletonComponent);
            playerComponent.reloadWeapon = false;
        }

        //shoot
        if (playerComponent.isLeftMouseButtonPressed && isWeaponReadyToShoot && weapon.getAmmoInClip() > 0) {
            weaponLight.setActive(true);
            Bone muzzleBone = skeletonComponent.skeleton.findBone("muzzle");
            weaponLight.setPosition(muzzleBone.getWorldX()+0.25f, muzzleBone.getWorldY());
            burstSize = burstSize + 1;
            Sound sound = Annihilation.getAssets().get("sfx/weapons/cg1.wav");
            sound.play(0.2f);
            weapon.setAmmoInClip(weapon.getAmmoInClip() - 1);
            weaponReloadTimer = 0;
            isWeaponReadyToShoot = false;
            calculateFinalShootTargetPosition(playerComponent);
            engine.addEntity(fxEntityCreator.createBulletEntity(muzzleBone.getWorldX(), muzzleBone.getWorldY(), vector2tmp.x, vector2tmp.y, 20, skeletonComponent.skeletonDirection));

            Bone shellEjector = skeletonComponent.skeleton.findBone("shell_ejector");
            engine.addEntity(fxEntityCreator.createBulletShellEntity(shellEjector.getWorldX(), shellEjector.getWorldY()));

            skeletonComponent.setSkeletonAnimation(false, playerComponent.activeWeapon.getShootAnimation(), 4, false);
            skeletonComponent.animationState.addEmptyAnimation(4, 0.1f, 0f);
        }
    }


    private void weaponReload(PlayerComponent playerComponent, PlayerInventoryComponent playerInventoryComponent, SkeletonComponent skeletonComponent) {
        Item weapon = playerComponent.activeWeapon;
        Item wantedItem = null;
        Array<Item> playerInventory = playerInventoryComponent.inventoryItems;
        for (Item item : playerInventory) {
            if (item.getItemType() == weapon.getAmmoType()) {
                wantedItem = item;
                break;
            }
        }
        if (wantedItem != null) {
            Item finalWantedItem = wantedItem;
            playerComponent.canShoot = false;
            skeletonComponent.setSkeletonAnimation(false, playerComponent.activeWeapon.getReloadAnimation(), 4, false);
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    playerComponent.canShoot = true;
                    skeletonComponent.animationState.addEmptyAnimation(4, 0.2f, 0);
                    if (finalWantedItem.getItemAmount() - weapon.getMaxAmmoInClip() > 0) {
                        finalWantedItem.setItemAmount(finalWantedItem.getItemAmount() - weapon.getMaxAmmoInClip());
                        weapon.setAmmoInClip(weapon.getMaxAmmoInClip());
                    } else {
                        int count = finalWantedItem.getItemAmount() - weapon.getMaxAmmoInClip();
                        count = weapon.getMaxAmmoInClip() + count;
                        weapon.setAmmoInClip(count);
                        playerInventory.removeValue(finalWantedItem, true);
                    }
                }
            }, skeletonComponent.animationState.getCurrent(4).getAnimation().getDuration());
        }
    }
}
