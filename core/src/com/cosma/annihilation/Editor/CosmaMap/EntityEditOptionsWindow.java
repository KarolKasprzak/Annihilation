package com.cosma.annihilation.Editor.CosmaMap;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.cosma.annihilation.Components.BodyComponent;
import com.cosma.annihilation.Components.ContainerComponent;
import com.cosma.annihilation.Components.SerializationComponent;
import com.cosma.annihilation.Gui.Inventory.InventoryItemLocation;
import com.cosma.annihilation.Items.InventoryItem;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.spinner.IntSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;

public class EntityEditOptionsWindow extends VisWindow {
    public EntityEditOptionsWindow(Entity entity) {
        super(entity.getComponent(SerializationComponent.class).entityName);
        addCloseButton();

        for(Component component: entity.getComponents()){
            if(component instanceof ContainerComponent){
                VisTextButton textButton = new VisTextButton("Edit inventory");
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        AddEntityInventoryWindow addWindow = new AddEntityInventoryWindow((ContainerComponent) component);
                        getStage().addActor(addWindow);
                        close();
                    }
                });
                add(textButton);
                row();
            }
            if(component instanceof BodyComponent){
                VisTextButton textButton = new VisTextButton("Edit body");
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        //*todo
                    }
                });
                add(textButton);
                row();
            }
        }
        pack();
        setCenterOnAdd(true);
    }
    class AddEntityInventoryWindow extends VisWindow {
        AddEntityInventoryWindow(ContainerComponent containerComponent) {
            super(containerComponent.name);
            VisList<InventoryItemLocation> itemList = new VisList<>();
            itemList.setItems(containerComponent.itemLocations);

            VisTextButton removeButton = new VisTextButton("Remove item");
            removeButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    System.out.println(containerComponent.itemLocations.size);
                    containerComponent.itemLocations.removeValue(itemList.getSelected(),true);
                    itemList.clearItems();
                    itemList.setItems(containerComponent.itemLocations);
                }
            });

            VisSelectBox<Enum> itemSelectBox = new VisSelectBox<>();
            itemSelectBox.setItems(InventoryItem.ItemID.values());

            final IntSpinnerModel intModel = new IntSpinnerModel(1, 1, 100, 1);
            Spinner intSpinner = new Spinner("amount:", intModel);

            VisTextButton addItemButton = new VisTextButton("Add item");
            addItemButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if(containerComponent.itemLocations.size<4){
                        InventoryItemLocation item = new InventoryItemLocation();
                        item.setItemsAmount(intModel.getValue());
                        item.setItemID(itemSelectBox.getSelected().toString());

                        Array<Integer> usedSlots = new Array<>();
                        for(InventoryItemLocation itemLocation: containerComponent.itemLocations){
                            usedSlots.add(itemLocation.getTableIndex());
                        }
                        for(int i =0; i < 4; i++){
                            if(!usedSlots.contains(i,true)){
                                item.setTableIndex(i);
                                break;
                            }
                        }
                        containerComponent.itemLocations.add(item);
                        itemList.clearItems();
                        itemList.setItems(containerComponent.itemLocations);
                    }else System.out.println("container is full");

                }
            });



            add(itemList);
            row();
            add(removeButton);
            row();
            add();
            row();
            add(itemSelectBox);
            row();
            add(intSpinner);
            row();
            add(addItemButton);
            setCenterOnAdd(true);
            addCloseButton();
            pack();
            setSize(getWidth(),getHeight()*2);
        }
    }
}
