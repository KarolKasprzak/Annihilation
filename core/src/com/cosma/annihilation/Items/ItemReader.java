package com.cosma.annihilation.Items;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class ItemReader implements Json.Serializer<Item> {
    @Override
    public void write(Json json, Item object, Class knownType) {

    }

    @Override
    public Item read(Json json, JsonValue jsonData, Class type) {
        return null;
    }
}
