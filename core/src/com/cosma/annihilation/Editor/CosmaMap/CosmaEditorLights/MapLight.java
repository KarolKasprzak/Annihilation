package com.cosma.annihilation.Editor.CosmaMap.CosmaEditorLights;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public abstract class MapLight {
    private String name;
    private boolean visible = true;
    private boolean isHighlighted = false;
    private int raysNumber;
    private Color color;
    private float lightDistance;
    private float x;
    private float y;
    private boolean staticLight = false;
    private boolean softLight = true;
    private float softLength = 2.5f;
    private short maskBit;
    private short categoryBit;

    private boolean isLightEnabled = true;
    private float lightZPositionForShader = 0.05f;
    private float lightFalloffDistance = 0.5f;
    private float intensityForShader = 1.0f;
    private boolean renderWithShader = true;

    public boolean isLightEnabled() {
        return isLightEnabled;
    }

    public void setLightEnabled(boolean lightEnabled) {
        isLightEnabled = lightEnabled;
    }

    public float getLightZPositionForShader() {
        return lightZPositionForShader;
    }

    public void setLightZPositionForShader(float lightZPositionForShader) {
        this.lightZPositionForShader = lightZPositionForShader;
    }

    public float getLightFalloffDistance() {
        return lightFalloffDistance;
    }

    public void setLightFalloffDistance(float lightFalloffDistance) {
        this.lightFalloffDistance = lightFalloffDistance;
    }

    public float getIntensityForShader() {
        return intensityForShader;
    }

    public void setIntensityForShader(float intensityForShader) {
        this.intensityForShader = intensityForShader;
    }

    public boolean isRenderWithShader() {
        return renderWithShader;
    }

    public void setRenderWithShader(boolean renderWithShader) {
        this.renderWithShader = renderWithShader;
    }



    MapLight() {
    }
    public MapLight(float x, float y, Color color,int raysNumber) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.raysNumber = raysNumber;
    }

    MapLight(float x, float y, Color color, int raysNumber, float distance) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.raysNumber = raysNumber;
        this.lightDistance = distance;

    }

    public float getLightDistance() {
        return lightDistance;
    }

    public void setLightDistance(float lightDistance) {
        this.lightDistance = lightDistance;
    }

    public int getRaysNumber() {
        return raysNumber;
    }

    public void setRaysNumber(int raysNumber) {
        this.raysNumber = raysNumber;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public boolean isStaticLight() {return staticLight;}

    public void setStaticLight(boolean staticLight) {this.staticLight = staticLight;}

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public void setHighlighted(boolean highlighted) {
        isHighlighted = highlighted;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public boolean isVisible () {
        return visible;
    }

    public void setVisible (boolean visible) {
        this.visible = visible;
    }

    public boolean isSoftLight() {
        return softLight;
    }

    public void setSoftLight(boolean softLight) {
        this.softLight = softLight;
    }

    public float getSoftLength() {
        return softLength;
    }

    public void setSoftLength(float softLength) {
        this.softLength = softLength;
    }

    public short getMaskBit() {  return maskBit;    }

    public void setMaskBit(short maskBit) { this.maskBit = maskBit; }

    public short getCategoryBit() {return categoryBit; }

    public void setCategoryBit(short categoryBit) { this.categoryBit = categoryBit;  }

}
