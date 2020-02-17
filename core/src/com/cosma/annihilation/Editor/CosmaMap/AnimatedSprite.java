package com.cosma.annihilation.Editor.CosmaMap;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.utils.Array;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Utils.Constants;

public class AnimatedSprite extends Sprite{

    private float x,y,width,height,angle;
    private String atlasRegionName,atlasPath;
    private TextureRegion textureRegion;
    private  Animation<TextureRegion> animation;
    private boolean flipX = false,flipY = false;
    public boolean isFlipX() {
        return flipX;
    }
    public boolean isFlipY() {
        return flipY;
    }
    private float time = 0;



    public AnimatedSprite() {
    }

     void setTextureRegion(String region, String path) {

                this.animation = new Animation<>(0.1f,Annihilation.getAssets().get(path,TextureAtlas.class).findRegions(region),Animation.PlayMode.LOOP);
                this.textureRegion = animation.getKeyFrame(time);
                this.atlasRegionName= region;
                this.width = textureRegion.getRegionWidth()/ Constants.PPM;
                this.height = textureRegion.getRegionHeight()/Constants.PPM;

        }

        public void updateAnimation(float deltaTime){
            time += deltaTime;
            textureRegion = animation.getKeyFrame(time,true);
        }

    public void setSpritePosition(float x,float y, float angle){
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public void setSpritePosition(float x,float y){
        this.x = x;
        this.y = y;
    }

    public void setSpriteAngle(float angle){
        this.angle = angle;
    }

    String getTextureDate(){
        return ((FileTextureData)textureRegion.getTexture().getTextureData()).getFileHandle().pathWithoutExtension()+".atlas"+","+atlasRegionName;
    }

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getAngle() {
        return angle;
    }
}
