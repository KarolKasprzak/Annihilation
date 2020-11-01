package com.cosma.annihilation.Editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorObject.MapMaterialObject;
import com.cosma.annihilation.Screens.EditorScreen;
import com.cosma.annihilation.Utils.Enums.MaterialType;
import com.cosma.annihilation.Utils.Util;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

public class MaterialPanel extends VisWindow implements InputProcessor {

    private EditorScreen editorScreen;
    private VisSelectBox<MaterialType> selectMaterialBox;
    private float x1, y1, x2, y2;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private VisTextButton createMaterialButton;
    private boolean canDragRight, canDragLeft, canDragUp, canDragDown, canDragObject, canRotateObject, isObjectSelected = false,
            isLeftButtonPressed, isRightButtonPressed, canDraw = false, canCreateBox = false, canOpenEditWindow = false;
    private MapMaterialObject selectedMaterialObject;

    public MaterialPanel(final EditorScreen editorScreen) {
        super("Objects:");
        this.editorScreen = editorScreen;
        this.shapeRenderer = editorScreen.getShapeRenderer();
        this.camera = editorScreen.getCamera();



        createMaterialButton = new VisTextButton("add material");

        selectMaterialBox = new VisSelectBox();
        selectMaterialBox.setItems(MaterialType.values());

        add(selectMaterialBox).pad(10);
        add(createMaterialButton).pad(10);

        createMaterialButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                canCreateBox = true;
            }
        });


        pack();
        center();
        setMovable(true);
        setResizable(false);

        disableButtons();
    }

    public void disableButtons(){
        if(editorScreen.isMaterialLayerSelected()){
            selectMaterialBox.setDisabled(false);
            createMaterialButton.setDisabled(false);
        }else{
            selectMaterialBox.setDisabled(true);
            createMaterialButton.setDisabled(true);
        }
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

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.FORWARD_DEL && selectedMaterialObject != null) {
             editorScreen.getMap().getMapMaterialObjects().remove(selectedMaterialObject);
             selectedMaterialObject = null;
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
        if (button == Input.Buttons.LEFT && canCreateBox()) {
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

            if (editorScreen.isMaterialLayerSelected()) {
                canCreateBox = false;
                MapMaterialObject mapMaterialObject = new MapMaterialObject(x,y,width,height,selectMaterialBox.getSelected());
                editorScreen.getMap().getMapMaterialObjects().add(mapMaterialObject);
            }
        }
        if (button == Input.Buttons.LEFT) isLeftButtonPressed = false;
        if (button == Input.Buttons.RIGHT) isRightButtonPressed = false;
        if (button == Input.Buttons.RIGHT && canOpenEditWindow) {
//            if (selectedObject != null) {
//                ObjectEditWindow objectEditWindow = new ObjectEditWindow(selectedBody, selectedObject);
//                getStage().addActor(objectEditWindow);
//            }
        }

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (selectedMaterialObject != null) {
            Vector3 worldCoordinates = new Vector3(screenX, screenY, 0);
            Vector3 vec = camera.unproject(worldCoordinates);
            Vector3 deltaWorldCoordinates = new Vector3(screenX - Gdx.input.getDeltaX(), screenY - Gdx.input.getDeltaY(), 0);
            Vector3 deltaVec = camera.unproject(deltaWorldCoordinates);
            float amountX, amountY;
            if (canDragRight && isLeftButtonPressed) {
                float width = vec.x - selectedMaterialObject.getX();
                if (width < 0.2f) width = 0.2f;
                selectedMaterialObject.setWidth(width);
            }
            if (canDragLeft && isLeftButtonPressed) {
                amountX = vec.x - deltaVec.x;
                if (selectedMaterialObject.getWidth() - amountX < 0.2f) amountX = 0;
                selectedMaterialObject.setX(selectedMaterialObject.getX() + amountX);
                selectedMaterialObject.setWidth(selectedMaterialObject.getWidth() - amountX);
            }
            if (canDragDown && isLeftButtonPressed) {
                float height = vec.y - selectedMaterialObject.getY();
                if (height < 0.2f) height = 0.2f;
                selectedMaterialObject.setHeight(height);
            }
            if (canDragUp && isLeftButtonPressed) {
                amountY = vec.y - deltaVec.y;
                if (selectedMaterialObject.getHeight() - amountY < 0.2f) amountY = 0;
                selectedMaterialObject.setY(selectedMaterialObject.getY() + amountY);
                selectedMaterialObject.setHeight(selectedMaterialObject.getHeight() - amountY);
            }
            if (canDragObject && isLeftButtonPressed) {
                amountX = vec.x - deltaVec.x;
                amountY = vec.y - deltaVec.y;
                selectedMaterialObject.setX(selectedMaterialObject.getX() + amountX);
                selectedMaterialObject.setY(selectedMaterialObject.getY() + amountY);
            }
//            if (canRotateObject && isRightButtonPressed) {
//                amountY = vec.y - deltaVec.y;
//                selectedObject.setRotation(selectedObject.getRotation() + amountY * 7);
//            }
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Vector3 worldCoordinates = new Vector3(screenX, screenY, 0);
        Vector3 vec = camera.unproject(worldCoordinates);

        if (editorScreen.isMaterialLayerSelected()) {
            isObjectSelected = false;
            for (MapMaterialObject obj : editorScreen.getMap().getMapMaterialObjects()) {

                if (vec.x > obj.getX() && vec.x < obj.getX() + obj.getWidth() && vec.y > obj.getY() && vec.y < obj.getY() + obj.getHeight()) {

                    isObjectSelected = true;
                    obj.setHighlighted(true);
                    selectedMaterialObject = obj;

                    float x = obj.getX();
                    float y = obj.getY();
                    float width = obj.getWidth();
                    float height = obj.getHeight();
                    if (Util.roundFloat(x + width, 2) == Util.roundFloat(vec.x, 2) && Util.isFloatInRange(vec.y, y, y + height)) {
                        Util.setCursorSizeHorizontal();
                        canDragRight = true;
                    } else {
                        canDragRight = false;
                    }
                    if (Util.roundFloat(x, 2) == Util.roundFloat(vec.x, 2) && Util.isFloatInRange(vec.y, y, y + height)) {
                        Util.setCursorSizeHorizontal();
                        canDragLeft = true;
                    } else {
                        canDragLeft = false;
                    }
                    if (Util.roundFloat(y, 2) == Util.roundFloat(vec.y, 2) && Util.isFloatInRange(vec.x, x, x + width)) {
                        Util.setCursorSize();
                        canDragUp = true;
                    } else {
                        canDragUp = false;
                    }
                    if (Util.roundFloat(y + height, 2) == Util.roundFloat(vec.y, 2) && Util.isFloatInRange(vec.x, x, x + width)) {
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
        }
        if (!isObjectSelected) {
            for (MapMaterialObject mapObject : editorScreen.getMap().getMapMaterialObjects()) {
                mapObject.setHighlighted(false);
                selectedMaterialObject = null;
                Util.setCursorSystem();
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
