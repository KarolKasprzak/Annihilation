package com.cosma.annihilation.Gui.Inventory;

public interface InventorySlotObserver {
    public static enum InventorySlotEvent{
        ADDED_ITEM,
        REMOVED_ITEM
    }


    public void onNotify(final InventorySlot inventorySlot, InventorySlotEvent event);
}
