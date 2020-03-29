package com.cosma.annihilation.Gui.MainMenu;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Array;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Components.PlayerComponent;
import com.cosma.annihilation.Components.PlayerInventoryComponent;
import com.cosma.annihilation.Gui.GuiWindow;
import com.cosma.annihilation.Gui.InventoryTable;
import com.cosma.annihilation.Items.*;

public class InventoryWindow extends GuiWindow implements InventorySlotObserver {

    private Engine engine;
    private DragAndDrop dragAndDrop;

    private InventoryTable inventorySlotsTable;
    private InventoryTable equipmentSlotsTable;
    private InventorySlot weaponInventorySlot;
    private InventorySlot armourInventorySlot;
    private ClickListener clickListener;

    public InventoryWindow(String title, Skin skin, Engine engine,float parentWidth) {
        super(title, skin);
        this.engine = engine;
        dragAndDrop = new DragAndDrop();
        float slotSize = parentWidth * 0.1f;



        clickListener = new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.out.println("work");
            }
        };
        clickListener.setButton(Input.Buttons.RIGHT);



        //inventory table
        inventorySlotsTable = new InventoryTable();
        inventorySlotsTable.center().padTop(parentWidth * 0.05f);
        for (int i = 1; i <= 25; i++) {
            InventorySlot inventorySlot = new InventorySlot();
            inventorySlot.addListener(clickListener);
            inventorySlot.setImageScale(1f);
            dragAndDrop.addTarget(new InventorySlotTarget(inventorySlot));
            inventorySlotsTable.add(inventorySlot).size(slotSize, slotSize).pad(slotSize*0.05f);
            if (i == 5||i == 10||i == 15||i == 20|| i == 25) inventorySlotsTable.row();
        }
        add(inventorySlotsTable);
        row();

        //equipment table
        equipmentSlotsTable = new InventoryTable();
        armourInventorySlot = new InventorySlot(new Image(Annihilation.getAssets().get("gfx/interface/gui_armour_slot.png", Texture.class)));
        weaponInventorySlot = new InventorySlot(new Image(Annihilation.getAssets().get("gfx/interface/gui_weapon_slot.png",Texture.class)),ItemType.WEAPON_SHORT,ItemType.WEAPON_LONG);
        weaponInventorySlot.register(this);

        dragAndDrop.addTarget(new InventorySlotTarget(armourInventorySlot));
        dragAndDrop.addTarget(new InventorySlotTarget(weaponInventorySlot));
        equipmentSlotsTable.add(weaponInventorySlot).size(slotSize, slotSize).pad(slotSize*0.05f).colspan(2);
        equipmentSlotsTable.add(armourInventorySlot).size(slotSize,slotSize).pad(slotSize*0.05f);
        add(equipmentSlotsTable);

    }

    void saveInventory() {
        Entity player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
        player.getComponent(PlayerInventoryComponent.class).inventoryItems = inventorySlotsTable.getItemsTable();
        player.getComponent(PlayerInventoryComponent.class).equippedItems = equipmentSlotsTable.getItemsTable();
    }

    void loadInventory() {
        Entity player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
        if (player.getComponent(PlayerInventoryComponent.class).equippedItems != null) {
            Array<Item> equipment = player.getComponent(PlayerInventoryComponent.class).equippedItems;
            equipmentSlotsTable.fillTable(equipment,dragAndDrop);
        }
        if (player.getComponent(PlayerInventoryComponent.class).inventoryItems != null) {
            Array<Item> inventory = player.getComponent(PlayerInventoryComponent.class).inventoryItems;
            inventorySlotsTable.fillTable(inventory,dragAndDrop);
        }
    }

    private Item getActiveWeapon() {
        if (weaponInventorySlot.getItem() == null) {
            return null;
        }
        return weaponInventorySlot.getItem();
    }

    private void setActivePlayerWeapon() {
        engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first()
                .getComponent(PlayerComponent.class).activeWeapon = getActiveWeapon();
        if (!weaponInventorySlot.hasItem()) {
            engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first()
                    .getComponent(PlayerComponent.class).activeWeapon = null;

        }
    }

    private void removeActivePlayerWeapon() {
        if (weaponInventorySlot.hasItem()) {
            engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first()
                    .getComponent(PlayerComponent.class).activeWeapon = null;
        }
    }


    @Override
    public void onNotify(InventorySlot inventorySlot, InventorySlotEvent event) {
        if (event == InventorySlotEvent.ADDED_ITEM) {
            setActivePlayerWeapon();
        }
        if (event == InventorySlotEvent.REMOVED_ITEM) {
            removeActivePlayerWeapon();
        }
    }
}
