package com.cosma.annihilation.Systems;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.utils.Timer;
import com.cosma.annihilation.Components.*;
import com.cosma.annihilation.Entities.EntityFactory;
import com.cosma.annihilation.EntityEngine.core.ComponentMapper;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.EntityEngine.core.Family;
import com.cosma.annihilation.EntityEngine.signals.Listener;
import com.cosma.annihilation.EntityEngine.signals.Signal;
import com.cosma.annihilation.EntityEngine.systems.IteratingSystem;
import com.cosma.annihilation.Utils.CollisionID;
import com.cosma.annihilation.Utils.Constants;
import com.cosma.annihilation.Utils.EntityEventSignal;
import com.cosma.annihilation.Utils.Enums.GameEvent;


public class HealthSystem extends IteratingSystem implements Listener<GameEvent> {


    private ComponentMapper<HealthComponent> healthMapper;
    private ComponentMapper<BodyComponent> bodyMapper;
    private ComponentMapper<AiComponent> aiComponentMapper;
    private ComponentMapper<SkeletonComponent> skeletonMapper;
    private OrthographicCamera camera;


    public HealthSystem(OrthographicCamera camera) {
        super(Family.all(HealthComponent.class, BodyComponent.class, AiComponent.class, SkeletonComponent.class).get(), Constants.HEALTH_SYSTEM);
        this.camera = camera;

        healthMapper = ComponentMapper.getFor(HealthComponent.class);
        bodyMapper = ComponentMapper.getFor(BodyComponent.class);
        aiComponentMapper = ComponentMapper.getFor(AiComponent.class);
        skeletonMapper = ComponentMapper.getFor(SkeletonComponent.class);
    }


    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        HealthComponent healthComponent = healthMapper.get(entity);
        BodyComponent bodyComponent = bodyMapper.get(entity);
        AiComponent aiComponent = aiComponentMapper.get(entity);
        SkeletonComponent skeletonComponent = skeletonMapper.get(entity);

        if (healthComponent.hp <= 0) {
            skeletonComponent.dead();
            skeletonComponent.animationState.addEmptyAnimation(5, 0.2f, 0.1f);
            entity.remove(AiComponent.class);
            Filter filter = new Filter();
            filter.categoryBits = CollisionID.SCENERY_BACKGROUND_OBJECT;
            filter.maskBits = CollisionID.MASK_SCENERY_BACKGROUND_OBJECT;
            bodyComponent.body.getFixtureList().first().setFilterData(filter);
            aiComponent.isPaused = true;
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    entity.remove(AiComponent.class);
                }
            }, 4);
        }

        setDamageTexture(healthComponent, skeletonComponent);

        if (healthComponent.isHit && healthComponent.hp > 0) {
            Vector2 attackerPosition = healthComponent.attackerPosition;
            Vector2 position = bodyComponent.body.getPosition();
            if (attackerPosition.x > position.x) {
                this.getEngine().addEntity(EntityFactory.getInstance().createBloodSplashEntity(bodyComponent.body.getPosition().x - 1, bodyComponent.body.getPosition().y + MathUtils.random(-0.2f, 0.3f), MathUtils.random(0, 90)));

            } else {
                this.getEngine().addEntity(EntityFactory.getInstance().createBloodSplashEntity(bodyComponent.body.getPosition().x + 1, bodyComponent.body.getPosition().y + MathUtils.random(-0.2f, 0.3f), MathUtils.random(0, 90)));
            }
            healthComponent.isHit = false;
            aiComponent.isPaused = true;
            skeletonComponent.setSkeletonAnimation(false, "hit", 6, false);

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    aiComponent.isPaused = false;
                }
            }, skeletonComponent.animationState.getCurrent(6).getAnimation().getDuration());
            skeletonComponent.animationState.addEmptyAnimation(6, 0.1f, skeletonComponent.animationState.getCurrent(6).getAnimation().getDuration());
        }

    }

    public void setDamageTexture(HealthComponent healthComponent, SkeletonComponent skeletonComponent) {
        float health = healthComponent.hp * 100 / healthComponent.maxHP ;
                if (health >= 90) {
                    return;
                }
                if (health <= 75) {
                    skeletonComponent.skeleton.setAttachment("body", "body_scratched");
                    if (health <= 45) {
                skeletonComponent.skeleton.setAttachment("body", "body_wounded");
            }
        }
    }

    @Override
    public void receive(Signal<GameEvent> signal, GameEvent entityEvent) {

    }

    private void calculateAccuracy(EntityEventSignal entityEvent) {
        if (entityEvent.getAccuracy()) {
            displayMessage(entityEvent, "miss");

        } else {
            entityEvent.getEntity().getComponent(HealthComponent.class).hp -= entityEvent.getDamage();
            displayMessage(entityEvent, Integer.toString(entityEvent.getDamage()) + " dmg");

        }
    }

    private void displayMessage(EntityEventSignal entityEvent, String message) {
//        Vector3 worldCoordinates = new Vector3(entityEvent.getEntity().getComponent(TransformComponent.class).position.x, entityEvent.getEntity().getComponent(TransformComponent.class).position.y, 0);
//        Vector3 cameraCoordinates = camera.project(worldCoordinates);
//        TextActor floatingText = new TextActor(message, TimeUnit.SECONDS.toMillis(1));
//        floatingText.setPosition(cameraCoordinates.x, cameraCoordinates.y+100);
//        floatingText.setDeltaY(200);
//        gui.getStage().addActor(floatingText);
//        floatingText.animate();
    }


}
