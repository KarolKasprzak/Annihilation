package com.cosma.annihilation.Systems;


import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.cosma.annihilation.Ai.PlayerTasks.PlayerGoToPosition;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Box2dLight.LightOld;
import com.cosma.annihilation.Components.*;
import com.cosma.annihilation.Editor.CosmaMap.CosmaLights.Light;
import com.cosma.annihilation.EntityEngine.core.ComponentMapper;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.EntityEngine.core.Family;
import com.cosma.annihilation.EntityEngine.signals.Listener;
import com.cosma.annihilation.EntityEngine.signals.Signal;
import com.cosma.annihilation.EntityEngine.systems.IteratingSystem;
import com.cosma.annihilation.EntityEngine.utils.ImmutableArray;
import com.cosma.annihilation.Utils.Constants;
import com.cosma.annihilation.Utils.Enums.GameEvent;


public class ActionSystem extends IteratingSystem implements Listener<GameEvent> {
    private ComponentMapper<PlayerComponent> stateMapper;
    private ComponentMapper<PhysicsComponent> bodyMapper;
    private PlayerComponent playerComponent;
    private PhysicsComponent physicsComponent;
    private OrthographicCamera camera;
    private SpriteBatch batch;
//    Filter filter;
//    Filter filter1;

    public ActionSystem(OrthographicCamera camera, SpriteBatch batch) {
        super(Family.all(PlayerComponent.class).get(), Constants.ACTION_SYSTEM);
        stateMapper = ComponentMapper.getFor(PlayerComponent.class);
        bodyMapper = ComponentMapper.getFor(PhysicsComponent.class);
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
        physicsComponent = bodyMapper.get(entity);
        if (playerComponent.collisionEntityArray.size > 0) {
            playerComponent.processedEntity = playerComponent.collisionEntityArray.first();
            ActionComponent actionComponent = playerComponent.processedEntity.getComponent(ActionComponent.class);


            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            Texture icon;
            Body entityBody = playerComponent.processedEntity.getComponent(PhysicsComponent.class).body;

            switch (actionComponent.action) {
                case TALK:
                    icon = Annihilation.getAssets().get("gfx/textures/talk_icon.png", Texture.class);
                    batch.draw(icon, entityBody.getPosition().x, entityBody.getPosition().y + 0.8f, icon.getWidth() / Constants.PPM, icon.getHeight() / Constants.PPM);
                    break;
                case CLIMB_UP:
                    icon = Annihilation.getAssets().get("gfx/textures/action_icon_up.png", Texture.class);
                    break;
                default:
                    icon = Annihilation.getAssets().get("gfx/textures/action_icon_down.png", Texture.class);
                    break;
            }
            float width = icon.getWidth() / Constants.PPM;
            float height = icon.getHeight() / Constants.PPM;
            float x = entityBody.getPosition().x + actionComponent.offsetX - width / 2;
            float y = entityBody.getPosition().y + actionComponent.offsetY - height / 2;
            batch.draw(icon, x, y, width, height);
            batch.end();
        } else {
            playerComponent.processedEntity = null;
        }

    }

    @Override
    public void receive(Signal<GameEvent> signal, GameEvent event) {
        if (playerComponent != null && event.equals(GameEvent.PERFORM_ACTION)) {
            if (playerComponent.processedEntity != null && playerComponent.isWeaponHidden && playerComponent.canPerformAction) {
                ActionComponent actionComponent = playerComponent.processedEntity.getComponent(ActionComponent.class);

                switch (actionComponent.action) {
                    case CLIMB_UP:
                        climbUp();
                        break;
                    case OPEN_DOOR:
//                            doorAction();
                        break;
                    case OPEN_GATE:
                        openGate();
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
                    case OPEN_NOTE:
                        openNoteWindow();
                        break;
                    case SWITCH_LIGHT:
                        Light light = getEngine().getCurrentMap().getLights().get(actionComponent.actionTargetName);
                        if (light.isActive()) {
                            light.setActive(false);
                        } else {
                            light.setActive(true);
                        }
                        break;
                }
            }
        }
    }

