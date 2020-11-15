package com.cosma.annihilation.Components;

import com.badlogic.gdx.math.Rectangle;
import com.cosma.annihilation.EntityEngine.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.cosma.annihilation.Utils.Constants;


/**Sprite drawn over walls etc  */
public class SpriteComponent implements Component,Pool.Poolable {
    public float time = 0;
    public float lifeTime = 0;
    public boolean isLifeTimeLimited = false;
    /** if true sprite can be spawn only over specified area */
    public boolean idSpawnPlaceLimited = false;
    public boolean drawDiffuse = true;
    public boolean flipX = false;
    public boolean flipY = false;
    public float x;
    public float y;
    public float width;
    public float height;
    public float angle = 0;
    public Rectangle rectangle;

    public void createRectangle(TextureComponent textureComponent){
        createRectangle(textureComponent.textureRegion.getRegionWidth()/Constants.PPM,textureComponent.textureRegion.getRegionHeight()/Constants.PPM);
    }

    public void createRectangle(){
        rectangle = new com.badlogic.gdx.math.Rectangle(x,y,this.width,this.height);
    }

    public void createRectangle(float width, float height){
        rectangle = new com.badlogic.gdx.math.Rectangle(x,y,width,height);
    }

    @Override
    public void reset() {
        time = 0;
        flipX = false;
        flipY = false;
    }
}
