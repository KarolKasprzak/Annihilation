package com.cosma.annihilation.Editor.CosmaMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.OrderedMap;
import com.cosma.annihilation.Editor.CosmaMap.CosmaLights.Light;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorObject.MapMaterialObject;
import com.cosma.annihilation.EntityEngine.core.Entity;

import java.util.ArrayList;

public class GameMap {

    private int width;
    private int height;
    private int tileSize;
    private ArrayList<Entity> entityList;

    private ArrayList<MapMaterialObject> materialList;
    private String mapName;
    private OrderedMap<String, Light> lightMap;
    private SpriteMapLayer spriteMapLayer;
    private ObjectMapLayer objectMapLayer;
    private Color ambientColor;


    public GameMap(int width, int height, int tileSize) {
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        lightMap = new OrderedMap<>();
        entityList = new ArrayList<>();
        spriteMapLayer = new SpriteMapLayer();
        objectMapLayer = new ObjectMapLayer();
        materialList = new ArrayList<>();
        ambientColor = new Color();
        ambientColor.set(0.2f,0.2f,0.2f,0.2f);
    }

    public GameMap() {

    }

    public SpriteMapLayer getSpriteMapLayer() {
        return spriteMapLayer;
    }

    public ObjectMapLayer getObjectMapLayer() {
        return objectMapLayer;
    }

    public OrderedMap<String, Light> getLights() {
        return lightMap;
    }

    public Color getAmbientColor() {
        return ambientColor;
    }

    public void setAmbientColor(Color ambientColor) {
        this.ambientColor = ambientColor;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTileSize() {
        return tileSize;
    }

    public void addEntity(Entity entity) {
        entityList.add(entity);
    }

    public void removeEntity(Entity entity) {
        entityList.remove(entity);
    }

    public ArrayList<Entity> getEntityArrayList() {
        return entityList;
    }

    public ArrayList<MapMaterialObject> getMapMaterialObjects() {
        return materialList;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }
}
