package com.cosma.annihilation.Editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.cosma.annihilation.Box2dLight.*;
import com.cosma.annihilation.Editor.CosmaMap.AmbientLightsWindow;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorLights.MapConeLight;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorLights.MapLight;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorLights.MapPointLight;
import com.cosma.annihilation.Screens.EditorScreen;
import com.cosma.annihilation.Utils.CollisionID;
import com.cosma.annihilation.Utils.Util;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;

public class LightsPanel extends VisWindow implements InputProcessor {

    private EditorScreen editorScreen;
    private VisTextButton createLightButton;
    /**
     * 0 = point , 1 = cone
     **/
    private int selectedLightType = 0;
    private VisCheckBox setPointLight, setConeLight;
    private OrthographicCamera camera;
    private RayHandler rayHandler;
    private ColorPicker picker;
    private Color selectedColor;
    private MapLight selectedLight;
    private Light selectedBox2dLight;
    private Filter filter;
    private boolean canDragObject, isLeftButtonPressed, canCreateLight = false;
    private AmbientLightsWindow ambientLightsWindow;


    public LightsPanel(final EditorScreen editorScreen, RayHandler rayHandler) {
        super("Lights:");
        this.rayHandler = rayHandler;
        this.editorScreen = editorScreen;
        this.camera = editorScreen.getCamera();

        ambientLightsWindow = new AmbientLightsWindow(editorScreen.getMap(),editorScreen.getRayHandler());


        Drawable white = VisUI.getSkin().getDrawable("white");
        final Image image = new Image(white);
        picker = new ColorPicker("color picker", new ColorPickerAdapter() {
            @Override
            public void finished(Color newColor) {
                selectedColor = newColor;
                image.setColor(newColor);
            }
        });

        filter = new Filter();
        filter.categoryBits = CollisionID.LIGHT;
        filter.maskBits = CollisionID.MASK_LIGHT;

        TableUtils.setSpacingDefaults(this);
        columnDefaults(0).left();

        createLightButton = new VisTextButton("create light");
        setPointLight = new VisCheckBox("point", true);
        setPointLight.setFocusBorderEnabled(false);

        setConeLight = new VisCheckBox("cone");
        setConeLight.setFocusBorderEnabled(false);

        setPointLight.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                selectedLightType = 0;
                setConeLight.setChecked(false);
                return super.touchDown(event, x, y, pointer, button);
            }
        });


        setConeLight.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                selectedLightType = 1;
                setPointLight.setChecked(false);
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        createLightButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                canCreateLight = true;
            }
        });

        image.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                getStage().addActor(picker.fadeIn());
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        picker.setColor(Color.WHITE);
        image.setColor(Color.WHITE);
        selectedColor = Color.WHITE;

        VisTextButton ambientButton = new VisTextButton("ambient");

        ambientButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                    getStage().addActor(ambientLightsWindow);
            }
        });

        row();
        add(setPointLight).left().top();
        add(setConeLight).left().top();
        row();
        add(createLightButton).center().top();
        add(ambientButton).center().top();
        add(image).top().size(Gdx.graphics.getHeight() * 0.017f).center().top().expandX().expandY();
        setPanelButtonsDisable(true);
        pack();
        setMovable(false);
        setResizable(false);
    }

    void setPanelButtonsDisable(Boolean status) {
        createLightButton.setDisabled(status);
        setConeLight.setDisabled(status);
        setPointLight.setDisabled(status);
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
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if (button == Input.Buttons.LEFT) isLeftButtonPressed = true;
        if (button == Input.Buttons.RIGHT && selectedLight != null) {
            final int delete = 1;
            final int options = 2;
            final int clone = 3;
            final int cancel = 4;
            Dialogs.showConfirmDialog(getStage(),selectedLight.getName(), "what do you want?",
                    new String[]{"delete", "options","clone","cancel"}, new Integer[]{delete, options,clone,cancel},
                    result -> {
                        if (result == delete) {
                            editorScreen.getMap().getLightsMapLayer().getLights().remove(selectedLight.getName());
                            editorScreen.getMap().findLight(selectedLight.getName()).remove(true);
                            selectedBox2dLight = null;
                            selectedLight = null;
                        }

                        if (result == clone) {
                           if(selectedLight instanceof MapConeLight){
                               String lightName = editorScreen.getMap().getLightsMapLayer().createConeLight(selectedLight.getX()+2, selectedLight.getY(), selectedLight.getColor(),
                                       25, selectedLight.getLightDistance(), ((MapConeLight) selectedLight).getDirection(), ((MapConeLight) selectedLight).getConeDegree());
                               ConeLight light = new ConeLight(rayHandler, 25, selectedBox2dLight.getColor(), selectedBox2dLight.getDistance(), selectedBox2dLight.getX()+2, selectedBox2dLight.getY(),
                                       ((MapConeLight) selectedLight).getDirection() , ((MapConeLight) selectedLight).getConeDegree());
                               light.setSoftnessLength(selectedLight.getSoftLength());
                               light.setContactFilter(filter);
                               editorScreen.getMap().putLight(lightName, light);
                           }
                            if(selectedLight instanceof MapPointLight){
                                String lightName = editorScreen.getMap().getLightsMapLayer().createPointLight(selectedLight.getX()+2, selectedLight.getY(), selectedLight.getColor()
                                        , 25, selectedLight.getLightDistance());
                                PointLight light = new PointLight(rayHandler, 25, selectedLight.getColor(), selectedBox2dLight.getDistance(), selectedBox2dLight.getX()+2, selectedBox2dLight.getY());
                                light.setContactFilter(filter);
                                editorScreen.getMap().putLight(lightName, light);
                            }
                        }

                        if (result == options){
                            LightsAdvWindow lightsAdvWindow = new LightsAdvWindow(selectedLight,selectedBox2dLight);
                            lightsAdvWindow.setPosition(Gdx.input.getX(),Gdx.input.getY());
                            getStage().addActor(lightsAdvWindow);
                        }



                    }).setPosition(Gdx.input.getX(),Gdx.input.getY());

        }

        if (button == Input.Buttons.LEFT && canCreateLight && editorScreen.isLightsEditModeSelected()) {
            Vector3 worldCoordinates = new Vector3(screenX, screenY, 0);
            Vector3 vec = camera.unproject(worldCoordinates);
            if (selectedLightType == 0) {
                editorScreen.getMap().getLightsMapLayer().createPointLight(vec.x, vec.y, selectedColor, 25, 5);
                PointLight light = new PointLight(rayHandler, 25, selectedColor, 5, vec.x, vec.y);
                light.setContactFilter(filter);
                editorScreen.getMap().putLight(editorScreen.getMap().getLightsMapLayer().getLastLightName(), light);
            }
            if (selectedLightType == 1) {
                editorScreen.getMap().getLightsMapLayer().createConeLight(vec.x, vec.y, selectedColor, 25, 5, 270, 45);
                ConeLight light = new ConeLight(rayHandler, 25, selectedColor, 5, vec.x, vec.y, 270, 45);
                light.setContactFilter(filter);
                editorScreen.getMap().putLight(editorScreen.getMap().getLightsMapLayer().getLastLightName(), light);
            }
            canCreateLight = false;

        }
        return false;
    }



    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) isLeftButtonPressed = false;
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer){
        Vector3 worldCoordinates = new Vector3(screenX, screenY, 0);
        Vector3 vec = camera.unproject(worldCoordinates);
        Vector3 deltaWorldCoordinates = new Vector3(screenX - Gdx.input.getDeltaX(), screenY - Gdx.input.getDeltaY(), 0);
        Vector3 deltaVec = camera.unproject(deltaWorldCoordinates);
        float amountX, amountY;

        if (canDragObject && selectedBox2dLight != null && isLeftButtonPressed) {
            amountX = vec.x - deltaVec.x;
            amountY = vec.y - deltaVec.y;
            selectedLight.setX(selectedLight.getX() + amountX);
            selectedLight.setY(selectedLight.getY() + amountY);
            selectedBox2dLight.setPosition(selectedBox2dLight.getX()+amountX,selectedBox2dLight.getY()+amountY);
        }

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (editorScreen.isLightsEditModeSelected()) {
            Vector3 worldCoordinates = new Vector3(screenX, screenY, 0);
            Vector3 vec = camera.unproject(worldCoordinates);
            MapLight mapLightFound = null;
            boolean lightFound = false;
            for (MapLight mapLight : editorScreen.getMap().getLightsMapLayer().getLights()) {
                float x = mapLight.getX();
                float y = mapLight.getY();

                if (Util.isFloatInRange(vec.x, x - 1, x + 1) && Util.isFloatInRange(vec.y, y - 1, y + 1)) {
                    lightFound = true;
                    mapLightFound = mapLight;
                }

            }

            if(lightFound){
                selectedLight = mapLightFound;
                selectedBox2dLight = editorScreen.getMap().findLight(mapLightFound.getName());
                mapLightFound.setHighlighted(true);
                canDragObject = true;
            }else {
                if(selectedLight != null){
                    selectedLight.setHighlighted(false);}
                selectedLight = null;
                selectedBox2dLight = null;
                canDragObject = false;
            }
        }
        return false;
}

    @Override
    public boolean scrolled(int amount) {

        return false;
    }
}
