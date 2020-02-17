package com.cosma.annihilation.Editor.CosmaMap;

public abstract class MapLayer {
    private int width;
    private int height;
    private String name = "";
    private boolean visible = true;;

    public MapLayer(int width, int height, String name) {
        this.width = width;
        this.height = height;
        this.name = name;
    }

    public MapLayer() {

    }

    public String getName() {
        return name;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isLayerVisible(){
        return visible;
    }

    public void setName(String name) {
        this.name = name;
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
