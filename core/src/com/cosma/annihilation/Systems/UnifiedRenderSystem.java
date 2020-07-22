package com.cosma.annihilation.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.cosma.annihilation.Box2dLight.RayHandler;
import com.cosma.annihilation.Components.*;
import com.cosma.annihilation.Editor.CosmaMap.AnimatedSprite;
import com.cosma.annihilation.Editor.CosmaMap.GameMap;
import com.cosma.annihilation.Editor.CosmaMap.Sprite;
import com.cosma.annihilation.EntityEngine.core.ComponentMapper;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.EntityEngine.core.Family;
import com.cosma.annihilation.EntityEngine.systems.SortedIteratingSystem;
import com.cosma.annihilation.Utils.Constants;
import com.cosma.annihilation.Utils.RenderComparator;
import com.esotericsoftware.spine.SkeletonRenderer;

public class UnifiedRenderSystem extends SortedIteratingSystem {

    private ComponentMapper<TextureComponent> textureMapper;
    private ComponentMapper<SkeletonComponent> skeletonMapper;
    private ComponentMapper<PhysicsComponent> physicsMapper;

    private ShaderProgram shader;
    private OrthographicCamera camera;
    private RayHandler rayHandler;
    private SpriteBatch batch;
    private PolygonSpriteBatch polygonBatch;
    private Vector2 positionTmp = new Vector2();
    private SkeletonRenderer skeletonRenderer;

    public UnifiedRenderSystem(SpriteBatch batch, OrthographicCamera camera, World world, PolygonSpriteBatch polygonBatch, RayHandler rayHandler) {
        super(Family.one(SkeletonComponent.class, TextureComponent.class).all(PhysicsComponent.class, DrawOrderComponent.class).get(), new RenderComparator(), Constants.RENDER);
        this.camera = camera;
        this.rayHandler = rayHandler;
        this.batch = batch;
        this.polygonBatch = polygonBatch;
        textureMapper = ComponentMapper.getFor(TextureComponent.class);
        physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);
        skeletonMapper = ComponentMapper.getFor(SkeletonComponent.class);

        skeletonRenderer = new SkeletonRenderer();
        skeletonRenderer.setPremultipliedAlpha(false);

        //create shader
        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(Gdx.files.internal("shaders/normalMap/ver.glsl").readString(), Gdx.files.internal("shaders/normalMap/frag.glsl").readString());
        if (!shader.isCompiled())
            throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
        shader.begin();
        shader.setUniformi("u_texture", 0);
        shader.setUniformi("u_normals", 1);
        shader.end();

    }


    @Override
    public void update(float deltaTime) {
        GameMap gameMap = this.getEngine().getCurrentMap();

        batch.setProjectionMatrix(camera.combined);
        polygonBatch.setProjectionMatrix(camera.combined);
        batch.setShader(shader);
        polygonBatch.setShader(shader);
        //render background (map)

                for (Sprite sprite : gameMap.getSpriteMapLayer().getSpriteArray()) {
                    positionTmp.set(sprite.getX(), sprite.getY());
                    if (sprite instanceof AnimatedSprite) {
                        ((AnimatedSprite) sprite).updateAnimation(deltaTime);
                    }
                    sprite.bindNormalTexture(1);
                    sprite.getTextureRegion().getTexture().bind(0);

                    batch.begin();
                    this.getEngine().prepareDataForNormalShaderRender(shader, false, false);
                    batch.draw(sprite.getTextureRegion(), positionTmp.x + (sprite.isFlipX() ? sprite.getTextureRegion().getRegionWidth() / Constants.PPM : 0), positionTmp.y, (float) sprite.getTextureRegion().getRegionWidth() / Constants.PPM, (float) sprite.getTextureRegion().getRegionHeight() / Constants.PPM,
                            sprite.getTextureRegion().getRegionWidth() / Constants.PPM * (sprite.isFlipX() ? -1 : 1), sprite.getTextureRegion().getRegionHeight() / Constants.PPM,
                            1, 1, sprite.getAngle());
                    batch.end();
                }


        super.update(deltaTime);

        //render first plan (map)


        //render Light
        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        //skeleton render
        if (skeletonMapper.has(entity)) {
            SkeletonComponent skeletonComponent = skeletonMapper.get(entity);
            PhysicsComponent physicsComponent = physicsMapper.get(entity);

            skeletonComponent.normalTexture.bind(1);
            skeletonComponent.diffuseTexture.bind(0);


            if (entity.getComponent(PlayerComponent.class) == null) {
                skeletonComponent.animationState.apply(skeletonComponent.skeleton);
            }

            if (!skeletonComponent.skeletonDirection) {
                skeletonComponent.skeleton.setFlipX(true);
            } else {
                skeletonComponent.skeleton.setFlipX(false);
            }
            skeletonComponent.skeleton.setPosition(physicsComponent.body.getPosition().x, physicsComponent.body.getPosition().y - (physicsComponent.height / 2));
            skeletonComponent.skeleton.updateWorldTransform();
            skeletonComponent.animationState.update(deltaTime);

            polygonBatch.begin();
            getEngine().prepareDataForNormalShaderRender(shader,!skeletonComponent.skeletonDirection,false);
            skeletonRenderer.draw(polygonBatch, skeletonComponent.skeleton);
            polygonBatch.end();
        }

        //sprite render
        if (textureMapper.has(entity)) {

        }
    }


}
