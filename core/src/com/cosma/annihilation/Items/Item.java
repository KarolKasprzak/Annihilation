package com.cosma.annihilation.Items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;


public class InventoryItem extends Image {
    private int itemAttributes;
    private int itemUseType;
    private int itemValue;
    private ItemID itemID;
    private float weight;
    private String itemName;
    private String itemShortDescription;
    private String textureName;
    private Texture texture;
    private boolean stackable;

    public enum ItemID {
        MP44,BOX,P38,AMMO_BOX9MM,FIRE_AXE;
    }



    public enum ItemAttribute{
        CONSUMABLE(1),
        EQUIPPABLE(2);

        private int attribute;

        ItemAttribute(int attribute){
            this.attribute = attribute;
        }

        public int getValue(){
            return attribute;
        }

    }

    public enum ItemType {
        ARMOUR(1),
        ITEM_RESTORE_MP(2),
        WEAPON_MELEE(4),
        WEAPON_DISTANCE_ENERGETIC(8),
        WEAPON_DISTANCE_ENERGETIC_LONG(16),
        WEAPON_DISTANCE(32),
        WEAPON_DISTANCE_LONG(64),
        AMMUNIION(128);
//        ARMOR_SHIELD(128),
//        ARMOR_HELMET(256),
//        ARMOR_CHEST(512),
//        ARMOR_FEET(1024);

        private int itemUseType;

        ItemType(int itemUseType){
            this.itemUseType = itemUseType;
        }

        public int getValue(){
            return itemUseType;
        }
    }



    public InventoryItem(){}

    public InventoryItem(String textureName,ItemID itemID,int itemAttributes, int itemUseType, int itemValue,String itemName,boolean stackable,String itemShortDescription){
           this.textureName = textureName;
           this.itemID = itemID;
           this.itemAttributes = itemAttributes;
           this.itemUseType = itemUseType;
           this.itemValue = itemValue;
           this.itemName = itemName;
           this.stackable = stackable;
           this.itemShortDescription = itemShortDescription;

    }
    public InventoryItem(InventoryItem inventoryItem){
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

    public boolean isSameItemType(InventoryItem inventoryItem){
        return itemID == inventoryItem.getItemID();
    }

    public int getTradeValue(){
        return MathUtils.floor(itemValue*.33f)+2;
    }


}