    private void openGate() {
        ImmutableArray<Entity> entities = getEngine().getEntitiesFor(Family.all(GateComponent.class).get());
        for (Entity entity : entities) {
            GateComponent gateComponent = entity.getComponent(GateComponent.class);
            if (gateComponent.gateName.equals(playerComponent.processedEntity.getComponent(ActionComponent.class).actionTargetName) && !gateComponent.isMoving) {
                Vector2 position = entity.getComponent(PhysicsComponent.class).body.getPosition();
                if(gateComponent.isOpen){
                    gateComponent.targetPosition.set(position.x,position.y-gateComponent.moveDistance);
                }else{
                    gateComponent.targetPosition.set(position.x,position.y+gateComponent.moveDistance);
                }
                gateComponent.isMoving = true;
            }
        }
    }

    private void climbUp() {
        Entity playerEntity = getEngine().getPlayerEntity();
        PlayerComponent playerComponent = playerEntity.getComponent(PlayerComponent.class);
        playerComponent.climbing = true;
        playerComponent.canMoveOnSide = false;
        Body body = playerEntity.getComponent(PhysicsComponent.class).body;
        body.getFixtureList().get(0).setSensor(true);
        playerComponent.climbingTargetPosition = playerComponent.processedEntity.getComponent(ActionComponent.class).actionTargetPosition;
        playerComponent.climbingStartPosition = playerComponent.processedEntity.getComponent(PhysicsComponent.class).body.getPosition();
        playerComponent.activeTask = new PlayerGoToPosition(playerComponent.climbingStartPosition);
    }

    private void goToAnotherMap() {
        playerComponent.mapName = playerComponent.processedEntity.getComponent(MapChangeComponent.class).targetMapPath;
//        worldBuilder.goToMap();
//        playerComponent.getComponent(PhysicsComponent.class).body.setTransform(gateEntity.getComponent(MapChangeComponent.class).playerPositionOnTargetMap,0);
    }

    private void openLootWindow() {
        if (playerComponent.processedEntity.getComponent(ContainerComponent.class).itemList.size > 0) {
            getEngine().getSystem(UserInterfaceSystem.class).openLootMenu();
        }
    }

    private void openNoteWindow() {
        getEngine().getSystem(UserInterfaceSystem.class).openNoteMenu();
    }

    private void startDialogAction() {
        {
//          d
        }
    }

//    private void doorAction() {
//        if (playerComponent.processedEntity.getComponent(PhysicsComponent.class).body.getFixtureList().get(0).isSensor()) {
//            playerComponent.processedEntity.getComponent(PhysicsComponent.class).body.getFixtureList().get(0).setSensor(false);
//            playerComponent.processedEntity.getComponent(PhysicsComponent.class).body.getFixtureList().get(0).setFilterData(filter1);
//            playerComponent.processedEntity.getComponent(PhysicsComponent.class).body.getFixtureList().get(0).refilter();
//            playerComponent.processedEntity.getComponent(DoorComponent.class).isOpen = false;
//
//        } else {
//            playerComponent.processedEntity.getComponent(PhysicsComponent.class).body.getFixtureList().get(0).setSensor(true);
//            playerComponent.processedEntity.getComponent(PhysicsComponent.class).body.getFixtureList().get(0).setFilterData(filter);
//            playerComponent.processedEntity.getComponent(DoorComponent.class).isOpen = true;
//
//        }
//    }
//
//    public void loadDoor(Entity entity){
//        entity.getComponent(PhysicsComponent.class).body.getFixtureList().get(0).setSensor(true);
//        entity.getComponent(PhysicsComponent.class).body.getFixtureList().get(0).setFilterData(filter);
//    }
}
