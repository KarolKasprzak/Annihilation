package com.cosma.annihilation.Systems;

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
import com.cosma.annihilation.Utils.Enums.EntityAction;
import com.cosma.annihilation.Utils.Enums.GameEvent;
import com.cosma.annihilation.Utils.Util;
import com.cosma.annihilation.World.WorldBuilder;

public class ActionSystem extends IteratingSystem implements Listener<GameEvent> {
    private ComponentMapper<PlayerComponent> stateMapper;
    private PlayerComponent playerComponent;


    private WorldBuilder worldBuilder;
    private OrthographicCamera camera;
    private SpriteBatch batch;

//    Filter filter;
//    Filter filter1;

    public ActionSystem(WorldBuilder worldBuilder, OrthographicCamera camera, SpriteBatch batch) {
        super(Family.all(PlayerComponent.class).get(), Constants.ACTION_SYSTEM);
        this.worldBuilder = worldBuilder;
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

        if (!playerComponent.collisionEntityList.isEmpty()) {
            playerComponent.processedEntity = playerComponent.collisionEntityList.listIterator().next();
            if (Util.hasComponent(playerComponent.processedEntity, TextureComponent.class)) {
                playerComponent.processedEntity.getComponent(TextureComponent.class).renderWithShader = true;
            }
            ActionComponent actionComponent = playerComponent.processedEntity.getComponent(ActionComponent.class);
            batch.setProjectionMatrix(camera.combined);
            batch.begin();

            Texture texture;
            Body entityBody = playerComponent.processedEntity.getComponent(BodyComponent.class).body;

            switch (actionComponent.action) {
                case TALK:
                    texture = Annihilation.getAssets().get("gfx/textures/talk_icon.png", Texture.class);
                    batch.draw(texture, entityBody.getPosition().x, entityBody.getPosition().y + 0.8f, texture.getWidth()/Constants.PPM, texture.getHeight()/Constants.PPM);
                    break;
                case OPEN:
                    texture = Annihilation.getAssets().get("gfx/textures/search_icon.png", Texture.class);
                    batch.draw(texture, entityBody.getPosition().x-0.5f, entityBody.getPosition().y-0.5f , texture.getWidth()/Constants.PPM, texture.getHeight()/Constants.PPM);
                    break;

            }
            batch.end();

        } else
            playerComponent.processedEntity = null;
    }

    @Override
    public void receive(Signal<GameEvent> signal, GameEvent event) {
        if (playerComponent != null) {
            switch (event) {
                case PERFORM_ACTION:
                    if (playerComponent.processedEntity != null && playerComponent.isWeaponHidden && playerComponent.canPerformAction) {
                        EntityAction action = playerComponent.processedEntity.getComponent(ActionComponent.class).action;
                        switch (action) {
                            case OPEN_DOOR:
//                            doorAction();
                                break;
                            case OPEN:
                                openBoxAction();
                                break;
                            case GO_TO:
                                goToAnotherMap();
                                break;
                            case TALK:
                                startDialogAction();
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
        worldBuilder.goToMap();
//        playerComponent.getComponent(BodyComponent.class).body.setTransform(gateEntity.getComponent(GateComponent.class).playerPositionOnTargetMap,0);
    }

    private void openBoxAction() {
        if (playerComponent.processedEntity.getComponent(ContainerComponent.class).itemLocations.size > -1) {
            getEngine().getSystem(UserInterfaceSystem.class).showLootWindow(playerComponent.processedEntity);
        }
    }

    private void startDialogAction() {
        {
            getEngine().getSystem(UserInterfaceSystem.class).showDialogWindow(playerComponent.processedEntity);
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
