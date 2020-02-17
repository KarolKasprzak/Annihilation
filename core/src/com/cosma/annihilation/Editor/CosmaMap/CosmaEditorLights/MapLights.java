package com.cosma.annihilation.Editor.CosmaMap.CosmaEditorLights;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;

import java.util.Iterator;

public class MapLights implements Iterable<MapLight> {
    private Array<MapLight> lights = new Array<MapLight>();

    public MapLight getLight (int index) {
        return lights.get(index);
    }

    public MapLight getLight (String name) {
        for (int i = 0, n = lights.size; i < n; i++) {
            MapLight light = lights.get(i);
            if (name.equals(light.getName())) {
                return light;
            }
        }
        return null;
    }

    public Array<MapLight> getLights() {
        return lights;
    }

    public boolean isEmpty(){
        return lights.size <= 0;
    }

    public int getIndex (MapLight light) {
        return lights.indexOf(light, true);
    }

    public int getIndex (String name) {
        return getIndex(getLight(name));
    }

    public int getCount () {
        return lights.size;
    }

    public void add (MapLight light) {
        this.lights.add(light);
    }

    public void remove (int index) {
        lights.removeIndex(index);
    }


    public void remove (String name) {
                lights.removeValue(getLight(name),true);
    }

    public int size () {
        return lights.size;
    }

    public <T extends MapLight> Array<T> getByType (Class<T> type) {
        return getByType(type, new Array<T>());
    }

   private  <T extends MapLight> Array<T> getByType (Class<T> type, Array<T> fill) {
        fill.clear();
        for (int i = 0, n = lights.size; i < n; i++) {
            MapLight light = lights.get(i);
            if (ClassReflection.isInstance(type, light)) {
                fill.add((T)light);
            }
        }
        return fill;
    }

    @Override
    public Iterator<MapLight> iterator() {
        return lights.iterator();
    }


}
