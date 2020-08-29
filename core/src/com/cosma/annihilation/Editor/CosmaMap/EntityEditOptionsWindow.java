package com.cosma.annihilation.Editor.CosmaMap;


import com.cosma.annihilation.Components.PhysicsComponent;
import com.cosma.annihilation.EntityEngine.core.Component;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Components.ActionComponent;
import com.cosma.annihilation.Components.ContainerComponent;
import com.cosma.annihilation.Components.SerializationComponent;
import com.cosma.annihilation.Items.Item;
import com.cosma.annihilation.Items.Tools;
import com.cosma.annihilation.Utils.Enums.EntityAction;
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
                        AddEntityInventoryWindow addWindow = new AddEntityInventoryWindow((ContainerComponent) component);
                        getStage().addActor(addWindow);
                        close();
                    }
                });
                add(textButton);
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
                add(textButton);
                row();
            }
            if (component instanceof ActionComponent) {
                VisTextButton textButton = new VisTextButton("Edit action");
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        AddEntityActionComponentEdit addWindow = new AddEntityActionComponentEdit((ActionComponent) component,entity,camera);
                        getStage().addActor(addWindow);
                        close();
                    }
                });
                add(textButton);
                row();
            }
        }
        pack();
        setCenterOnAdd(true);
    }

    class AddEntityActionComponentEdit extends VisWindow
    {
        @Override
        public void act(float delta) {
            super.act(delta);
            if(waitForClick){
                if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
                    Vector3 tempCoords = new Vector3();
                    tempCoords.set(Gdx.input.getX(),Gdx.input.getY(),0);
                    camera.unproject(tempCoords);
                    actionComponent.actionTargetPosition = new Vector2(tempCoords.x,tempCoords.y);
                    waitForClick = false;
                }
            }
        }

        private boolean waitForClick = false;
        private ActionComponent actionComponent;
        private OrthographicCamera camera;

        public AddEntityActionComponentEdit(ActionComponent actionComponent, Entity entity,OrthographicCamera camera) {
            super("");
            this.actionComponent = actionComponent;
            this.camera = camera;

            VisLabel targetLabel = new VisLabel("Target: ");
            VisTextField targetTextField = new VisTextField();
            if(actionComponent.actionTargetName != null){
                targetTextField.setText(actionComponent.actionTargetName);
            }
            VisTextButton saveButton = new VisTextButton("save");
            Array<String> actionList = new Array<>();
            for(EntityAction action: EntityAction.values()){
                actionList.add(action.toString());
            }
            VisSelectBox<String> actionSelectBox = new VisSelectBox<>();
            actionSelectBox.setItems(actionList);

            saveButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    actionComponent.actionTargetName = targetTextField.getText();
                    if(!actionSelectBox.getSelected().equals("NOTHING")){
                        actionComponent.action = EntityAction.valueOf(actionSelectBox.getSelected());
                    }
                    close();
                }
            });

            VisTextButton setActionTargetButton = new VisTextButton("set target position");

            setActionTargetButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    waitForClick = true;
                }
            });


            add(targetLabel);
            add(targetTextField).width(150);
            row();
            add(actionSelectBox);
            row();
            add(setActionTargetButton);
            row();
            add(saveButton);

            pack();
            addCloseButton();
            setSize(getWidth(), getHeight() * 2);
            setCenterOnAdd(true);



        }
    }




    class AddEntityInventoryWindow extends VisWindow {
        private void drawItem(ContainerComponent containerComponent, VisTable visTable){
            for(Item item: containerComponent.itemList){
                visTable.add(new VisLabel((item.getItemId() + " " + item.getItemAmount())));
                visTable.row();
            }
        }

        AddEntityInventoryWindow(ContainerComponent containerComponent) {
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


