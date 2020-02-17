package com.cosma.annihilation.Editor.CosmaMap;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorObject.MapObjects;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorObject.RectangleObject;

public class ObjectMapLayer extends MapLayer {
    private int width;
    private int height;
    private MapObjects objects = new MapObjects();

    public ObjectMapLayer(int width, int height, String name) {
        super(width, height, name);
    }

    public ObjectMapLayer() {
    }

    public MapObjects getObjects() {
        return objects;
    }

    public String createBoxObject(float x, float y, float w, float h, BodyDef.BodyType bodyType) {
        String name = "Rectangle_" + (objects.getCount() + 1);
        RectangleObject rec = new RectangleObject(x,y,w,h,0, bodyType);
        rec.setName(name);
        objects.add(rec);
        return name;
    }
}
