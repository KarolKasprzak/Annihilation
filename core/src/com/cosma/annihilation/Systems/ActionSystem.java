package com.cosma.annihilation.Systems;

import box2dLight.Light;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Components.*;
import com.cosma.annihilation.Utils.Constants;
import com.cosma.annihilation.Utils.EntityEngine;
import com.cosma.annihilation.Utils.Enums.EntityAction;
import com.cosma.annihilation.Utils.Enums.GameEvent;

import javax.jnlp.ClipboardService;

public class ActionSystem extends IteratingSystem implements Listener<GameEvent> {
    private ComponentMapper<PlayerComponent> stateMapper;
    private PlayerComponent playerComponent;
    private OrthographicCamera camera;
    private SpriteBatch batch;
//    Filter filter;
//    Filter filter1;

    public ActionSystem(OrthographicCamera camera, SpriteBatch batch) {
        super(Family.all(PlayerComponent.class).get(), Constants.ACTION_SYSTEM);
        stateMapper = ComponentMapper.getFor(PlayerComponent.class);
        this.batch = batch;
        this.camera = camera;
//        filter = new Filter();
//        filter.categoryBits = CollisionID.NO_SHADOW;
//
//        filter1 = new Filter();
//        filter1.categoryBits = CollisionID.CAST_SHADOW;

    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        playerComponent = stateMapper.get(entity);
        if (playerComponent.collisionEntityArray.size > 0) {
            playerComponent.processedEntity = playerComponent.collisionEntityArray.first();
            ActionComponent actionComponent = playerComponent.processedEntity.getComponent(ActionComponent.class);


            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            Texture icon;
            Body entityBody = playerComponent.processedEntity.getComponent(BodyComponent.class).body;

            switch (actionComponent.action) {
                case TALK:
                    icon = Annihilation.getAssets().get("gfx/textures/talk_icon.png", Texture.class);
                    batch.draw(icon, entityBody.getPosition().x, entityBody.getPosition().y + 0.8f, icon.getWidth() / Constants.PPM, icon.getHeight() / Constants.PPM);
                    break;
                case OPEN_CRATE:
                case SWITCH_LIGHT:
                    icon = Annihilation.getAssets().get("gfx/textures/action_icon.png", Texture.class);
                    float width = icon.getWidth() / Constants.PPM;
                    float height = icon.getHeight() / Constants.PPM;
                    float x = entityBody.getPosition().x + actionComponent.offsetX - width / 2;
                    float y = entityBody.getPosition().y + actionComponent.offsetY - height / 2;
                    batch.draw(icon, x, y, width, height);
                    break;
            }
            batch.end();
        } else {
            playerComponent.processedEntity = null;
        }

    }

    @Override
    public void receive(Signal<GameEvent> signal, GameEvent event) {
        if (playerComponent != null) {
            switch (event) {
                case PERFORM_ACTION:
                    if (playerComponent.processedEntity != null && playerComponent.isWeaponHidden && playerComponent.canPerformAction) {
                        ActionComponent actionComponent = playerComponent.processedEntity.getComponent(ActionComponent.class);

                        switch (actionComponent.action) {
                            case OPEN_DOOR:
//                            doorAction();
                                break;
                            case OPEN_CRATE:
                                openLootWindow();
                                break;
                            case GO_TO:
                                goToAnotherMap();
                                break;
                            case TALK:
                                startDialogAction();
                                break;
                            case SWITCH_LIGHT:
                                Light light = ((EntityEngine) this.getEngine()).getCurrentMap().findLight(actionComponent.actionTargetName);
                                if (light.isActive()) {
                                    light.setActive(false);
                                } else {
                                    light.setActive(true);
                                }
                                break;
                        }
                    }
                    break;
                case CROUCH:
                    break;
            }
        }
    }

    private void goToAnotherMap() {
        playerComponent.mapName = playerComponent.processedEntity.getComponent(GateComponent.class).targetMapPath;
//        worldBuilder.goToMap();
//        playerComponent.getComponent(BodyComponent.class).body.setTransform(gateEntity.getComponent(GateComponent.class).playerPositionOnTargetMap,0);
    }

    private void openLootWindow() {
        if (playerComponent.processedEntity.getComponent(ContainerComponent.class).itemList.size > 0) {
            getEngine().getSystem(UserInterfaceSystem.class).openPlayerMenu(true);
        }
    }

    private void startDialogAction() {
        {
//          d
        }
    }

//    private void doorAction() {
//        if (playerComponent.processedEntity.getComponent(BodyComponent.class).body.getFixtureList().get(0).isSensor()) {
//            playerComponent.processedEntity.getComponent(BodyComponent.class).body.getFixtureList().get(0).setSensor(false);
//            playerComponent.processedEntity.getComponent(BodyComponent.class).body.getFixtureList().get(0).setFilterData(filter1);
//            playerComponent.processedEntity.getComponent(BodyComponent.class).body.getFixtureList().get(0).refilter();
//            playerComponent.processedEntity.getComponent(DoorComponent.class).isOpen = false;
//
//        } else {
//            playerComponent.processedEntity.getComponent(BodyComponent.class).body.getFixtureList().get(0).setSensor(true);
//            playerComponent.processedEntity.getComponent(BodyComponent.class).body.getFixtureList().get(0).setFilterData(filter);
//            playerComponent.processedEntity.getComponent(DoorComponent.class).isOpen = true;
//
//        }
//    }
//
//    public void loadDoor(Entity entity){
//        entity.getComponent(BodyComponent.class).body.getFixtureList().get(0).setSensor(true);
//        entity.getComponent(BodyComponent.class).body.getFixtureList().get(0).setFilterData(filter);
//    }
}
