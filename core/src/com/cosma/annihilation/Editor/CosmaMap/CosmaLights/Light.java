package com.cosma.annihilation.Editor.CosmaMap.CosmaLights;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Editor.CosmaMap.GameMap;

public class Light {
    private String name;
    private boolean isHighlighted = false;
    private Color color = Color.WHITE;
    private TextureRegion textureRegion = Annihilation.getTextureRegion("light_maps", "test");

    public TextureRegion getTexture() {
        return textureRegion;
    }

    private float lightRadius = 3;
    private float x;
    private float y;

    private boolean isActive = true;
    private float lightZPosition = 0.05f;
    private float intensityForShader = 1.0f;

    public void changeName(GameMap map, String newName) {
        map.getLights().alter(getName(), newName);
    }

    private void generateName(GameMap map) {
        this.name = "light_" + map.getLights().size + 1;
        if (map.getLights().containsKey(this.name)) {
            for (int i = 0; i < map.getLights().size + 10; i++) {
                if (!map.getLights().containsKey("light_" + i)) {
                    this.name = "light_" + i;
                    return;
                }
            }
        }
    }

    public Light(float x, float y, GameMap map) {
        generateName(map);
        this.x = x;
        this.y = y;
        map.getLights().put(getName(), this);
    }

    public Light(Color color, float x, float y, GameMap map) {
        generateName(map);
        this.color = color;
        this.x = x;
        this.y = y;
        map.getLights().put(getName(), this);
    }

    public Light() {
    }


    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public float getLightRadius() {
        return lightRadius;
    }

    public void setLightRadius(float lightRadius) {
        this.lightRadius = lightRadius;
    }

    public float getLightZ() {
        return lightZPosition;
    }

    public void setLightZ(float lightZPosition) {
        this.lightZPosition = lightZPosition;
    }

    public float getIntensityForShader() {
        return intensityForShader;
    }

    public void setIntensityForShader(float intensityForShader) {
        this.intensityForShader = intensityForShader;
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

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public void setHighlighted(boolean highlighted) {
        isHighlighted = highlighted;
    }

    public String getName() {
        return name;
    }
}
