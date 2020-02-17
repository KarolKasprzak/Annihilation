package com.cosma.annihilation.Utils.Animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cosma.annihilation.Annihilation;

import java.util.HashMap;

 class NpcHazmat extends HashMap<String,Animation<TextureRegion>> {

     NpcHazmat(){
        TextureAtlas textureAtlas = Annihilation.getAssets().get("gfx/atlas/hazmat_suit_pistol/texture.atlas");

        //WALK
        Animation<TextureRegion> walkAnimation = new Animation<>(0.1f,textureAtlas.findRegions("walk"),Animation.PlayMode.LOOP);
        System.out.println("Key:"+walkAnimation.getKeyFrames().length);
        this.put("WALK",walkAnimation);
        //IDLE
        Animation<TextureRegion> idleAnimation = new Animation<>(0.1f,textureAtlas.findRegions("idle"),Animation.PlayMode.LOOP);
        this.put("IDLE",idleAnimation);
        //SHOOT
        Animation<TextureRegion> shootAnimation = new Animation<>(0.1f,textureAtlas.findRegions("shoot"),Animation.PlayMode.NORMAL);
        this.put("NPC_SHOOT",shootAnimation);
        //DEATH_STAND
        Animation<TextureRegion> deathStandAnimation = new Animation<>(0.15f,textureAtlas.findRegions("die_stand"),Animation.PlayMode.NORMAL);
        this.put("DEATH_STAND",deathStandAnimation);

    }
}

