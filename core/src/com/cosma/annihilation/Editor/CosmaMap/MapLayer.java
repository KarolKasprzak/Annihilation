package com.cosma.annihilation.Editor.CosmaMap;

public abstract class MapLayer {
    private int width;
    private int height;
    private boolean visible = true;;

    public MapLayer(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public MapLayer() {
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isLayerVisible(){
        return visible;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
