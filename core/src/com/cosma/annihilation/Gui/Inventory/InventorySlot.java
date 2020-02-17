package com.cosma.annihilation.Gui.Inventory;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Items.InventoryItem;

public class InventorySlot extends Stack implements InventorySlotObservable{
    private int itemsAmount = 0;
    private int itemTypeFilter;
    private Image backgroundImage;
    private Label itemsAmountLabel;
    private Stack stack;
    private Array<InventorySlotObserver> observers;
    private float itemImageScale;

    public InventorySlot(){

        stack = new Stack();
        backgroundImage = new Image();
        Image backgroundImageStandard = new Image( Annihilation.getAssets().get("gfx/interface/gui_frame_64x64.png",Texture.class));
//        Image backgroundImageStandard = new Image( Annihilation.getAssets().get(GfxAssetDescriptors.defaultStack));
        stack.add(backgroundImageStandard);
        stack.setName("background");
        this.add(stack);
        itemsAmountLabel = new Label(String.valueOf(itemsAmount),Annihilation.getAssets().get("gfx/interface/uiskin.json", Skin.class));
        itemsAmountLabel.setFontScale(0.5f);
        itemsAmountLabel.setAlignment(Align.bottomRight);
        itemsAmountLabel.setVisible(false);
        this.add(itemsAmountLabel);
        this.itemImageScale = 1;
        observers = new Array<InventorySlotObserver>();

    }
    public InventorySlot(int itemTypeFilter,Image backgroundImage) {
        this();
        this.itemTypeFilter = itemTypeFilter;
        this.backgroundImage = backgroundImage;
        stack.add(backgroundImage);
    }

    public boolean isAcceptItemUseType(int itemType){
        if( itemTypeFilter == 0 ){
            return true;
        }else
            {
            return (( itemTypeFilter & itemType) == itemType);
        }
    }

    public boolean hasItem(){
        if( hasChildren() ){
            SnapshotArray<Actor> items = this.getChildren();
            return items.size > 2;
        }
        return false;
    }

    private void checkVisibilityOfItemCount(){
        if( itemsAmount < 2){
            itemsAmountLabel.setVisible(false);
        }else{
            itemsAmountLabel.setVisible(true);
        }
    }

    public void removeItem() {
        itemsAmount--;
        itemsAmountLabel.setText(String.valueOf(itemsAmount));
        checkVisibilityOfItemCount();
        notifyObservers(this, InventorySlotObserver.InventorySlotEvent.REMOVED_ITEM);

    }

    public void setImageScale(float scale){
        this.itemImageScale = scale;
    }


    private void addItem() {
        itemsAmount++;
        itemsAmountLabel.setText(String.valueOf(itemsAmount));
        checkVisibilityOfItemCount();
        notifyObservers(this,InventorySlotObserver.InventorySlotEvent.ADDED_ITEM);
    }

    public InventoryItem getInventoryItem(){
        InventoryItem actor = null;
        if( this.hasChildren() ){
            SnapshotArray<Actor> items = this.getChildren();
            if( items.size > 2 ){
                actor = (InventoryItem) items.peek();
            }
        }
        return actor;
    }

    public Array<Actor> getAllInventoryItems() {
        Array<Actor> items = new Array<Actor>();
        if( hasItem() ){
            SnapshotArray<Actor> arrayChildren = this.getChildren();
            int numInventoryItems =  arrayChildren.size - 2;
            for(int i = 0; i < numInventoryItems; i++) {
                removeItem();
                items.add(arrayChildren.pop());
            }
        }
        return items;
    }

    public int getItemsNumber(){
        if( hasChildren() ){
            SnapshotArray<Actor> items = this.getChildren();
            return items.size - 2;
        }
        return 0;
    }

    public void clearAllItems() {
        if( hasItem() ){
            SnapshotArray<Actor> arrayChildren = this.getChildren();
            int numInventoryItems =  getItemsNumber();
            for(int i = 0; i < numInventoryItems; i++) {
                removeItem();
                checkVisibilityOfItemCount();
                arrayChildren.pop();
            }
        }
    }

    @Override
    public void add(Actor actor) {
        super.add(actor);
        if( itemsAmountLabel == null ){
            return;
        }
        if( !actor.equals(stack) && !actor.equals(itemsAmountLabel) ) {
            addItem();
            actor.setScale(itemImageScale);
        }
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
        for (InventorySlotObserver observer: observers){
            observer.onNotify(inventorySlot,event);
        }
    }
}
