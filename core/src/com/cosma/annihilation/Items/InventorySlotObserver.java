package com.cosma.annihilation.Items;

public interface InventorySlotObserver {
    public static enum InventorySlotEvent{
        ADDED_ITEM,
        REMOVED_ITEM
    }


    public void onNotify(final InventorySlot inventorySlot, InventorySlotEvent event);
}
