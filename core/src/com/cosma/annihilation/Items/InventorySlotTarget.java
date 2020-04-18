package com.cosma.annihilation.Items;

import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

public class InventorySlotTarget extends DragAndDrop.Target {

    private InventorySlot targetSlot;

    public InventorySlotTarget(InventorySlot actor) {
        super(actor);
        targetSlot = actor;
    }

    @Override
    public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
        return true;
    }

    @Override
    public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {

        Item sourceItem = (Item) payload.getDragActor();
        Item targetItem = targetSlot.getItem();
        InventorySlot sourceSlot = ((InventorySlotSource)source).getSourceSlot();


        if( sourceItem == null ) {
            return;
        }
        if(!targetSlot.isSlotAcceptItemType(sourceItem.getItemType()))  {
            sourceSlot.add(sourceItem);
            return;
        }

        if(!targetSlot.hasItem()){
            sourceSlot.updateItemCounter();
            sourceSlot.notifyObservers(sourceSlot, InventorySlotObserver.InventorySlotEvent.REMOVED_ITEM);
            targetSlot.add(sourceItem);
        }else{
            if( sourceItem.isSameItemType(targetItem) && sourceItem.isStackable()){
                sourceSlot.notifyObservers(sourceSlot, InventorySlotObserver.InventorySlotEvent.REMOVED_ITEM);
                sourceSlot.updateItemCounter();
                targetSlot.add(sourceItem);
            }else
//                if(targetItem.getCategory() == ItemType.GUNS && sourceItem.getCategory() == ItemType.AMMO){
//                    if(targetItem.get)
//                }else
                sourceSlot.add(sourceItem);
        }
    }
}
