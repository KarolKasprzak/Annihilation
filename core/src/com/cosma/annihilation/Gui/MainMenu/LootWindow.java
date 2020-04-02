package com.cosma.annihilation.Gui.MainMenu;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Array;
import com.cosma.annihilation.Components.ContainerComponent;
import com.cosma.annihilation.Components.PlayerComponent;
import com.cosma.annihilation.Components.PlayerInventoryComponent;
import com.cosma.annihilation.Gui.GuiWindow;
import com.cosma.annihilation.Gui.InventoryTable;
import com.cosma.annihilation.Items.InventorySlot;
import com.cosma.annihilation.Items.InventorySlotTarget;
import com.cosma.annihilation.Items.Item;
import com.cosma.annihilation.Items.Tools;

public class LootWindow extends GuiWindow {
    private DragAndDrop dragAndDrop;
    private InventoryTable slotTable;
    private ContainerComponent containerComponent;
    private Entity playerEntity;

    public LootWindow(Skin skin, Engine engine, float parentWidth) {
        super("", skin);
        dragAndDrop = new DragAndDrop();
        Array<InventorySlot> slotArray = new Array<>();
        slotTable = new InventoryTable();
        float slotSize = parentWidth * 0.120f;
        ActorGestureListener listener = new ActorGestureListener() {
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

        for (int i = 0; i < 8; i++) {
            InventorySlot inventorySlot = new InventorySlot();
            inventorySlot.addListener(listener);
            inventorySlot.setImageScale(1f);
            dragAndDrop.addTarget(new InventorySlotTarget(inventorySlot));
            slotTable.add(inventorySlot).size(slotSize, slotSize).pad(slotSize * 0.05f);
            slotArray.add(inventorySlot);
            if (i == 3) slotTable.row();
        }
        this.add(slotTable).center().pad(slotSize* 0.12f);

        playerEntity = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
        pack();
    }

    private void moveItemToPlayerEquipment(InventorySlot equipmentSlot) {
        Array<Item> playerInventory = playerEntity.getComponent(PlayerInventoryComponent.class).inventoryItems;
        if (playerInventory.size <= 24) {
            equipmentSlot.getItem().setTableIndex(Tools.findFreeIndex(playerInventory, 24));
            playerInventory.add(equipmentSlot.getItem());
            containerComponent.itemList.removeValue(equipmentSlot.getItem(), true);
            equipmentSlot.clearItems();
        }
    }

    public void initialize() {
        if (playerEntity.getComponent(PlayerComponent.class).processedEntity.getComponent(ContainerComponent.class)!= null) {
            containerComponent = playerEntity.getComponent(PlayerComponent.class).processedEntity.getComponent(ContainerComponent.class);
            this.getTitleTable().clearChildren();

            this.getTitleLabel().setText("------- "+containerComponent.name+" -------");
            this.getTitleTable().add(this.getTitleLabel()).center();
            slotTable.fillTable(containerComponent.itemList, dragAndDrop);
        }
    }

    @Override
    public void close() {
        super.close();
        this.getTitleLabel().setText("");
    }
}
