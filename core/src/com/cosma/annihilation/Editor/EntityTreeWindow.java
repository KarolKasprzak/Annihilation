package com.cosma.annihilation.Editor;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Json;
import com.cosma.annihilation.Components.AiComponent;
import com.cosma.annihilation.Components.PhysicsComponent;
import com.cosma.annihilation.Components.SerializationComponent;
import com.cosma.annihilation.Editor.CosmaMap.EntityEditOptionsWindow;
import com.cosma.annihilation.EntityEngine.core.Component;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.Screens.MapEditor;
import com.cosma.annihilation.Utils.Serialization.EntityReader;
import com.cosma.annihilation.Utils.Util;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.dialog.ConfirmDialogListener;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTree;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;

import java.util.HashMap;

public class EntityTreeWindow extends VisWindow implements InputProcessor {

    private HashMap<String, FileHandle> jsonList;
    private World world;
    private MapEditor mapEditor;
    private boolean canAddEntity = false;
    private String selectedEntityName;
    private Body selectedBody;
    private boolean canMove = false;
    private boolean canMoveWithDrag = false;
    private boolean isLeftMouseButtonPressed = false;
    private Vector3 vector3tempt = new Vector3();
    private Vector3 vector3tempt1 = new Vector3();
    private Json json;


    public EntityTreeWindow(World world, MapEditor mapEditor) {
        super("Entity:");
        this.world = world;
        this.mapEditor = mapEditor;


        TableUtils.setSpacingDefaults(this);
        columnDefaults(0).left();
        jsonList = new HashMap<>();
        json = new Json();
        json.setSerializer(Entity.class, new EntityReader(world));

        VisCheckBox moveCheckBox = new VisCheckBox("Move: ");
        moveCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(moveCheckBox.isChecked()){
                    canMoveWithDrag = true;
                }else{
                    canMoveWithDrag = false;
                }
            }
        });


        final VisTree tree = new VisTree();
        Node treeRoot = new Node(new VisLabel("Entity:"));
        Node checkBoxNode = new Node(moveCheckBox);
        treeRoot.add(checkBoxNode);
        FileHandle file = Gdx.files.local("entity");
        for (FileHandle rootDirectory : file.list()) {
            if (rootDirectory.isDirectory()) {
                Node node = new Node(new VisLabel(rootDirectory.nameWithoutExtension()));
                treeRoot.add(node);
                for (FileHandle childrenDirectory : rootDirectory.list(".json")) {
                    VisLabel label = new VisLabel(childrenDirectory.nameWithoutExtension());
                    label.setName(childrenDirectory.nameWithoutExtension());
                    Node childrenNode = new Node(label);
                    jsonList.put(childrenDirectory.nameWithoutExtension(), childrenDirectory);
                    node.add(childrenNode);
                }
            }
        }
        treeRoot.setExpanded(true);

        tree.add(treeRoot);

        add(tree).expand().fill();

        setSize(150, 380);
        setPosition(0, 400);

        tree.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!tree.getSelection().isEmpty() && tree.getSelection().first().getActor() instanceof VisLabel) {
                    VisLabel label = ((VisLabel) tree.getSelection().first().getActor());
                    if (label.getName() != null) {
                        selectedEntityName = label.getName();
                        canAddEntity = true;
                        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Crosshair);
                    }
                }
            }
        });
    }

    private void createEntity(String key, float x, float y) {

        Entity entity = json.fromJson(Entity.class, jsonList.get(key));
        for (Component component : entity.getComponents()) {
            if (component instanceof PhysicsComponent) {
                ((PhysicsComponent) component).body.setTransform(x, y, 0);
                continue;
            }
            if (component instanceof AiComponent) {
                ((AiComponent) component).startPosition.set(x, y);
            }
        }
        mapEditor.getMap().addEntity(entity);
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(final int screenX, final int screenY, int pointer, int button) {
        Vector3 worldCoordinates = vector3tempt.set(screenX,screenY,0);
        final Vector3 vec = mapEditor.getCamera().unproject(worldCoordinates);

        if(canMoveWithDrag && button == Input.Buttons.LEFT){
            isLeftMouseButtonPressed = true;
        }else{
            isLeftMouseButtonPressed = false;
        }

        if (canAddEntity && button == Input.Buttons.LEFT) {
            createEntity(selectedEntityName, vec.x, vec.y);
            canAddEntity = false;
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        }
        if (canAddEntity && button == Input.Buttons.RIGHT) {
            canAddEntity = false;
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        }

        if (canMove && button == Input.Buttons.RIGHT) {
            canMove = false;
            selectedBody = null;
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        }

        if (canMove && button == Input.Buttons.LEFT) {
            selectedBody.setTransform(vec.x, vec.y, 0);
            if (selectedBody.getUserData() instanceof Entity) {
                Entity entity = (Entity) selectedBody.getUserData();
                if (entity.getComponent(AiComponent.class) != null) {
                    entity.getComponent(AiComponent.class).startPosition.set(vec.x, vec.y);
                }
            }
            selectedBody.setActive(true);
            selectedBody.setAwake(true);
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            canMove = false;
            selectedBody = null;
        }

        if (button == Input.Buttons.RIGHT) {
            world.QueryAABB(new QueryCallback() {
                @Override
                public boolean reportFixture(final Fixture fixture) {
                    for (Entity entity : mapEditor.getMap().getEntityArrayList()) {
                        if (fixture.getBody() == entity.getComponent(PhysicsComponent.class).body) {
                            final int delete = 1;
                            final int move = 2;
                            final int options = 3;
                            final int cancel = 4;
                            Dialogs.showConfirmDialog(getStage(), entity.getComponent(SerializationComponent.class).entityName, "what do you want?",
                                    new String[]{"delete", "move", "options", "cancel"}, new Integer[]{delete, move, options, cancel},
                                    new ConfirmDialogListener<Integer>() {
                                        @Override
                                        public void result(Integer result) {
                                            if (result == delete) {
                                                mapEditor.getMap().removeEntity(((Entity) fixture.getBody().getUserData()));
                                                world.destroyBody(fixture.getBody());
                                            }

                                            if (result == move) {
                                                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Crosshair);
                                                selectedBody = fixture.getBody();
                                                canMove = true;
                                            }

                                            if (result == options) {
                                                EntityEditOptionsWindow window = new EntityEditOptionsWindow(entity,mapEditor.getCamera());
                                                getStage().addActor(window);
                                            }

                                            if (result == cancel) {
//
                                            }
                                        }
                                    }).setPosition(Gdx.input.getX(), Gdx.input.getY());
                        }
                    }
                    return false;
                }
            }, vec.x - 0.2f, vec.y - 0.2f, vec.x + 0.2f, vec.y + 0.2f);
        }


        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 worldCoordinates = vector3tempt.set(screenX,screenY,0);
        Vector3 vec = mapEditor.getCamera().unproject(worldCoordinates);
        Vector3 deltaWorldCoordinates = vector3tempt1.set(screenX - Gdx.input.getDeltaX(), screenY - Gdx.input.getDeltaY(), 0);
        Vector3 deltaVec = mapEditor.getCamera().unproject(deltaWorldCoordinates);
        float amountX, amountY;

        if (canMoveWithDrag && isLeftMouseButtonPressed && selectedBody != null) {
            amountX = vec.x - deltaVec.x;
            amountY = vec.y - deltaVec.y;
            selectedBody.setTransform(selectedBody.getPosition().x+amountX, selectedBody.getPosition().y+amountY, 0);
        }

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if(canMoveWithDrag){
            Vector3 worldCoordinates = vector3tempt.set(screenX,screenY,0);
            final Vector3 vec = mapEditor.getCamera().unproject(worldCoordinates);
            selectedBody = null;
            Util.setCursorSystem();
            world.QueryAABB(new QueryCallback() {
                @Override
                public boolean reportFixture(final Fixture fixture) {
                    for (Entity entity : mapEditor.getMap().getEntityArrayList()) {
                        if(fixture.getBody() == entity.getComponent(PhysicsComponent.class).body) {
                         selectedBody = fixture.getBody();
                         Util.setCursorMove();
                         return false;
                        }
                    }
                    return false;
                }
            }, vec.x - 0.1f, vec.y - 0.1f, vec.x + 0.1f, vec.y + 0.1f);
        }
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
