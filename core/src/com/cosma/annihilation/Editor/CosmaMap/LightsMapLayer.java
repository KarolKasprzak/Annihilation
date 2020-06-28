package com.cosma.annihilation.Editor.CosmaMap;

import com.badlogic.gdx.graphics.Color;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorLights.*;


public class LightsMapLayer extends MapLayer {
    private MapLights lights = new MapLights();
    private Color ambientLightColor;
    private float ambientLightIntensity;

    private Color shaderAmbientLightColor;
    private float shaderAmbientLightIntensity;

    public LightsMapLayer() {
        ambientLightColor = Color.WHITE;
        ambientLightIntensity = 0;

        shaderAmbientLightColor = Color.WHITE;
        shaderAmbientLightIntensity = 0;
    }

    public MapLights getLights() {
        return lights;
    }

    public String createPointLight(float x, float y, Color color, int raysNumber, float maxDistance) {
        String name = "";
        if(isNameAvailable( "PointLight_" + (lights.getCount() + 1))){
            name = "PointLight_" + (lights.getCount() + 1);
        }else{
            int counter = 0;
            while(!isNameAvailable( "PointLight_" +counter)){
                counter++;
            }
            name = "PointLight_" + counter;
        }

        MapPointLight light = new MapPointLight(x,y,color,raysNumber,maxDistance);
        light.setName(name);
        lights.add(light);
        return  name;
    }

    public String createConeLight(float x, float y, Color color, int raysNumber, float maxDistance,float direction,float coneDegree) {
        String name = "ConeLight_" + (lights.getCount() + 1);
        MapConeLight light = new MapConeLight(x,y,color,raysNumber,maxDistance,direction,coneDegree);
        light.setName(name);
        lights.add(light);
        return name;
    }

    public Color getAmbientLightColor() {
        return ambientLightColor;
    }

    public void setAmbientLightColor(Color ambientLightColor) {
        this.ambientLightColor = ambientLightColor;
    }

    public float getAmbientLightIntensity() {
        return ambientLightIntensity;
    }

    public Color getShaderAmbientLightColor() {
        return shaderAmbientLightColor;
    }

    public void setShaderAmbientLightColor(Color shaderAmbientLightColor) {
        this.shaderAmbientLightColor = shaderAmbientLightColor;
    }

    public float getShaderAmbientLightIntensity() {
        return shaderAmbientLightIntensity;
    }

    public void setShaderAmbientLightIntensity(float shaderAmbientLightIntensity) {
        this.shaderAmbientLightIntensity = shaderAmbientLightIntensity;
    }

    public void setAmbientLightIntensity(float ambientLightIntensity) {
        this.ambientLightIntensity = ambientLightIntensity;
    }

    public String getLastLightName(){
        if(lights.getCount() == 0){return  null;}
        return  lights.getLight(lights.getCount()-1).getName();
    }

    public boolean isNameAvailable(String name){
        return lights.getLight(name) == null;
    }
}
