package com.cosma.annihilation.Editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorObject.MapObject;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorObject.RectangleObject;
import com.cosma.annihilation.Editor.CosmaMap.ObjectMapLayer;
import com.cosma.annihilation.Screens.MapEditor;
import com.cosma.annihilation.Utils.Util;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.*;

public class ObjectPanel extends VisWindow implements InputProcessor {

    private MapEditor mapEditor;
    private VisTextButton createRectangleButton, createJointButton;
    private float x1, y1, x2, y2;
    private BodyDef.BodyType bodyType = BodyDef.BodyType.StaticBody;
    private VisCheckBox setStaticBox, setKinematicBox, setDynamicBox ,enableBodyEdit;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private Array<Body> bodies = new Array<>();
    private Body selectedBody;
    private boolean canDragRight, canDragLeft, canDragUp, canDragDown, canDragObject, canRotateObject,
            isLeftButtonPressed, isRightButtonPressed,canDraw = false,canCreateBox = false, canOpenEditWindow =false, canBodyEdit = false;
    private RectangleObject selectedObject;

    public ObjectPanel(final MapEditor mapEditor) {
        super("Objects:");
        this.mapEditor = mapEditor;
        this.shapeRenderer = mapEditor.getShapeRenderer();
        this.camera = mapEditor.getCamera();

        TableUtils.setSpacingDefaults(this);
        columnDefaults(0).left();

        createRectangleButton = new VisTextButton("add box");
        createJointButton = new VisTextButton("add joint");
        setStaticBox = new VisCheckBox("Static", true);
        setStaticBox.setFocusBorderEnabled(false);
        setDynamicBox = new VisCheckBox("Dynamic");
        setDynamicBox.setFocusBorderEnabled(false);
        setKinematicBox = new VisCheckBox("Kinematic");
        enableBodyEdit = new VisCheckBox("Edit");

        add(setDynamicBox);
        add(setStaticBox);
        add(setKinematicBox);
        row();
        add(createRectangleButton).top();
        add(createJointButton).top();
        add(enableBodyEdit);
        add().expand().fill();

        setStaticBox.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                bodyType = BodyDef.BodyType.StaticBody;
                setDynamicBox.setChecked(false);
                setKinematicBox.setChecked(false);
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        setDynamicBox.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                bodyType = BodyDef.BodyType.DynamicBody;
                setStaticBox.setChecked(false);
                setKinematicBox.setChecked(false);
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        setKinematicBox.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                bodyType = BodyDef.BodyType.KinematicBody;
                setDynamicBox.setChecked(false);
                setStaticBox.setChecked(false);
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        enableBodyEdit.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                canBodyEdit = !enableBodyEdit.isChecked();
                return super.touchDown(event, x, y, pointer, button);
            }
        });



        createRectangleButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                canCreateBox = true;
            }
        });

        setPanelButtonsDisable(true);
        pack();
        setSize(getWidth(), getHeight());
        setPosition(1900, 200);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (canDraw) {
            Vector3 worldCoordinates = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            Vector3 vec = camera.unproject(worldCoordinates);
            x2 = vec.x;
            y2 = vec.y;
            drawBox();
        }
    }

    void setPanelButtonsDisable(Boolean status) {
        createRectangleButton.setDisabled(status);
        setKinematicBox.setDisabled(status);
        setDynamicBox.setDisabled(status);
        setStaticBox.setDisabled(status);
        createJointButton.setDisabled(status);
    }

    private void createBoxObject(float x, float y, float w, float h) {
        if (mapEditor.isObjectLayerSelected()) {
            Util.createBox2dObject(mapEditor.getWorld(), x, y, w, h, bodyType, mapEditor.layersPanel.getSelectedLayer(ObjectMapLayer.class).createBoxObject(x, y, w, h, bodyType), 0,null);
            canCreateBox = false;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.FORWARD_DEL && selectedBody != null &&  selectedObject != null) {
            mapEditor.layersPanel.getSelectedLayer(ObjectMapLayer.class).getObjects().remove(selectedObject.getName());
            mapEditor.getWorld().destroyBody(selectedBody);
        }
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
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT && canCreateBox() && mapEditor.isObjectLayerSelected()) {
            Vector3 worldCoordinates = new Vector3(screenX, screenY, 0);
            Vector3 vec = camera.unproject(worldCoordinates);
            canDraw = true;
            x1 = vec.x;
            y1 = vec.y;
        }
        if (button == Input.Buttons.LEFT) isLeftButtonPressed = true;
        if (button == Input.Buttons.RIGHT) isRightButtonPressed = true;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT && canCreateBox()) {
            canDraw = false;
            float x = x1;
            float y = y1;
            float width = x2 - x1;
            float height = y2 - y1;

            if (width < 0) {
                x = x - -width;
                width = -width;
            }
            if (height < 0 && width < 0) {
                x = x - -width;
                width = -width;
                y = y - -height;
                height = -height;
            }
            if (height < 0) {
                y = y - -height;
                height = -height;
            }

            createBoxObject(x, y, width, height);
        }
        if (button == Input.Buttons.LEFT) isLeftButtonPressed = false;
        if (button == Input.Buttons.RIGHT) isRightButtonPressed = false;
        if (button == Input.Buttons.RIGHT && canOpenEditWindow && canBodyEdit ){
            if(selectedObject != null){
                ObjectEditWindow objectEditWindow= new ObjectEditWindow(selectedBody,selectedObject);
                getStage().addActor(objectEditWindow);
            }
        }

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (selectedBody != null) {
            Vector3 worldCoordinates = new Vector3(screenX, screenY, 0);
            Vector3 vec = camera.unproject(worldCoordinates);
            Vector3 deltaWorldCoordinates = new Vector3(screenX - Gdx.input.getDeltaX(), screenY - Gdx.input.getDeltaY(), 0);
            Vector3 deltaVec = camera.unproject(deltaWorldCoordinates);
            float amountX, amountY;
            if (canDragRight && isLeftButtonPressed) {
                float width = vec.x - selectedObject.getX();
                if (width < 0.2f) width = 0.2f;
                selectedObject.setWidth(width);
            }
            if (canDragLeft && isLeftButtonPressed) {
                amountX = vec.x - deltaVec.x;
                if (selectedObject.getWidth() - amountX < 0.2f) amountX = 0;
                selectedObject.setX(selectedObject.getX() + amountX);
                selectedObject.setWidth(selectedObject.getWidth() - amountX);
            }
            if (canDragDown && isLeftButtonPressed) {
                float height = vec.y - selectedObject.getY();
                if (height < 0.2f) height = 0.2f;
                selectedObject.setHeight(height);
            }
            if (canDragUp && isLeftButtonPressed) {
                amountY = vec.y - deltaVec.y;
                if (selectedObject.getHeight() - amountY < 0.2f) amountY = 0;
                selectedObject.setY(selectedObject.getY() + amountY);
                selectedObject.setHeight(selectedObject.getHeight() - amountY);
            }
            if (canDragObject && isLeftButtonPressed) {
                amountX = vec.x - deltaVec.x;
                amountY = vec.y - deltaVec.y;
                selectedObject.setX(selectedObject.getX() + amountX);
                selectedObject.setY(selectedObject.getY() + amountY);
            }
            if (canRotateObject && isRightButtonPressed) {
                amountY = vec.y - deltaVec.y;
                selectedObject.setRotation(selectedObject.getRotation() + amountY * 7);
            }
            if (selectedBody != null) {
                float x = selectedObject.getX();
                float y = selectedObject.getY();
                float width = selectedObject.getWidth();
                float height = selectedObject.getHeight();
                ((PolygonShape) selectedBody.getFixtureList().first().getShape()).setAsBox(width / 2, height / 2);
                selectedBody.setTransform(width / 2 + x, height / 2 + y, selectedObject.getRotation() * MathUtils.degreesToRadians);
            }
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Vector3 worldCoordinates = new Vector3(screenX, screenY, 0);
        Vector3 vec = camera.unproject(worldCoordinates);

        if (mapEditor.isObjectLayerSelected()) {
            boolean isObjectSelected = false;

            for (MapObject mapObject : mapEditor.layersPanel.getSelectedLayer(ObjectMapLayer.class).getObjects()) {
                if (mapObject instanceof RectangleObject) {
                    RectangleObject obj = (RectangleObject) mapObject;
                    if (vec.x >= obj.getX() - obj.getWidth() && vec.x <= obj.getX() + obj.getWidth() && vec.y >= obj.getY() - obj.getHeight() && vec.y <= obj.getY() + obj.getHeight()) {

                        isObjectSelected = true;
                        obj.setHighlighted(true);
                        selectedObject = obj;
                        mapEditor.getWorld().getBodies(bodies);
                        for (Body body : bodies) {
                            if (body.getUserData().equals(obj.getName())) {
                                selectedBody = body;
                                float x = selectedObject.getX();
                                float y = selectedObject.getY();
                                float width = selectedObject.getWidth();
                                float height = selectedObject.getHeight();
                                if (Util.roundFloat(x + width, 1) == Util.roundFloat(vec.x, 1) && Util.isFloatInRange(vec.y, y, y + height)) {
                                    Util.setCursorSizeHorizontal();
                                    canDragRight = true;
                                } else {
                                    canDragRight = false;
                                }
                                if (Util.roundFloat(x, 1) == Util.roundFloat(vec.x, 1) && Util.isFloatInRange(vec.y, y, y + height)) {
                                    Util.setCursorSizeHorizontal();
                                    canDragLeft = true;
                                } else {
                                    canDragLeft = false;
                                }
                                if (Util.roundFloat(y, 1) == Util.roundFloat(vec.y, 1) && Util.isFloatInRange(vec.x, x, x + width)) {
                                    Util.setCursorSize();
                                    canDragUp = true;
                                } else {
                                    canDragUp = false;
                                }
                                if (Util.roundFloat(y + height, 1) == Util.roundFloat(vec.y, 1) && Util.isFloatInRange(vec.x, x, x + width)) {
                                    Util.setCursorSize();
                                    canDragDown = true;
                                } else {
                                    canDragDown = false;
                                }
                                if (Util.isFloatInRange(vec.x, x + 0.1f, x + width - 0.1f)
                                        && (Util.isFloatInRange(vec.y, y + 0.1f, y + height - 0.1f))) {
                                    Util.setCursorMove();
                                    canDragObject = true;
                                    canRotateObject = true;
                                    canOpenEditWindow = true;
                                } else {
                                    canDragObject = false;
                                    canRotateObject = false;
                                    canOpenEditWindow = false;
                                }
                                if (!canDragLeft && !canDragRight && !canDragDown && !canDragUp && !canDragObject) {
                                    Util.setCursorSystem();
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
            }

            if (!isObjectSelected) {
                for (MapObject mapObject : mapEditor.layersPanel.getSelectedLayer(ObjectMapLayer.class).getObjects()) {
                    if (mapObject instanceof RectangleObject) {
                        mapObject.setHighlighted(false);
                    }
                    selectedObject = null;
                    selectedBody = null;
                    Util.setCursorSystem();
                }
            }
        }
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    private boolean canCreateBox() {
        return canCreateBox;
    }

    private void drawBox() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(x1, y1, x2 - x1, y2 - y1);
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.2f);
        shapeRenderer.rect(x1, y1, x2 - x1, y2 - y1);
        shapeRenderer.end();
    }


}
