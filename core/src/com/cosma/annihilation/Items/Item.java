package com.cosma.annihilation.Items;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Item extends Image implements Json.Serializable, Comparable<Item>{
    //not null
    private String itemId;
    private String itemName;
    private ItemType itemType;
    private ItemStatus itemStatus;
    private String itemShortDescription;
    private String itemIcon;
    private float weight;
    private int itemValue;
    private boolean stackable;
    //todo
    private boolean stackableSeparate;
    //date
    private int tableIndex = 0;
    private int itemAmount = 1;
    //optional
    private int damage;
    private boolean automatic;
    private int ammoInClip;
    private int maxAmmoInClip;
    private float reloadTime;
    private float accuracy;
    private ItemType ammoType;
    private int hpRecovery;


    @Override
    public void write(Json json) {
        json.writeObjectStart();
        json.writeValue("itemID", itemId);
        if (itemStatus != ItemStatus.STANDARD) {
            json.writeValue("itemStatus", itemStatus.toString());
        }
        if (ammoInClip > 0) {
            json.writeValue("ammoInClip", ammoInClip);
        }
        if (itemAmount > 1) {
            json.writeValue("itemAmount", itemAmount);
        }
        if (tableIndex >= 0) {
            json.writeValue("tableIndex", tableIndex);
        }
        json.writeObjectEnd();
    }

    @Override
    public String toString() {
        return itemId;
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
    }

    public Item() {
    }

    public void setOptionalValues(JsonValue jsonData) {
        //Optional values
        if (jsonData.has("damage")) {
            this.setDamage(jsonData.get("damage").asInt());
        }
        if (jsonData.has("maxAmmoInClip")) {
            this.setMaxAmmoInClip(jsonData.get("maxAmmoInClip").asInt());
        }
        if (jsonData.has("reloadTime")) {
            this.setReloadTime(jsonData.get("reloadTime").asFloat());
        }
        if (jsonData.has("accuracy")) {
            this.setAccuracy(jsonData.get("accuracy").asFloat());
        }
        if (jsonData.has("automatic")) {
            this.setAutomatic(jsonData.get("automatic").asBoolean());
        }
        if (jsonData.has("itemType")) {
            this.setAmmoType(ItemType.valueOf(jsonData.get("itemType").asString()));
        }
        if (jsonData.has("ammoInClip")) {
            this.setAmmoInClip(jsonData.get("ammoInClip").asInt());
        }
    }

    public int getTableIndex() {
        return tableIndex;
    }

    public void setTableIndex(int tableIndex) {
        this.tableIndex = tableIndex;
    }

    public int getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(int itemAmount) {
        this.itemAmount = itemAmount;
    }

    public ItemStatus getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(ItemStatus itemStatus) {
        this.itemStatus = itemStatus;
    }

    public int getHpRecovery() {
        return hpRecovery;
    }

    public void setHpRecovery(int hpRecovery) {
        this.hpRecovery = hpRecovery;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public int getItemValue() {
        return itemValue;
    }

    public void setItemValue(int itemValue) {
        this.itemValue = itemValue;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemShortDescription() {
        return itemShortDescription;
    }

    public void setItemShortDescription(String itemShortDescription) {
        this.itemShortDescription = itemShortDescription;
    }

    public String getItemIcon() {
        return itemIcon;
    }

    public void setItemIcon(String itemIcon) {
        this.itemIcon = itemIcon;
    }

    public boolean isStackable() {
        return stackable;
    }

    public void setStackable(boolean stackable) {
        this.stackable = stackable;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public boolean isAutomatic() {
        return automatic;
    }

    public void setAutomatic(boolean automatic) {
        this.automatic = automatic;
    }

    public int getAmmoInClip() {
        return ammoInClip;
    }

    public void setAmmoInClip(int ammoInClip) {
        this.ammoInClip = ammoInClip;
    }

    public int getMaxAmmoInClip() {
        return maxAmmoInClip;
    }

    public void setMaxAmmoInClip(int maxAmmoInClip) {
        this.maxAmmoInClip = maxAmmoInClip;
    }

    public float getReloadTime() {
        return reloadTime;
    }

    public void setReloadTime(float reloadTime) {
        this.reloadTime = reloadTime;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public ItemType getAmmoType() {
        return ammoType;
    }

    public void setAmmoType(ItemType ammoType) {
        this.ammoType = ammoType;
    }

    public boolean isSameItemType(Item inventoryItem) {
        return itemType.equals(inventoryItem.getItemType());
    }

    public int getTradeValue() {
        return MathUtils.floor(itemValue * .33f) + 2;
    }

    @Override
    public int compareTo(Item o) {
       return this.getTableIndex() - o.getTableIndex();
    }
}
