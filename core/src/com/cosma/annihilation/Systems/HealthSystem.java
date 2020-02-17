package com.cosma.annihilation.Systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.cosma.annihilation.Components.*;
import com.cosma.annihilation.Entities.EntityFactory;
import com.cosma.annihilation.Utils.Animation.AnimationStates;
import com.cosma.annihilation.Utils.Constants;
import com.cosma.annihilation.Utils.EntityEventSignal;
import com.cosma.annihilation.Utils.Enums.GameEvent;
import com.cosma.annihilation.Utils.Util;

public class HealthSystem extends IteratingSystem implements Listener<GameEvent> {


    private ComponentMapper<HealthComponent> healthMapper;
    private  OrthographicCamera camera;
    private ComponentMapper<BodyComponent> bodyMapper;

    public HealthSystem(OrthographicCamera camera) {
        super(Family.all(HealthComponent.class,BodyComponent.class).get(), Constants.HEALTH_SYSTEM);
        this.camera = camera;

        healthMapper = ComponentMapper.getFor(HealthComponent.class);
        bodyMapper = ComponentMapper.getFor(BodyComponent.class);
    }


    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        HealthComponent healthComponent = healthMapper.get(entity);
        BodyComponent bodyComponent = bodyMapper.get(entity);

        if (healthComponent.hp <= 0) {
            if(Util.hasComponent(entity,AnimationComponent.class)){
                healthComponent.isDead = true;
                AnimationComponent animationComponent = entity.getComponent(AnimationComponent.class);
                animationComponent.animationState = AnimationStates.DEATH_STAND;
                entity.getComponent(BodyComponent.class).body.setActive(false);
            }


//            getEngine().getSystem(CollisionSystem.class).bodiesToRemove.add(entity.getComponent(BodyComponent.class).body);
//            this.getEngine().removeEntity(entity);
        }
        if(healthComponent.isHit){
            Vector2 attackerPosition = healthComponent.attackerPosition;
            Vector2 position = bodyComponent.body.getPosition();
            if(attackerPosition.x > position.x){
                this.getEngine().addEntity(EntityFactory.getInstance().createBloodSplashEntity(bodyComponent.body.getPosition().x-1,bodyComponent.body.getPosition().y+MathUtils.random(-0.2f,0.3f),MathUtils.random(0,90)));

            }else{
                this.getEngine().addEntity(EntityFactory.getInstance().createBloodSplashEntity(bodyComponent.body.getPosition().x+1,bodyComponent.body.getPosition().y+MathUtils.random(-0.2f,0.3f),MathUtils.random(0,90)));
            }

            healthComponent.isHit = false;
        }


    }

    @Override
    public void receive(Signal<GameEvent> signal, GameEvent entityEvent) {

    }

    private void calculateAccuracy(EntityEventSignal entityEvent){
        if(entityEvent.getAccuracy()){
            displayMessage(entityEvent,"miss");

        }else{
            entityEvent.getEntity().getComponent(HealthComponent.class).hp -= entityEvent.getDamage();
            displayMessage(entityEvent,Integer.toString(entityEvent.getDamage()) + " dmg");

        }
    }

    private void displayMessage(EntityEventSignal entityEvent, String message){
//        Vector3 worldCoordinates = new Vector3(entityEvent.getEntity().getComponent(TransformComponent.class).position.x, entityEvent.getEntity().getComponent(TransformComponent.class).position.y, 0);
//        Vector3 cameraCoordinates = camera.project(worldCoordinates);
//        TextActor floatingText = new TextActor(message, TimeUnit.SECONDS.toMillis(1));
//        floatingText.setPosition(cameraCoordinates.x, cameraCoordinates.y+100);
//        floatingText.setDeltaY(200);
//        gui.getStage().addActor(floatingText);
//        floatingText.animate();
    }


}
