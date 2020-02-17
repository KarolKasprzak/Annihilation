package com.cosma.annihilation.Utils.Animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.cosma.annihilation.Annihilation;

import java.util.HashMap;

class NpcScientist extends HashMap<String, Animation<TextureRegion>> {

    NpcScientist() {
        TextureRegion textureRegion = new TextureRegion((Texture) Annihilation.getAssets().get("gfx/textures/scientist.png"));
        Array<TextureRegion> textureRegions = new Array<>();
        textureRegions.add(textureRegion);

        //WALK
        Animation<TextureRegion> walkAnimation = new Animation<>(0.1f, textureRegions, Animation.PlayMode.NORMAL);
        this.put("WALK", walkAnimation);
        //IDLE
        Animation<TextureRegion> idleAnimation = new Animation<>(0.1f, textureRegions, Animation.PlayMode.NORMAL);
        this.put("IDLE", idleAnimation);
        //SHOOT
        Animation<TextureRegion> shootAnimation = new Animation<>(0.1f, textureRegions, Animation.PlayMode.NORMAL);
        this.put("NPC_SHOOT", shootAnimation);
        //DEATH_STAND
        Animation<TextureRegion> deathStandAnimation = new Animation<>(0.15f, textureRegions, Animation.PlayMode.NORMAL);
        this.put("DEATH_STAND", deathStandAnimation);

    }
}

