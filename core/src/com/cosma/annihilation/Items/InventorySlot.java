package com.cosma.annihilation.Items;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;
import com.cosma.annihilation.Annihilation;

public class InventorySlot extends Stack implements InventorySlotObservable {
    private Array<ItemType> itemTypeFilter = new Array<>();
    private Label itemsAmountLabel;
    private Array<InventorySlotObserver> observers;

    public InventorySlot() {

        Stack backgroundStackDefault = new Stack();
        Image backgroundImageStandard = new Image(Annihilation.getAssets().get("gfx/interface/gui_frame.png", Texture.class));
        backgroundStackDefault.add(backgroundImageStandard);
        backgroundStackDefault.setName("background");
        this.add(backgroundStackDefault);
        itemsAmountLabel = new Label("", Annihilation.getLabelStyle());
        itemsAmountLabel.setAlignment(Align.topRight);
        itemsAmountLabel.setVisible(true);
        this.add(itemsAmountLabel);
        observers = new Array<>();

    }

    public InventorySlot(ItemType... args) {
        this();
        for (ItemType itemType : args) {
            itemTypeFilter.add(itemType);
        }
    }

    public boolean isSlotAcceptItemType(ItemType itemType) {
        if (itemTypeFilter == null || itemTypeFilter.size == 0) {
            return true;
        } else {
            for (ItemType type : itemTypeFilter) {

                if (itemType.equals(type))
                    return true;
            }
            return false;
        }
    }

    public boolean hasItem() {
        if (hasChildren()) {
            SnapshotArray<Actor> items = this.getChildren();
            return items.size > 2;
        }
        return false;
    }

    public void updateItemCounter() {
        if (hasItem()) {
            if (getItem().getItemAmount() >= 2) {
                itemsAmountLabel.setText(String.valueOf(getItem().getItemAmount()));
                itemsAmountLabel.setVisible(true);
            } else {
                itemsAmountLabel.setVisible(false);
            }
        } else {
            itemsAmountLabel.setVisible(false);
        }
    }

    public Item getItem() {
        Item actor = null;
        if (this.hasChildren()) {
            SnapshotArray<Actor> items = this.getChildren();
            if (items.size > 2) {
                actor = (Item) items.peek();
            }
        }
        return actor;
    }

    public Array<Actor> getAllInventoryItems() {
        Array<Actor> items = new Array<Actor>();
        if (hasItem()) {
            SnapshotArray<Actor> arrayChildren = this.getChildren();
            int numInventoryItems = arrayChildren.size - 2;
            for (int i = 0; i < numInventoryItems; i++) {
                items.add(arrayChildren.pop());
            }
        }
        return items;
    }

    public int getItemsNumber() {
        if (getChildren().size > 2) {
            return getItem().getItemAmount();
        }
        return 0;
    }

    public void clearItems() {
        if (hasItem()) {
            getItem().remove();
            updateItemCounter();
        }
    }
    public void hideItemCounter(){
        itemsAmountLabel.setVisible(false);
    }

    @Override
    public void add(Actor actor) {
        if (actor instanceof Item) {
            if (hasItem() && getItem().isSameItemType((Item) actor)) {
                Item item = getItem();
                item.setItemAmount(item.getItemAmount() + ((Item) actor).getItemAmount());

            } else {
                super.add(actor);

            }
            updateItemCounter();
        } else {
            super.add(actor);
        }

//        if(!actor.equals(backgroundStackDefault) && !actor.equals(itemsAmountLabel) ) {
//            if(actor instanceof Item && ((Item) actor).isStackable()){
//
//
//
//            }
//            actor.setScale(itemImageScale);
//            itemsAmountLabel.setText(String.valueOf((Item)actor));
//            checkVisibilityOfItemCount();
//            notifyObservers(this,InventorySlotObserver.InventorySlotEvent.ADDED_ITEM);
//
//        }
    }

    @Override
    public void register(InventorySlotObserver inventorySlotObserver) {
        observers.add(inventorySlotObserver);
    }

    @Override
    public void unregister(InventorySlotObserver inventorySlotObserver) {
        observers.removeValue(inventorySlotObserver, true);
    }

    @Override
    public void notifyObservers(InventorySlot inventorySlot, InventorySlotObserver.InventorySlotEvent event) {
        for (InventorySlotObserver observer : observers) {
            observer.onNotify(inventorySlot, event);
        }
    }
}
