package com.cosma.annihilation.Items;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.cosma.annihilation.Annihilation;

public class ItemReader implements Json.Serializer<Item> {
    @Override
    public void write(Json json, Item object, Class knownType) {

    }

    @Override
    public Item read(Json json, JsonValue jsonData, Class type) {
        Item item = new Item();
        item.setItemId(jsonData.get("itemID").asString());
        item.setItemName(Annihilation.getLocalText(jsonData.get("itemID").asString()));
        item.setItemType(ItemType.valueOf(jsonData.get("itemType").asString()));
        item.setItemShortDescription(Annihilation.getLocalText(jsonData.get("itemShortDescription").asString()));
        item.setItemIcon(jsonData.get("iconName").asString());
        item.setItemValue(jsonData.get("itemValue").asInt());
        item.setStackable(jsonData.get("stackable").asBoolean());
        item.setWeight(jsonData.get("weight").asInt());
        item.setItemStatus(ItemStatus.valueOf(jsonData.get("itemStatus").asString()));
        //Optional values
        if (jsonData.has("damage")) {
            item.setDamage(jsonData.get("damage").asInt());
        }
        if (jsonData.has("maxAmmoInClip")) {
            item.setMaxAmmoInClip(jsonData.get("maxAmmoInClip").asInt());
        }
        if (jsonData.has("reloadTime")) {
            item.setReloadTime(jsonData.get("reloadTime").asFloat());
        }
        if (jsonData.has("accuracy")) {
            item.setAccuracy(jsonData.get("accuracy").asFloat());
        }
        if (jsonData.has("automatic")) {
            item.setAutomatic(jsonData.get("automatic").asBoolean());
        }
        if (jsonData.has("ammoType")) {
            item.setAmmoType(ItemType.valueOf(jsonData.get("itemType").asString()));
        }
        if (jsonData.has("ammoInClip")) {
            item.setAmmoInClip(jsonData.get("ammoInClip").asInt());
        }

        return item;
    }
}
