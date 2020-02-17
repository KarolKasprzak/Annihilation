package com.cosma.annihilation.Editor.CosmaMap.CosmaEditorObject;

public abstract class MapObject {
    private String name;
    private boolean visible = true;
    private boolean isHighlighted = false;
    private String userDate;

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public void setHighlighted(boolean highlighted) {
        isHighlighted = highlighted;
    }

    MapObject() {
    }

    public String getUserDate() {
        return userDate;
    }

    public void setUserDate(String userDate) {
        this.userDate = userDate;
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
}
