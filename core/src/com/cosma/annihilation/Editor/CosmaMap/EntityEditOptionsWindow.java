package com.cosma.annihilation.Editor.CosmaMap;


import com.cosma.annihilation.Components.*;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorComponentsWindows.EditActionComponent;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorComponentsWindows.EditGateComponent;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorComponentsWindows.EditInventoryComponent;
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
                        EditInventoryComponent addWindow = new EditInventoryComponent((ContainerComponent) component);
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
}


