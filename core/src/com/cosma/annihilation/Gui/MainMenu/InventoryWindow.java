package com.cosma.annihilation.Gui.MainMenu;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
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
    private InventorySlot weaponInventorySlot;
    private InventorySlot armourInventorySlot;
    private ClickListener clickListener;
    private ContextMenu contextMenu;

    public InventoryWindow(String title, Skin skin, Engine engine, float parentWidth) {
        super(title, skin);
        this.engine = engine;
        float slotSize = parentWidth * 0.120f;

        contextMenu = new ContextMenu("",skin);

        dragAndDrop = new DragAndDrop();
        clickListener = new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                InventorySlot inventorySlot = (InventorySlot) event.getListenerActor();
                if (inventorySlot.hasItem()) {
                    getStage().addActor(contextMenu);
                    System.out.println("mouse: " + Gdx.input.getX() +" " +  Gdx.input.getY());
                    contextMenu.setPosition(Gdx.input.getX(),getStage().getCamera().viewportHeight-Gdx.input.getY());

//                    add(contextMenu);
                }
            }
        };


        clickListener.setButton(Input.Buttons.RIGHT);
        //inventory table
        inventorySlotsTable = new InventoryTable();
        inventorySlotsTable.center().padTop(parentWidth * 0.05f);
        for (int i = 1; i <= 20; i++) {
            InventorySlot inventorySlot = new InventorySlot();
            inventorySlot.addListener(clickListener);
            inventorySlot.setImageScale(1f);
            dragAndDrop.addTarget(new InventorySlotTarget(inventorySlot));
            inventorySlotsTable.add(inventorySlot).size(slotSize, slotSize).pad(slotSize * 0.05f);
            if (i == 5 || i == 10 || i == 15 || i == 20) inventorySlotsTable.row();
        }
        add(inventorySlotsTable);
        row();

        //equipment table
        InventoryTable equipmentSlotsTable = new InventoryTable();
        armourInventorySlot = new InventorySlot(ItemType.ARMOUR);
        weaponInventorySlot = new InventorySlot(ItemType.WEAPON_SHORT, ItemType.WEAPON_LONG);
        weaponInventorySlot.register(this);

        dragAndDrop.addTarget(new InventorySlotTarget(armourInventorySlot));
        dragAndDrop.addTarget(new InventorySlotTarget(weaponInventorySlot));

        Table table = new Table();
        Label weaponLabel = new Label(Annihilation.getLocalText("i_weapon"),skin);
        Label armourLabel = new Label(Annihilation.getLocalText("i_armour"),skin);
        table.add(weaponLabel);
        table.add(weaponInventorySlot).size(slotSize, slotSize).pad(slotSize * 0.05f);
        table.row();
        table.add(armourLabel);
        table.add(armourInventorySlot).size(slotSize, slotSize).pad(slotSize * 0.05f);
        add(table);
    }

    void saveInventory() {
        Entity player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
        player.getComponent(PlayerInventoryComponent.class).inventoryItems = inventorySlotsTable.getItemsTable();
        player.getComponent(PlayerInventoryComponent.class).equippedArmour = armourInventorySlot.getItem();
        player.getComponent(PlayerInventoryComponent.class).equippedWeapon = weaponInventorySlot.getItem();
    }

    void loadInventory() {
        PlayerInventoryComponent playerInventoryComponent = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first().getComponent(PlayerInventoryComponent.class);
        if(playerInventoryComponent.equippedWeapon != null){
            weaponInventorySlot.clearItems();
            playerInventoryComponent.equippedWeapon .getCaptureListeners().clear();
            weaponInventorySlot.add(playerInventoryComponent.equippedWeapon);
            dragAndDrop.addSource(new InventorySlotSource(weaponInventorySlot, dragAndDrop));
        }
        if(playerInventoryComponent.equippedArmour != null){
            armourInventorySlot.clearItems();
            playerInventoryComponent.equippedArmour .getCaptureListeners().clear();
            armourInventorySlot.add(playerInventoryComponent.equippedWeapon);
            dragAndDrop.addSource(new InventorySlotSource(armourInventorySlot, dragAndDrop));
        }

        if (playerInventoryComponent.inventoryItems != null) {
            Array<Item> inventory = playerInventoryComponent.inventoryItems;
            inventorySlotsTable.fillTable(inventory, dragAndDrop);
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
