package com.cosma.annihilation.Systems;

import box2dLight.RayHandler;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.cosma.annihilation.Components.BodyComponent;
import com.cosma.annihilation.Components.SkeletonComponent;
import com.cosma.annihilation.Utils.Constants;


public class LightRenderSystem extends IteratingSystem {


    private OrthographicCamera camera;
    private RayHandler rayHandler;


    public LightRenderSystem(OrthographicCamera camera, RayHandler rayHandler) {
        super(Family.all(SkeletonComponent.class, BodyComponent.class).get(), Constants.LIGHT_RENDER);
        this.rayHandler = rayHandler;
        this.camera = camera;

    }

    @Override
    public void update(float deltaTime) {
        rayHandler.getLightMapTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender();
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
    }
}
