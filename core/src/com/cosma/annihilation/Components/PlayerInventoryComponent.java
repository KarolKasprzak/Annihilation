package com.cosma.annihilation.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.cosma.annihilation.Gui.Inventory.InventoryItemLocation;

public class PlayerInventoryComponent implements Component {
    public Array<InventoryItemLocation> equippedItem = new Array<InventoryItemLocation>();
    public Array<InventoryItemLocation> inventoryItem = new Array<InventoryItemLocation>();
}
