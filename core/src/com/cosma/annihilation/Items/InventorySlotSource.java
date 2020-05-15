package com.cosma.annihilation.Items;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

public class InventorySlotSource extends DragAndDrop.Source {

    private InventorySlot sourceSlot;

    public InventorySlotSource(InventorySlot sourceSlot) {
        super(sourceSlot.getItem());
        this.sourceSlot = sourceSlot;
    }
    @Override
    public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
        DragAndDrop.Payload payload = new DragAndDrop.Payload();
        sourceSlot = (InventorySlot)getActor().getParent();
        sourceSlot.hideItemCounter();
        payload.setDragActor(getActor());
        return payload;
    }

    @Override
    public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
        if( target == null){
            sourceSlot.add(payload.getDragActor());
        }
    }



    public InventorySlot getSourceSlot() {return sourceSlot;}
}
