package com.cosma.annihilation.Items;


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

public class InventorySlot extends Stack implements InventorySlotObservable{
    private int itemAmount = 0;
    private Array<ItemType> itemTypeFilter = new Array<>();
    private Image backgroundImage;
    private Label itemsAmountLabel;
    private Stack backgroundStackDefault;
    private Array<InventorySlotObserver> observers;
    private float itemImageScale;

    public InventorySlot(){
        backgroundStackDefault = new Stack();
        backgroundImage = new Image();
        Image backgroundImageStandard = new Image( Annihilation.getAssets().get("gfx/interface/gui_frame_64x64.png",Texture.class));
//        Image backgroundImageStandard = new Image( Annihilation.getAssets().get(GfxAssetDescriptors.defaultStack));
        backgroundStackDefault.add(backgroundImageStandard);
        backgroundStackDefault.setName("background");
        this.add(backgroundStackDefault);
        itemsAmountLabel = new Label(String.valueOf(itemAmount),Annihilation.getAssets().get("gfx/interface/uiskin.json", Skin.class));
        itemsAmountLabel.setFontScale(1f);
        itemsAmountLabel.setAlignment(Align.bottomRight);
        itemsAmountLabel.setVisible(true);
        this.add(itemsAmountLabel);
        this.itemImageScale = 1;
        observers = new Array<InventorySlotObserver>();
        checkVisibilityOfItemCount();

    }
    public InventorySlot(Image backgroundImage, ItemType... args) {
        this();
        for(ItemType itemType: args){
            itemTypeFilter.add(itemType);
        }
        this.backgroundImage = backgroundImage;
        backgroundStackDefault.add(backgroundImage);
    }

    public boolean isSlotAcceptItemType(ItemType itemType){
        if(itemTypeFilter == null|| itemTypeFilter.size == 0){
            System.out.println("accept");
            return true;
        }else
            {
            for(ItemType type: itemTypeFilter){

                if(itemType.equals(type))
                    return true;
            }
            return false;
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
        if( itemAmount >= 2 ){
            itemsAmountLabel.setVisible(true);
        }else{
            itemsAmountLabel.setVisible(false);
        }
    }

    public void decreaseItemAmount() {
        itemAmount--;
        itemsAmountLabel.setText(String.valueOf(itemAmount));
        checkVisibilityOfItemCount();
        notifyObservers(this, InventorySlotObserver.InventorySlotEvent.REMOVED_ITEM);

    }

    public void setImageScale(float scale){
        this.itemImageScale = scale;
    }


    private void increaseItemAmount() {
        itemAmount++;
        itemsAmountLabel.setText(String.valueOf(itemAmount));
        checkVisibilityOfItemCount();
        notifyObservers(this,InventorySlotObserver.InventorySlotEvent.ADDED_ITEM);
    }

    public Item getItem(){
        Item actor = null;
        if( this.hasChildren() ){
            SnapshotArray<Actor> items = this.getChildren();
            for(int i1 = 0; i1 < items.size; i1++  ){
            }
            if( items.size > 2 ){
                actor = (Item) items.peek();
            }
        }
        return actor;
    }

    public Array<Actor> getAllInventoryItems() {
        Array<Actor> items = new Array<Actor>();
        if(hasItem()){
            SnapshotArray<Actor> arrayChildren = this.getChildren();
            int numInventoryItems = arrayChildren.size - 2;
            for(int i = 0; i < numInventoryItems; i++) {
                decreaseItemAmount();
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

    public void clearItems() {
        if(hasItem()){
            SnapshotArray<Actor> arrayChildren = this.getChildren();
            System.out.println("childre size: " +  this.getChildren().size);
            Item item = this.getItem();
            int numInventoryItems =  getItemsNumber();
            for(int i = 0; i < numInventoryItems; i++) {
                decreaseItemAmount();
                checkVisibilityOfItemCount();
                item.remove();
//                arrayChildren.pop();
                System.out.println("childre size after: " +  this.getChildren().size);
            }
            System.out.println("has parent: " + item.hasParent());
//            Group group = item.getParent();

            System.out.println("has parent: " + item.hasParent());
        }
    }

    @Override
    public void add(Actor actor) {
        super.add(actor);
        if(itemsAmountLabel == null ){
            return;
        }
        if(!actor.equals(backgroundStackDefault) && !actor.equals(itemsAmountLabel) ) {
            increaseItemAmount();
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
