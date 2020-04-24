package com.cosma.annihilation.Systems;


import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.cosma.annihilation.Components.BodyComponent;
import com.cosma.annihilation.Components.ParticleComponent;
import com.cosma.annihilation.Components.PlayerComponent;
import com.cosma.annihilation.Components.TextureComponent;
import com.cosma.annihilation.Utils.Constants;
import com.cosma.annihilation.Utils.RenderComparator;

public class ParticleRenderSystem extends IteratingSystem {

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private World world;
    private BitmapFont font;
    private ComponentMapper<ParticleComponent> particleMapper;

    public ParticleRenderSystem(World world, SpriteBatch batch) {
        super(Family.all(ParticleComponent.class).get(), Constants.Particle_RENDER);
        this.batch = batch;
        this.world = world;

        particleMapper = ComponentMapper.getFor(ParticleComponent.class);
    }


    @Override
    public void processEntity(Entity entity, float deltaTime) {
        ParticleComponent particleComponent = particleMapper.get(entity);
        batch.begin();
        particleComponent.particleEffect.draw(batch,deltaTime);
        batch.end();



    }
}