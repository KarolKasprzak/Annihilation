package com.cosma.annihilation.Gui.Inventory;

public interface InventorySlotObservable {


    public void register(InventorySlotObserver inventorySlotObserver);
    public void unregister(InventorySlotObserver inventorySlotObserver);


    public void notifyObservers(EquipmentSlot inventorySlot, InventorySlotObserver.InventorySlotEvent event);

}
