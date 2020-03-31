package com.cosma.annihilation.Gui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Array;
import com.cosma.annihilation.Components.ContainerComponent;
import com.cosma.annihilation.Components.PlayerComponent;
import com.cosma.annihilation.Components.PlayerInventoryComponent;
import com.cosma.annihilation.Items.InventorySlot;
import com.cosma.annihilation.Items.InventorySlotTarget;
import com.cosma.annihilation.Items.Item;
import com.cosma.annihilation.Items.Tools;
import com.cosma.annihilation.Utils.Util;

public class LootWindow extends GuiWindow {

    private DragAndDrop dragAndDrop;
    private InventoryTable slotTable;
    private ActorGestureListener listener;
    private Engine engine;
    private ContainerComponent containerComponent;


    public LootWindow(Skin skin, Engine engine) {
        super("", skin);

        this.engine = engine;
        dragAndDrop = new DragAndDrop();
        Array<InventorySlot> slotArray = new Array<>();

        debugAll();

        slotTable = new InventoryTable();
        slotTable.setDebug(false);

        listener = new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                if (count >= 2) {
                    if (((InventorySlot) event.getListenerActor()).hasItem()) {
                        moveItemToPlayerEquipment((InventorySlot) event.getListenerActor());
                    }
                }
            }
        };

        for (int i = 0; i < 4; i++) {
            InventorySlot inventorySlot = new InventorySlot();
            inventorySlot.addListener(listener);
            inventorySlot.setImageScale(1f);
            dragAndDrop.addTarget(new InventorySlotTarget(inventorySlot));
            slotTable.add(inventorySlot).size(Util.setWindowHeight(0.13f), Util.setWindowHeight(0.13f)).pad(Util.setWindowHeight(0.005f));
            slotArray.add(inventorySlot);
        }

        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                containerComponent = null;
//                PlayerInventoryWindow.clearItemsTable(slotTable);
//                for(InventorySlot inventorySlot: slotArray){
//                    inventorySlot.clearItems();
//
//                }
                close();

            }
        });

        this.add(slotTable).center().fillX().colspan(2).pad(80);
        this.row();
        this.add(closeButton).bottom().center().size(Util.setButtonWidth(1.7f), Util.setButtonHeight(1.7f));
        pack();
    }

    private void moveItemToPlayerEquipment(InventorySlot equipmentSlot) {
        Array<Item> playerInventory = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first().getComponent(PlayerInventoryComponent.class).inventoryItems;
        if (playerInventory.size <= 24) {
            equipmentSlot.getItem().setTableIndex(Tools.findFreeIndex(playerInventory,24));
            playerInventory.add(equipmentSlot.getItem());
            containerComponent.itemList.removeValue(equipmentSlot.getItem(),true);
            equipmentSlot.clearItems();
        }
    }

    public void open(Entity entity) {
        if(entity.getComponent(ContainerComponent.class) != null){
            containerComponent = entity.getComponent(ContainerComponent.class);
            slotTable.fillTable(containerComponent.itemList,dragAndDrop);
        }
        moveToCenter();
    }
}
