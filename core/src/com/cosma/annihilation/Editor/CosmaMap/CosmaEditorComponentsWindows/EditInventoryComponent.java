package com.cosma.annihilation.Editor.CosmaMap.CosmaEditorComponentsWindows;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Components.ContainerComponent;
import com.cosma.annihilation.Items.Item;
import com.cosma.annihilation.Items.Tools;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.spinner.IntSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;

public class EditInventoryComponent extends VisWindow {



    public EditInventoryComponent(ContainerComponent containerComponent) {

        super(containerComponent.name);


        VisList<Item> itemList = new VisList<>();
        itemList.setItems(containerComponent.itemList);


        VisTextButton removeButton = new VisTextButton("Remove item");
        removeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                containerComponent.itemList.removeValue(itemList.getSelected(), true);
                itemList.clearItems();
                itemList.setItems(containerComponent.itemList);
            }
        });

        VisSelectBox<String> itemSelectBox = new VisSelectBox<>();
        itemSelectBox.setItems(Annihilation.getItemIdList());
        final IntSpinnerModel intModel = new IntSpinnerModel(1, 1, 100, 1);
        Spinner intSpinner = new Spinner("amount:", intModel);

        VisTextButton addItemButton = new VisTextButton("Add item");
        addItemButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (containerComponent.itemList.size < 4) {
                    Item item = Annihilation.getItem(itemSelectBox.getSelected());
                    if(item.isStackable()){
                        item.setItemAmount(intModel.getValue());
                    }else{
                        item.setItemAmount(1);
                    }

                    item.setTableIndex(Tools.findFreeIndex(containerComponent.itemList, 4));
                    containerComponent.itemList.add(item);
                    itemList.clearItems();
                    itemList.setItems(containerComponent.itemList);
                }
            }
        });

        add(itemList).pad(10);
        row();
        add(removeButton).pad(10);
        row();
        add(itemSelectBox).pad(10);
        row();
        add(intSpinner).pad(10);
        row();
        add(addItemButton).pad(10);
        setCenterOnAdd(true);
        addCloseButton();
        pack();
        setSize(getWidth(), getHeight() * 1.2f);
    }
}


