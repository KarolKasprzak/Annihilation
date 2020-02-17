package com.cosma.annihilation.Editor.CosmaMap;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.cosma.annihilation.Annihilation;

public class SpriteMapLayer extends MapLayer implements Json.Serializable {

    private Array<Sprite> spriteArray = new Array<>();

    public SpriteMapLayer(int width, int height, String name) {
        super(width, height, name);
    }

    public SpriteMapLayer() {
    }

    public void createSprite(String regionName, String texturePath, float x, float y, float angle) {
        Sprite sprite = new Sprite();
        sprite.setTextureRegion(regionName, texturePath);
        sprite.setSpritePosition(x, y, angle);
        spriteArray.add(sprite);
        System.out.println("sprite added");
    }
    public void createAnimatedSprite(String regionName, String texturePath, float x, float y, float angle){
        AnimatedSprite sprite = new AnimatedSprite();
        sprite.setTextureRegion(regionName, texturePath);
        sprite.setSpritePosition(x, y, angle);
        spriteArray.add(sprite);
        System.out.println("animated sprite added");
    }

    public Array<Sprite> getSpriteArray() {
        return spriteArray;
    }

    @Override
    public void write(Json json) {
        json.writeValue("name", this.getName());
        json.writeValue("height", this.getHeight());
        json.writeValue("width", this.getWidth());
        json.writeArrayStart("sprites");
        for (Sprite sprite : spriteArray) {
            json.writeObjectStart();
            if(sprite instanceof AnimatedSprite){
                json.writeValue("animated",true);
            }
            json.writeValue("position", sprite.getX() + "," + sprite.getY() + "," + sprite.getAngle());
            json.writeValue("texture", sprite.getTextureDate());
            json.writeObjectEnd();
        }
        json.writeArrayEnd();
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.setName(jsonData.get("name").asString());
        this.setHeight(jsonData.get("height").asInt());
        this.setWidth(jsonData.get("width").asInt());
        for (JsonValue value : jsonData.get("sprites")) {
            String texture = value.get("texture").asString();
            String position = value.get("position").asString();
            if(value.has("animated")){
                AnimatedSprite animatedSprite = new AnimatedSprite();
                animatedSprite.setTextureRegion(texture.split(",")[1], texture.split(",")[0]);
                animatedSprite.setSpritePosition(Float.parseFloat(position.split(",")[0]), Float.parseFloat(position.split(",")[1]), Float.parseFloat(position.split(",")[2]));
                spriteArray.add(animatedSprite);
            }else{
                Sprite sprite = new Sprite();

                sprite.setTextureRegion(texture.split(",")[1], texture.split(",")[0]);
                sprite.setSpritePosition(Float.parseFloat(position.split(",")[0]), Float.parseFloat(position.split(",")[1]), Float.parseFloat(position.split(",")[2]));
                spriteArray.add(sprite);
            }
        }
    }
}
