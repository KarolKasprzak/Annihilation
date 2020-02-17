package com.cosma.annihilation.Gui.Inventory;

public interface InventorySlotObservable {


    public void register(InventorySlotObserver inventorySlotObserver);
    public void unregister(InventorySlotObserver inventorySlotObserver);


    public void notifyObservers(InventorySlot inventorySlot, InventorySlotObserver.InventorySlotEvent event);

}
