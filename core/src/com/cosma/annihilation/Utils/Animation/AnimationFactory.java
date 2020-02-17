package com.cosma.annihilation.Utils.Animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cosma.annihilation.Annihilation;

import java.util.HashMap;

public class AnimationFactory {

    private HashMap<AnimationId, HashMap<String, Animation<TextureRegion>>> animationMap;

    public AnimationFactory() {
        animationMap = new HashMap<>();

        //Player animations
        HashMap<String, Animation<TextureRegion>> playerAnimationMap = new HashMap<>();

        Animation<TextureRegion> playerWalkAnimation = new Animation<>(0.1f,
                Annihilation.getAssets().get("gfx/player/player_move.atlas",TextureAtlas.class).getRegions()
                ,Animation.PlayMode.LOOP);
        playerAnimationMap.put("WALK",playerWalkAnimation);

        Animation<TextureRegion> playerWalkSmallWeaponAnimation = new Animation<>(0.1f,
                Annihilation.getAssets().get("gfx/player/walk_weapon_small.atlas",TextureAtlas.class).getRegions()
                ,Animation.PlayMode.LOOP);
        playerAnimationMap.put("WALK_WEAPON_SMALL",playerWalkSmallWeaponAnimation);

        Animation<TextureRegion> playerWeaponMpWalkAnimation = new Animation<>(0.1f,
                Annihilation.getAssets().get("gfx/player/player_weapon_mp_walk.atlas",TextureAtlas.class).getRegions()
                ,Animation.PlayMode.LOOP);
        playerAnimationMap.put("WALK_WEAPON_MP",playerWeaponMpWalkAnimation);

        TextureRegion standNoWeapon = new TextureRegion(Annihilation.getAssets().get("gfx/player/player_stand.png",Texture.class));
        Animation<TextureRegion> playerIdleAnimation = new Animation<>(0.1f,standNoWeapon);
        playerAnimationMap.put("IDLE",playerIdleAnimation);

        TextureRegion standWeaponSmall = new TextureRegion(Annihilation.getAssets().get("gfx/player/player_small_weapons_stand.png",Texture.class));
        Animation<TextureRegion> playerIdleWeaponSmallAnimation = new Animation<>(0.1f,standWeaponSmall);
        playerAnimationMap.put("IDLE_WEAPON_SMALL",playerIdleWeaponSmallAnimation);

        TextureRegion jumpNoWeapon = new TextureRegion(Annihilation.getAssets().get("gfx/player/player_jump.png",Texture.class));
        Animation<TextureRegion> playerJumpAnimation = new Animation<>(0.1f,jumpNoWeapon);
        playerAnimationMap.put("JUMP",playerJumpAnimation);

        Animation<TextureRegion> playerMeleeAnimation = new Animation<>(0.1f,Annihilation.getAssets().get("gfx/player/player_melee.atlas",
                TextureAtlas.class).getRegions(),Animation.PlayMode.NORMAL);
        playerAnimationMap.put("MELEE",playerMeleeAnimation);

        //Test enemy animation
        HashMap<String, Animation<TextureRegion>> zombieAnimationMap = new HashMap<>();
        TextureRegion zombieIdleTexture = new TextureRegion(Annihilation.getAssets().get("gfx/textures/enemy.png",Texture.class));
        Animation<TextureRegion> zombieIdle = new Animation<>(0.1f,zombieIdleTexture);
        zombieAnimationMap.put("IDLE",zombieIdle);
        //Hazmat suit npc
        HashMap<String, Animation<TextureRegion>> hazmatAnimationMap = new HashMap<>();
        TextureRegion hazmatIdleTexture = new TextureRegion(Annihilation.getAssets().get("gfx/textures/hazmat_suit_gun_small_stand.png",Texture.class));
        Animation<TextureRegion> hazmatIdle = new Animation<>(0.1f,hazmatIdleTexture);
        hazmatAnimationMap.put("IDLE",hazmatIdle);

        Animation<TextureRegion> hazmatWalkSmallWeaponAnimation = new Animation<>(0.1f,
                Annihilation.getAssets().get("gfx/atlas/hazmat_npc_walk.atlas",TextureAtlas.class).getRegions()
                ,Animation.PlayMode.LOOP);
        hazmatAnimationMap.put("WALK",hazmatWalkSmallWeaponAnimation);

        Animation<TextureRegion>hazmatShootAnimation = new Animation<>(0.1f,
                Annihilation.getAssets().get("gfx/atlas/hazmat_npc_shoot.atlas",TextureAtlas.class).getRegions()
                ,Animation.PlayMode.NORMAL);
        hazmatAnimationMap.put("NPC_SHOOT",hazmatShootAnimation);


        animationMap.put(AnimationId.PLAYER,playerAnimationMap);
        animationMap.put(AnimationId.TEST_ZOMBIE,zombieAnimationMap);
        animationMap.put(AnimationId.HAZMAT_NPC,new NpcHazmat());
        animationMap.put(AnimationId.NPC_SCIENTIST,new NpcScientist());



    }
    public HashMap<String, Animation<TextureRegion>> createAnimationMap(AnimationId animationId){
        if(animationMap.keySet().contains(animationId)){
            return  animationMap.get(animationId);
        }
        Gdx.app.log("MyTag", "animation map not found");
        return null;
    }
    public enum AnimationId{
        PLAYER,TEST_ZOMBIE,HAZMAT_NPC,NPC_SCIENTIST
    }
}
