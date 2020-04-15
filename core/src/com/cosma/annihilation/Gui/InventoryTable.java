package com.cosma.annihilation.Gui;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Array;
import com.cosma.annihilation.Items.InventorySlot;
import com.cosma.annihilation.Items.InventorySlotSource;
import com.cosma.annihilation.Items.Item;

public class InventoryTable extends Table {

    public void clearItemsTable() {
        Array<Cell> cells;
        cells = this.getCells();
        for (int i = 0; i < cells.size; i++) {
            InventorySlot inventorySlot = (InventorySlot) cells.get(i).getActor();
            if (inventorySlot == null) {
                continue;
            }
            inventorySlot.clearItems();
        }
    }

    public void updateTableItemIndex(){
        Array<Cell> cells = this.getCells();
        for (int i = 0; i < cells.size; i++) {
            InventorySlot inventorySlot = ((InventorySlot) cells.get(i).getActor());
            if (inventorySlot == null) continue;
            if (inventorySlot.getItemsNumber() > 0) {
                inventorySlot.getItem().setTableIndex(i);
            }
        }
    }

    public Array<Item> getItemsFromTable() {
        updateTableItemIndex();
        Array<Cell> cells =  this.getCells();
        Array<Item> items = new Array<>();
        for (int i = 0; i < cells.size; i++) {
            InventorySlot inventorySlot = ((InventorySlot) cells.get(i).getActor());
            if (inventorySlot == null) continue;
            if (inventorySlot.getItemsNumber() > 0) {
                items.add(inventorySlot.getItem());
            }
        }
        return items;
    }

    public void fillTable(Array<Item> inventoryItems, DragAndDrop dragAndDrop) {
        clearItemsTable();
        Array<Cell> cells = this.getCells();
        for (int i = 0; i < inventoryItems.size; i++) {
            Item item = inventoryItems.get(i);
            item.getCaptureListeners().clear();
            InventorySlot inventorySlot = ((InventorySlot) cells.get(item.getTableIndex()).getActor());
            inventorySlot.add(item);
            dragAndDrop.addSource(new InventorySlotSource(inventorySlot, dragAndDrop));
        }
    }
}
