package com.cosma.annihilation.Editor.CosmaMap;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;

import java.util.Iterator;

public class MapLayers implements Iterable<MapLayer> {
    private Array<MapLayer> layers = new Array<MapLayer>();

    public MapLayer getLayer (int index) {
        return layers.get(index);
    }

    public MapLayer getLayer (String name) {
        for (int i = 0, n = layers.size; i < n; i++) {
            MapLayer layer = layers.get(i);
            if (name.equals(layer.getName())) {
                return layer;
            }
        }
        return null;
    }



    public Array<MapLayer> getLayers() {
        return layers;
    }

    public void swapLayerOrder(int layerIndex, int layerIndex1){
        layers.swap(layerIndex,layerIndex1);
    }

    public boolean isEmpty(){
        return layers.size <= 0;
    }


    public int getIndex (MapLayer layer) {
        return layers.indexOf(layer, true);
    }

    public int getIndex (String name) {
        return getIndex(getLayer(name));
    }

    public int getCount () {
        return layers.size;
    }

    public void add (MapLayer layer) {
        this.layers.add(layer);
    }

    public void remove (int index) {
        layers.removeIndex(index);
    }

//    public void remove (String name) {
//        for (int i = 0, n = layers.size; i < n; i++) {
//            MapLayer layer = layers.get(i);
//            if (name.equals(layer.getName())) {
//                layers.removeValue(layer)
//            }
//        }
//    }

    public void remove (String name) {
                layers.removeValue(getLayer(name),true);
    }

    public int size () {
        return layers.size;
    }

    public <T extends MapLayer> Array<T> getByType (Class<T> type) {
        return getByType(type, new Array<T>());
    }

    private  <T extends MapLayer> Array<T> getByType (Class<T> type, Array<T> fill) {
        fill.clear();
        for (int i = 0, n = layers.size; i < n; i++) {
            MapLayer layer = layers.get(i);
            if (ClassReflection.isInstance(type, layer)) {
                fill.add((T)layer);
            }
        }
        return fill;
    }

    @Override
    public Iterator<MapLayer> iterator() {
        return layers.iterator();
    }


}
