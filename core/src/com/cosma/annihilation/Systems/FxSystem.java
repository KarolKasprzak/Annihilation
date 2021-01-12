package com.cosma.annihilation.Systems;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Components.*;
import com.cosma.annihilation.EntityEngine.core.ComponentMapper;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.EntityEngine.core.Family;
import com.cosma.annihilation.EntityEngine.systems.IteratingSystem;
import com.cosma.annihilation.EntityEngine.utils.ImmutableArray;
import com.cosma.annihilation.Utils.Constants;

public class FxSystem extends IteratingSystem {

    private SpriteBatch batch;
    private World world;
    private ComponentMapper<ParticleComponent> particleMapper;
    private ComponentMapper<BulletComponent> bulletMapper;
    private ComponentMapper<PhysicsComponent> physicsMapper;
    private QueryCallback bulletCallback;


    public FxSystem(World world, SpriteBatch batch) {
        super(Family.one(ParticleComponent.class,BulletComponent.class).get(), Constants.PARTICLE_RENDER);
        this.batch = batch;
        this.world = world;
        particleMapper = ComponentMapper.getFor(ParticleComponent.class);
        bulletMapper = ComponentMapper.getFor(BulletComponent.class);
        physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);
        bulletCallback = new QueryCallback(){
            @Override
            public boolean reportFixture(Fixture fixture) {
                if(fixture.getBody().getUserData() instanceof Entity){
                    Entity entity = (Entity) fixture.getBody().getUserData();
                    if(entity.hasComponent(HealthComponent.class)){
                        // TODO: 07.01.2021
                        return false;
                    }
                }
                return true;
            }
        };
    }

    @Override
    public void update(float deltaTime) {

        super.update(deltaTime);
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        if(particleMapper.has(entity)){
            ParticleComponent particleComponent = particleMapper.get(entity);
            batch.begin();
            particleComponent.particleEffect.draw(batch,deltaTime);
            batch.end();
            if(particleComponent.particleEffect.isComplete()){
                getEngine().removeEntity(entity);
            }
        }
        //bullet system
        if(bulletMapper.has(entity)){
           BulletComponent bulletComponent = bulletMapper.get(entity);
           Body body = physicsMapper.get(entity).body;
            if(body.getPosition().dst(bulletComponent.targetX,bulletComponent.targetY) < 0.25){
                spawnParticleEffect("gun_spark.p",bulletComponent.targetX,bulletComponent.targetY);
                spawnBulletHole(bulletComponent.targetX,bulletComponent.targetY);
                if(bulletComponent.isBulletHit){
                    // TODO: 07.01.2021
                    world.QueryAABB(bulletCallback,10,0,0,0);
                }
                this.getEngine().removeEntity(entity);
            }
        }
    }

    void spawnBulletHole(float x, float y) {
        if (this.getEngine().isPointInDrawField(x, y)) {
            Entity entity = new Entity();
            TextureComponent textureComponent = new TextureComponent();
            textureComponent.textureRegion = Annihilation.getTextureRegion("fx_textures", "bullet_hole");
            textureComponent.normalTexture = Annihilation.getAssets().get("gfx/atlas/fx_textures_n.png", Texture.class);
            entity.add(textureComponent);

            DrawOrderComponent drawOrderComponent = new DrawOrderComponent();
            drawOrderComponent.drawOrder = 3;
            entity.add(drawOrderComponent);

            SpriteComponent spriteComponent = new SpriteComponent();
            spriteComponent.x = x;
            spriteComponent.y = y;
            spriteComponent.createRectangle(textureComponent);
            spriteComponent.drawDiffuse = false;
            entity.add(spriteComponent);

            ImmutableArray<Entity> entities = this.getEngine().getEntitiesFor(Family.all(SpriteComponent.class).get());
            if (entities.size() < 1) {
                this.getEngine().addEntity(entity);
            } else {
                boolean overlaps = false;
                for (Entity spriteEntity : entities) {
                    if (spriteComponent.rectangle.overlaps(spriteEntity.getComponent(SpriteComponent.class).rectangle)) {
                        overlaps = true;
                        break;
                    }
                }
                if (!overlaps) {
                    this.getEngine().addEntity(entity);
                }
            }
        }
    }

    public void spawnParticleEffect(String name,float x, float y){
        Entity entity = getEngine().createEntity();
        ParticleComponent particleComponent = new ParticleComponent();
        particleComponent.loadDate(name);
        particleComponent.particleEffect.setPosition(x,y);
        particleComponent.particleEffect.start();
        entity.add(particleComponent);
        getEngine().addEntity(entity);
    }
}