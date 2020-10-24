package com.cosma.annihilation.Editor.CosmaMap;


import com.cosma.annihilation.Components.*;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorComponentsWindows.EditActionComponent;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorComponentsWindows.EditGateComponent;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorComponentsWindows.EditParallaxComponent;
import com.cosma.annihilation.EntityEngine.core.Component;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Items.Item;
import com.cosma.annihilation.Items.Tools;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.spinner.IntSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;

public class EntityEditOptionsWindow extends VisWindow {
    public EntityEditOptionsWindow(Entity entity, OrthographicCamera camera) {
        super(entity.getComponent(SerializationComponent.class).entityName);
        addCloseButton();

        for (Component component : entity.getComponents()) {
            if (component instanceof ContainerComponent) {
                VisTextButton textButton = new VisTextButton("Edit inventory");
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        EntityInventoryWindow addWindow = new EntityInventoryWindow((ContainerComponent) component);
                        getStage().addActor(addWindow);
                        close();
                    }
                });
                add(textButton).pad(5);
                row();
            }
            if (component instanceof PhysicsComponent) {
                VisTextButton textButton = new VisTextButton("Edit body");
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        //*todo
                    }
                });
                add(textButton).pad(5);
                row();
            }
            if (component instanceof ParallaxComponent) {
                VisTextButton textButton = new VisTextButton("Edit parallax");
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {

                        EditParallaxComponent editParallaxComponent = new EditParallaxComponent((ParallaxComponent) component,entity);
                        getStage().addActor(editParallaxComponent);
                        close();
                    }
                });
                add(textButton).pad(5);
                row();
            }

            if (component instanceof GateComponent) {
                VisTextButton textButton = new VisTextButton("Edit gate");
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {

                        EditGateComponent editGateComponent = new EditGateComponent((GateComponent) component);
                        getStage().addActor(editGateComponent);
                        close();
                    }
                });
                add(textButton).pad(5);
                row();
            }

            if (component instanceof ActionComponent) {
                VisTextButton textButton = new VisTextButton("Edit action");
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        EditActionComponent addWindow = new EditActionComponent((ActionComponent) component,entity,camera);
                        getStage().addActor(addWindow);
                        close();
                    }
                });
                add(textButton).pad(5);
                row();
            }
        }
        pack();
        setCenterOnAdd(true);
    }







    class EntityInventoryWindow extends VisWindow {
        private void drawItem(ContainerComponent containerComponent, VisTable visTable){
            for(Item item: containerComponent.itemList){
                visTable.add(new VisLabel((item.getItemId() + " " + item.getItemAmount())));
                visTable.row();
            }
        }

        EntityInventoryWindow(ContainerComponent containerComponent) {
            super(containerComponent.name);

            VisTable itemTable = new VisTable();

            this.add(itemTable);
            drawItem(containerComponent,itemTable);


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
                        item.setItemAmount(intModel.getValue());
                        item.setTableIndex(Tools.findFreeIndex(containerComponent.itemList, 4));
                        containerComponent.itemList.add(item);
                        itemList.clearItems();
                        itemList.setItems(containerComponent.itemList);
                    }
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
            setSize(getWidth(), getHeight() * 2);
        }
    }
}


