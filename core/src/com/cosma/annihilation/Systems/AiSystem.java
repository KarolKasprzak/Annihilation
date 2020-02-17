package com.cosma.annihilation.Systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.cosma.annihilation.Components.*;
import com.cosma.annihilation.Utils.Constants;


public class AiSystem extends IteratingSystem {
    private World world;
    private ComponentMapper<AiComponent> aiMapper;
    private ComponentMapper<AnimationComponent> animationMapper;
    private ComponentMapper<BodyComponent> bodyMapper;
    private ComponentMapper<HealthComponent> healthMapper;
    private BitmapFont font;
    private SpriteBatch batch;
    private Camera camera;
    private OrthographicCamera worldCamera;

    public AiSystem(World world, SpriteBatch batch, OrthographicCamera camera) {
        super(Family.all(AiComponent.class).get(), Constants.AI_SYSTEM);
        this.world = world;
        this.batch = batch;
        this.worldCamera = camera;
        aiMapper = ComponentMapper.getFor(AiComponent.class);
        animationMapper = ComponentMapper.getFor(AnimationComponent.class);
        bodyMapper = ComponentMapper.getFor(BodyComponent.class);
        healthMapper = ComponentMapper.getFor(HealthComponent.class);
        font = new BitmapFont();
        font.setColor(Color.RED);
        font.getData().setScale(1, 1);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        camera = this.getEngine().getSystem(UserInterfaceSystem.class).getStage().getCamera();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AiComponent aiComponent = aiMapper.get(entity);
        AnimationComponent animationComponent = animationMapper.get(entity);
        HealthComponent healthComponent = healthMapper.get(entity);
        BodyComponent bodyComponent = bodyMapper.get(entity);
        if(!healthComponent.isDead){
            aiComponent.ai.update(entity);
        }else bodyComponent.body.setLinearVelocity(new Vector2(0, bodyComponent.body.getLinearVelocity().y));

        Vector3 worldPosition = worldCamera.project(new Vector3(bodyComponent.body.getPosition().x,bodyComponent.body.getPosition().y+1,0));
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.draw(batch, aiComponent.ai.getStatus(), worldPosition.x, worldPosition.y);
        batch.end();


    }
}
