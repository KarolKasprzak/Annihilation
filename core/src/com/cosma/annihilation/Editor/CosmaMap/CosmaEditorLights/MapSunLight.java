package com.cosma.annihilation.Editor.CosmaMap.CosmaEditorLights;

import com.badlogic.gdx.graphics.Color;

public class MapSunLight extends MapLight {
    private float direction;

    public MapSunLight(){

    }

    public MapSunLight(float x, float y, Color color, int raysNumber, float direction) {
        super(x, y, color, raysNumber);
        this.direction = direction;
    }

    public float getDirection() {
        return direction;
    }

    public void setDirection(float direction) {
        this.direction = direction;
    }

}
