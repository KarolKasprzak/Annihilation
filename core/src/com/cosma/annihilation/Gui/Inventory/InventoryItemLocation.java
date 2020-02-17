package com.cosma.annihilation.Gui.Inventory;

public class InventoryItemLocation {
    private int tableIndex;
    private String itemID;
    private int itemsAmount;

    public InventoryItemLocation(){
    }

    public InventoryItemLocation(int tableIndex, String itemID, int itemsAmount){
        this.tableIndex = tableIndex;
        this.itemID = itemID;
        this.itemsAmount = itemsAmount;
    }

    public int getTableIndex() {
        return tableIndex;
    }

    public String getItemID() {
        return itemID;
    }

    public int getItemsAmount() {
        return itemsAmount;
    }

    public void setTableIndex(int tableIndex) {
        this.tableIndex = tableIndex;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public void setItemsAmount(int itemsAmount) {
        this.itemsAmount = itemsAmount;
    }

    @Override
    public String toString() {
        if(itemID != null){
            return itemID + " " + itemsAmount;
        }
        return super.toString();
    }
}