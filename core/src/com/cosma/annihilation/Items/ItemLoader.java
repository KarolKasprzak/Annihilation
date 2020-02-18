package com.cosma.annihilation.Items;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;


public class ItemLoader {

    private ObjectMap<String, Item> itemMap;

    public ObjectMap<String, Item> getItemMap() {
        return itemMap;
    }

    public ItemLoader() {
        Json jason = new Json();
        jason.setSerializer(Item.class, new ItemReader());
        itemMap = new ObjectMap<>();


        FileHandle jsonFilesPath = Gdx.files.local("json/items");
        for (FileHandle file : jsonFilesPath.list(".json")) {
            Item item = jason.fromJson(Item.class, file);
            itemMap.put(item.getItemId(), item);
        }
    }
}
