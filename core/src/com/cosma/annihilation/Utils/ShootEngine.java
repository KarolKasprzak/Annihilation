package com.cosma.annihilation.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cosma.annihilation.Annihilation;
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

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public ShootEngine(World world, Viewport viewport, FxEntityCreator fxEntityCreator) {
        this.fxEntityCreator = fxEntityCreator;
        this.world = world;
        this.viewport = viewport;
        vector2tmp = new Vector2();
    }

    // based on movement, position, weapon accuracy, burst time and distance
    private Vector2 calculateFinalShootTargetPosition(PlayerComponent playerComponent) {


        float movement = Math.abs(MathUtils.norm(0, 2, playerComponent.currentHorizontalSpeed) * 0.1f);
        float weapon = playerComponent.activeWeapon.getAccuracy() * 0.2f;
        float burst = 0;
        if (playerComponent.activeWeapon.isAutomatic()) {
            burst = MathUtils.norm(0, playerComponent.activeWeapon.getMaxAmmoInClip(), burstSize) * 1.2f;
        }

//        float accuracy = 1 - movement - weapon - burst + MathUtils.random(0,0.3f);
        float accuracy = Math.abs(0 + movement - weapon + burst + MathUtils.random(0, 0.3f));

        vector2tmp.set(Gdx.input.getX(),Gdx.input.getY());
        viewport.unproject(vector2tmp);

        vector2tmp.set(MathUtils.random(vector2tmp.x - accuracy, vector2tmp.x + accuracy),MathUtils.random(vector2tmp.y - accuracy, vector2tmp.y + accuracy));





//        System.out.println("movement  " + movement);
//        System.out.println("burst  " + burst);
//        System.out.println("weapon  " + weapon);
        System.out.println("accuracy  " + accuracy);
//        float x = MathUtils.random()




        return vector2tmp;
    }


    public void update(PlayerComponent playerComponent, SkeletonComponent skeletonComponent, PlayerInventoryComponent playerInventoryComponent, float delta) {
        weaponReloadTimer += delta;

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
        if (playerComponent.isLeftMouseButtonPressed && isWeaponReadyToShoot) {

            burstSize = burstSize + 1;
            Sound sound = Annihilation.getAssets().get("sfx/weapons/cg1.wav");
            sound.play(0.2f);

            weapon.setAmmoInClip(weapon.getAmmoInClip() - 1);
            weaponReloadTimer = 0;
            isWeaponReadyToShoot = false;
            calculateFinalShootTargetPosition(playerComponent);
            spawnBulletHole(vector2tmp.x,vector2tmp.y);

            Bone shellEjector = skeletonComponent.skeleton.findBone("shell_ejector");

            engine.addEntity(fxEntityCreator.createBulletShellEntity(shellEjector.getWorldX(),shellEjector.getWorldY()));

        }
    }




    void spawnBulletHole(float x, float y) {
        if (engine.isPointInDrawField(x, y)) {
            System.out.println("create");
            Entity entity = new Entity();
            TextureComponent textureComponent = new TextureComponent();
            textureComponent.textureRegion = Annihilation.getTextureRegion("fx_textures", "bullet_hole");
            textureComponent.normalTexture = Annihilation.getAssets().get("gfx/atlas/fx_textures_n.png", Texture.class);
            entity.add(textureComponent);

            DrawOrderComponent drawOrderComponent = new DrawOrderComponent();
            drawOrderComponent.drawOrder = 3;
            entity.add(drawOrderComponent);

            SpriteComponent spriteComponent = new SpriteComponent();
            spriteComponent.x = x;
            spriteComponent.y = y;
            spriteComponent.createRectangle(textureComponent);
            spriteComponent.drawDiffuse = false;
            entity.add(spriteComponent);

            ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.all(SpriteComponent.class).get());
            if (entities.size() < 1) {
                engine.addEntity(entity);
            } else {
                boolean overlaps = false;
                for (Entity spriteEntity : entities) {
                    if (spriteComponent.rectangle.overlaps(spriteEntity.getComponent(SpriteComponent.class).rectangle)) {
                        overlaps = true;
                        break;
                    }
                }
                if (!overlaps) {
                    engine.addEntity(entity);
                }
            }
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
