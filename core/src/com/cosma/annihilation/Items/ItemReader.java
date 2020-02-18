package com.cosma.annihilation.Items;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class ItemReader implements Json.Serializer<Item> {
    @Override
    public void write(Json json, Item object, Class knownType) {

    }

    @Override
    public Item read(Json json, JsonValue jsonData, Class type) {
        Item item = new Item();
        item.setItemId(jsonData.get("itemID").asString());
        item.setItemName(jsonData.get("itemName").asString());
        item.setItemType(Item.ItemType.valueOf(jsonData.get("itemType").asString()));
        item.setItemShortDescription(jsonData.get("itemShortDescription").asString());
        item.setItemIcon(jsonData.get("iconName").asString());
        item.setItemValue(jsonData.get("itemValue").asInt());
        item.setStackable(jsonData.get("stackable").asBoolean());
        item.setWeight(jsonData.get("weight").asInt());
        item.setItemStatus(Item.ItemStatus.valueOf(jsonData.get("itemStatus").asString()));
        item.setOptionalValues(jsonData);
        return item;
    }
}
