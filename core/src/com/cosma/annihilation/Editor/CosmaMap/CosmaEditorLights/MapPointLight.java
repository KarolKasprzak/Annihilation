package com.cosma.annihilation.Editor.CosmaMap.CosmaEditorLights;

import com.badlogic.gdx.graphics.Color;

public class MapPointLight extends MapLight {

    public MapPointLight() {
    }

    public MapPointLight(float x, float y, Color color, int raysNumber, float maxDistance) {
    super(x,y,color,raysNumber,maxDistance);
    }
}
