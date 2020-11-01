package com.cosma.annihilation.Editor.CosmaMap.CosmaEditorObject;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.cosma.annihilation.Utils.Enums.MaterialType;

public class MapMaterialObject implements Json.Serializable {
    private float x;
    private float y;
    private float width;
    private float height;
    private Rectangle rectangle;
    private MaterialType materialType;
    private boolean isHighlighted = false;



    public MapMaterialObject() {
    }

    public MapMaterialObject(float x, float y, float width, float height, MaterialType materialType) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.materialType = materialType;
        rectangle = new Rectangle(x,y,width,height);
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public void setHighlighted(boolean highlighted) {
        isHighlighted = highlighted;
    }

    public void setPosition(Vector2 vector2){
        this.x = vector2.x;
        this.y = vector2.y;
    }

    public void setPosition(float x, float y){
        this.x = x;
        this.y = y;
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

    public void setDimensions(float width, float height){
        this.width = width;
        this.height = height;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public MaterialType getMaterialType() {
        return materialType;
    }

    public void setMaterialType(MaterialType materialType) {
        this.materialType = materialType;
    }

    @Override
    public void write(Json json) {
        json.writeValue("dimensions",x + "," + y + "," + width + "," + height);
        json.writeValue("material",materialType.toString());
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        String dimensions = jsonData.get("dimensions").asString();
        this.setPosition(Float.parseFloat(dimensions.split(",")[0]),Float.parseFloat(dimensions.split(",")[1]));
        this.setDimensions(Float.parseFloat(dimensions.split(",")[2]),Float.parseFloat(dimensions.split(",")[3]));
        this.rectangle = new Rectangle(x,y,width,height);
        setMaterialType(MaterialType.valueOf(jsonData.get("material").asString()));
    }
}
