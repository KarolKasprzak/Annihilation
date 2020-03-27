package com.cosma.annihilation.Items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Scaling;
import com.cosma.annihilation.Annihilation;


public class ItemLoader {

    private ObjectMap<String, FileHandle> itemMap;
    private Array<String> itemIdList;
    private Json jason;

    public Item getItem(String itemID){
        FileHandle file = itemMap.get(itemID);
        Item item = jason.fromJson(Item.class, file);
        item.setDrawable(new TextureRegionDrawable(Annihilation.getAssets().get("gfx/atlas/items_icon.atlas", TextureAtlas.class).findRegion(item.getItemIcon())));
        item.setScaling(Scaling.fit);
        return item;
    }


    public Array<String> getItemIdList() {
        return itemIdList;
    }

    public ItemLoader() {
        jason= new Json();
        jason.setSerializer(Item.class, new ItemReader());
        itemIdList = new Array<>();
        itemMap = new ObjectMap<>();

        FileHandle jsonFilesPath = Gdx.files.local("json/items");
        for (FileHandle file : jsonFilesPath.list(".json")) {
            Item item = jason.fromJson(Item.class, file);
            itemMap.put(item.getItemId(), file);
            itemIdList.add(item.getItemId());
        }
    }
}
