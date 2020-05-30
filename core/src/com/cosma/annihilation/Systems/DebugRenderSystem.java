package com.cosma.annihilation.Systems;



import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.EntityEngine.core.Family;
import com.cosma.annihilation.EntityEngine.systems.IteratingSystem;
import com.cosma.annihilation.Utils.Constants;
import com.cosma.annihilation.Utils.StateManager;


public class DebugRenderSystem extends IteratingSystem {

    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;
    private World world;

    public DebugRenderSystem(OrthographicCamera camera, World world) {
        super(Family.all().get(),Constants.DEBUG_RENDER);

        this.camera = camera;
        this.world = world;
        debugRenderer = new Box2DDebugRenderer();
    }

    @Override
    public void update(float deltaTime) {
        if(StateManager.debugMode) {
            debugRenderer.render(world, camera.combined);
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }
}