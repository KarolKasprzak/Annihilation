package com.cosma.annihilation.Editor.CosmaMap.CosmaEditorObject;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;

import java.util.Iterator;

public class MapObjects implements Iterable<MapObject> {
    private Array<MapObject> objects = new Array<MapObject>();

    public MapObject getObject (int index) {
        return objects.get(index);
    }

    public MapObject getObject (String name) {
        for (int i = 0, n = objects.size; i < n; i++) {
            MapObject object = objects.get(i);
            if (name.equals(object.getName())) {
                return object;
            }
        }
        return null;
    }


    public Array<MapObject> getObjects() {
        return objects;
    }

    public boolean isEmpty(){
        return objects.size > 0;
    }

    public int getIndex (MapObject object) {
        return objects.indexOf(object, true);
    }

    public int getIndex (String name) {
        return getIndex(getObject(name));
    }

    public int getCount () {
        return objects.size;
    }

    public void add (MapObject object) {
        this.objects.add(object);
    }

    public void remove (int index) {
        objects.removeIndex(index);
    }

//    public void remove (String name) {
////        for (int i = 0, n = objects.size; i < n; i++) {
////            MapLayer layer = objects.get(i);
////            if (name.equals(layer.getName())) {
////                objects.removeValue(layer);
////            }
////        }
//    }

    public void remove (String name) {
                objects.removeValue(getObject(name),true);
    }

    public int size () {
        return objects.size;
    }

    public <T extends MapObject> Array<T> getByType (Class<T> type) {
        return getByType(type, new Array<T>());
    }

   private  <T extends MapObject> Array<T> getByType (Class<T> type, Array<T> fill) {
        fill.clear();
        for (int i = 0, n = objects.size; i < n; i++) {
            MapObject object = objects.get(i);
            if (ClassReflection.isInstance(type, object)) {
                fill.add((T)object);
            }
        }
        return fill;
    }

    @Override
    public Iterator<MapObject> iterator() {
        return objects.iterator();
    }


}
