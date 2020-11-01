package com.cosma.annihilation.Editor.CosmaMap;

import com.badlogic.gdx.utils.OrderedMap;
import com.cosma.annihilation.Box2dLight.Light;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorObject.MapMaterialObject;
import com.cosma.annihilation.EntityEngine.core.Entity;

import java.util.ArrayList;

public class GameMap{

    private int width;
    private int height;
    private int tileSize;
    private ArrayList<Entity> entityList;
    private ArrayList<MapMaterialObject>materialList;
    private String mapName;

    private SpriteMapLayer spriteMapLayer;
    private ObjectMapLayer objectMapLayer;
    private LightsMapLayer lightsMapLayer;

    public GameMap(int width, int height, int tileSize) {
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        lightMap = new OrderedMap<>();
        entityList = new ArrayList<>();
        spriteMapLayer = new SpriteMapLayer();
        objectMapLayer = new ObjectMapLayer();
        lightsMapLayer = new LightsMapLayer();
        materialList = new ArrayList<>();
    }

    public SpriteMapLayer getSpriteMapLayer() {
        return spriteMapLayer;
    }

    public ObjectMapLayer getObjectMapLayer() {
        return objectMapLayer;
    }

    public LightsMapLayer getLightsMapLayer() {
        return lightsMapLayer;
    }

    public GameMap() {
        materialList = new ArrayList<>();
        lightMap = new OrderedMap<>();
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

    public void addEntity(Entity entity){
        entityList.add(entity);
    }

    public void removeEntity(Entity entity){
        entityList.remove(entity);
    }

    public ArrayList<Entity> getEntityArrayList(){
        return entityList;
    }

    public ArrayList<MapMaterialObject> getMapMaterialObjects() {return materialList;}

    public String getMapName() {return mapName;}

    public void setMapName(String mapName) {this.mapName = mapName;}

    transient private OrderedMap<String, Light> lightMap;

    public Light findLight(String name) {
        return lightMap.get(name);
    }

    public void putLight(String name,Light light) {
        lightMap.put(name,light);
    }

}
