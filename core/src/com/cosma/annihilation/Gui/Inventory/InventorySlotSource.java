package com.cosma.annihilation.Gui.Inventory;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

public class InventorySlotSource extends DragAndDrop.Source {

    private EquipmentSlot sourceSlot;
    private DragAndDrop dragAndDrop;

    public InventorySlotSource(EquipmentSlot sourceSlot, DragAndDrop dragAndDrop) {
        super(sourceSlot.getItem());
        this.sourceSlot = sourceSlot;
        this.dragAndDrop = dragAndDrop;
    }
    @Override
    public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
        DragAndDrop.Payload payload = new DragAndDrop.Payload();



        sourceSlot = (EquipmentSlot)getActor().getParent();
        sourceSlot.removeItem();
        payload.setDragActor(getActor());
//        dragAndDrop.setDragActorPosition(-x, -y + getActor().getHeight());

        return payload;
    }

    @Override
    public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
        if( target == null){
            sourceSlot.add(payload.getDragActor());
        }
    }
    public EquipmentSlot getSourceSlot() {return sourceSlot;}
}
