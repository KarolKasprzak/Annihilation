package com.cosma.annihilation.Items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;


public class Item extends Image {
    private ItemType itemType;
    private int itemValue;
    private String itemId;
    private float weight;
    private String itemName;
    private String itemShortDescription;
    private String itemIcon;
    private boolean stackable;
    private int damage;
    private boolean automatic;
    private int ammoInMagazine;
    private int maxAmmoInClip;
    private float reloadTime;
    private float accuracy;
    private ItemType ammoID;



    public enum ItemType{
        CONSUMABLE,
        ARMOUR,
        WEAPON_LONG,
        WEAPON_SHORT,
        WEAPON_MELEE,
        AMMUNITION_9MM,
        AMMUNITION_7MM,
        AMMUNITION_PLASMA,
        AMMUNITION_ENERGETIC;
    }






    public Item(){}

    public Item(String textureName, ItemID itemID, int itemAttributes, int itemUseType, int itemValue, String itemName, boolean stackable, String itemShortDescription){
           this.textureName = textureName;
           this.itemID = itemID;
           this.itemAttributes = itemAttributes;
           this.itemUseType = itemUseType;
           this.itemValue = itemValue;
           this.itemName = itemName;
           this.stackable = stackable;
           this.itemShortDescription = itemShortDescription;

    }
    public Item(Item inventoryItem){
        this.textureName = inventoryItem.getTextureName();
        this.itemID = inventoryItem.getItemID();
        this.itemAttributes = inventoryItem.getItemAttributes();
        this.itemUseType = inventoryItem.getItemUseType();
        this.itemValue = inventoryItem.getItemValue();
        this.itemName = inventoryItem.getItemName();

        this.stackable = inventoryItem.isStackable();
        this.itemShortDescription = inventoryItem.getItemShortDescription();
    }
    public int getItemValue() {
        return itemValue;
    }
    public ItemID getItemID(){
        return this.itemID;
    }
    public String getTextureName() {
        return textureName;
    }
    public int getItemAttributes() {
        return itemAttributes;
    }
    public int getItemUseType() {
        return itemUseType;
    }
    public float getItemWeight(){
        return weight;
    }
    public String getItemName() {return itemName;}
    public void setItemWeight(float weight){
        this.weight = weight;
    }
    public String getItemShortDescription() {
        return itemShortDescription;
    }
    public boolean isStackable(){
       return stackable;
    }

    public boolean isSameItemType(Item inventoryItem){
        return itemID == inventoryItem.getItemID();
    }

    public int getTradeValue(){
        return MathUtils.floor(itemValue*.33f)+2;
    }


}
