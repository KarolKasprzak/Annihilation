package com.cosma.annihilation.Editor.CosmaMap;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Utils.Constants;

public class Sprite{
    private float x,y,width,height,angle;
    private String atlasRegionName,atlasPath;
    private TextureRegion textureRegion;
    private boolean flipX = false,flipY = false;

    public boolean isFlipX() {
        return flipX;
    }

    public boolean isFlipY() {
        return flipY;
    }

    public Sprite() {
    }

     void setTextureRegion(String region, String path) {
                this.textureRegion = Annihilation.getAssets().get(path,TextureAtlas.class).findRegion(region);
                this.atlasRegionName= region;
                this.width = textureRegion.getRegionWidth()/ Constants.PPM;
                this.height = textureRegion.getRegionHeight()/Constants.PPM;
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
