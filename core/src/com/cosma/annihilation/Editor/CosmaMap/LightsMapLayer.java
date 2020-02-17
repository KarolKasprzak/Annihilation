package com.cosma.annihilation.Editor.CosmaMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorLights.*;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorObject.MapObjects;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorObject.RectangleObject;
import com.cosma.annihilation.Utils.CollisionID;

public class LightsMapLayer extends MapLayer {
    private int width;
    private int height;
    private MapLights lights = new MapLights();

    public LightsMapLayer(int width, int height, String name) {
        super(width, height, name);
    }

    public LightsMapLayer() {
    }

    public MapLights getLights() {
        return lights;
    }

    public void createPointLight(float x, float y, Color color, int raysNumber, float maxDistance) {
        String name = "PointLight_" + (lights.getCount() + 1);
        MapPointLight light = new MapPointLight(x,y,color,raysNumber,maxDistance);
        light.setName(name);
        lights.add(light);
    }

    public void createConeLight(float x, float y, Color color, int raysNumber, float maxDistance,float direction,float coneDegree) {
        String name = "ConeLight_" + (lights.getCount() + 1);
        MapConeLight light = new MapConeLight(x,y,color,raysNumber,maxDistance,direction,coneDegree);
        light.setName(name);
        lights.add(light);
    }

    public void createSunLight(float x, float y, Color color, int raysNumber,float direction) {
        String name = "SunLight_" + (lights.getCount() + 1);
        MapSunLight light = new MapSunLight(x,y,color,raysNumber,direction);
        light.setName(name);
        lights.add(light);
    }

    public String getLastLightName(){
        if(lights.getCount() == 0){return  null;}
        return  lights.getLight(lights.getCount()-1).getName();
    }
}
