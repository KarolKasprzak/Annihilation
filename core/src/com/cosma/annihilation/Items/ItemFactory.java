package com.cosma.annihilation.Items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Scaling;
import com.cosma.annihilation.Items.InventoryItem;
import com.cosma.annihilation.Items.InventoryItem.ItemID;
import com.cosma.annihilation.Utils.AssetLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;


public class ItemFactory {

    private Hashtable<ItemID, Object> itemMap;
    private AssetLoader assetLoader;
    private static ItemFactory instance = null;
    private final String ARMOUR_ITEM_PATH = "json/items/armours/armours.json";


    public static ItemFactory getInstance() {
        if (instance == null) {
            instance = new ItemFactory();
        }

        return instance;
    }

    private ItemFactory() {
        String WEAPON_ITEM_PATH = "json/items/weapons/weapons.json";
        Json json = new Json();
        ArrayList<JsonValue> list = json.fromJson(ArrayList.class, Gdx.files.internal(WEAPON_ITEM_PATH));
        String STANDARD_ITEM_PATH = "json/items/standard/items.json";
        ArrayList<JsonValue> list1 = json.fromJson(ArrayList.class, Gdx.files.internal(STANDARD_ITEM_PATH));


        itemMap = new Hashtable<ItemID, Object>();
//        json.setIgnoreUnknownFields(true);


        for (JsonValue jsonValue : list) {
            WeaponItem weaponItem = json.readValue(WeaponItem.class, jsonValue);
            weaponItem.setAutomatic(jsonValue.getBoolean("automatic"));
            weaponItem.setAmmoInMagazine(jsonValue.getInt("ammoInMagazine"));
            weaponItem.setMaxAmmoInMagazine(jsonValue.getInt("maxAmmoInMagazine"));
            weaponItem.setReloadTime(jsonValue.getFloat("reloadTime"));
            weaponItem.setAccuracy(jsonValue.getFloat("accuracy"));
            itemMap.put(weaponItem.getItemID(), weaponItem);
        }
        for (JsonValue jsonValue : list1) {
            InventoryItem inventoryItem = json.readValue(InventoryItem.class, jsonValue);
            itemMap.put(inventoryItem.getItemID(), inventoryItem);
        }

    }


    public InventoryItem getItem(ItemID itemID) {
        Class type = itemMap.get(itemID).getClass();
        if (type == InventoryItem.class) {
            InventoryItem item = new InventoryItem((InventoryItem) itemMap.get(itemID));
            setTexture(item);
            return (InventoryItem) (InventoryItem) item;
        }
        if (type == WeaponItem.class) {
            WeaponItem item = new WeaponItem((WeaponItem) itemMap.get(itemID));
            setTexture(item);
            return (InventoryItem) (WeaponItem) item;
        }
        return null;
    }
    private void setTexture(InventoryItem inventoryItem){
        inventoryItem.setDrawable(new TextureRegionDrawable(new TextureRegion((Texture) assetLoader.manager.get(inventoryItem.getTextureName()))));
        inventoryItem.setScaling(Scaling.fit);
        }

    public void setAssetLoader(AssetLoader assetLoader) {
        this.assetLoader = assetLoader;
    }
}




