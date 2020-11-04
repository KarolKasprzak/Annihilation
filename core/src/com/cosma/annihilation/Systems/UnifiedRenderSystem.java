package com.cosma.annihilation.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
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
import com.cosma.annihilation.Utils.CustomSkeletonRender;
import com.cosma.annihilation.Utils.NormalMapShaderProvider;
import com.cosma.annihilation.Utils.RenderComparator;
import com.esotericsoftware.spine.SkeletonRendererDebug;

public class UnifiedRenderSystem extends SortedIteratingSystem {

    private ComponentMapper<TextureComponent> textureMapper;
    private ComponentMapper<SkeletonComponent> skeletonMapper;
    private ComponentMapper<PhysicsComponent> physicsMapper;
    private ComponentMapper<SpriteComponent> spriteMapper;

    private OrthographicCamera camera;
    private RayHandler rayHandler;
    private SpriteBatch batch;
    private PolygonSpriteBatch polygonBatch;
    private Vector2 positionTmp = new Vector2();
    private CustomSkeletonRender skeletonRenderer;
    private SkeletonRendererDebug debugRenderer;
    private NormalMapShaderProvider shaderData;
    private TextureRegion normalMapRegion;
    private FrameBuffer normalMapFBO;
    private FrameBuffer diffuseMapFBO;
    private ExtendViewport viewport;
    private Vector3 screenCoordinate;

    public UnifiedRenderSystem(SpriteBatch batch, OrthographicCamera camera, World world, PolygonSpriteBatch polygonBatch, RayHandler rayHandler, GameMap gameMap, ExtendViewport viewport) {
        super(Family.one(SkeletonComponent.class, TextureComponent.class).all(DrawOrderComponent.class).get(), new RenderComparator(), Constants.RENDER);
        this.camera = camera;
        this.rayHandler = rayHandler;
        this.batch = batch;
        this.polygonBatch = polygonBatch;
        this.viewport = viewport;
        textureMapper = ComponentMapper.getFor(TextureComponent.class);
        physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);
        skeletonMapper = ComponentMapper.getFor(SkeletonComponent.class);
        spriteMapper = ComponentMapper.getFor(SpriteComponent.class);

        skeletonRenderer = new CustomSkeletonRender();

        skeletonRenderer.setPremultipliedAlpha(false);

        normalMapFBO = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        normalMapRegion = new TextureRegion();

        debugRenderer = new SkeletonRendererDebug();

        debugRenderer.setBoundingBoxes(true);
        debugRenderer.setRegionAttachments(false);
        debugRenderer.setScale(0.01f);
        screenCoordinate = new Vector3();

        shaderData = new NormalMapShaderProvider(camera, rayHandler, gameMap);
    }

    @Override
    public void update(float deltaTime) {
        GameMap gameMap = this.getEngine().getCurrentMap();

        batch.setProjectionMatrix(camera.combined);
        polygonBatch.setProjectionMatrix(camera.combined);

        normalMapFBO.begin();
        polygonBatch.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //render normals for background (map)
        for (Sprite sprite : gameMap.getSpriteMapLayer().getSpriteArray()) {
            positionTmp.set(sprite.getX(), sprite.getY());
            if (sprite instanceof AnimatedSprite) {
                ((AnimatedSprite) sprite).updateAnimation(deltaTime);
            }
            if (sprite.getNormalTexture() != null) {
                drawTextureRegion(sprite, positionTmp, true);
            }
        }

        //render normals for entities, textures & sprites
        firstPassUpdate(deltaTime);
        polygonBatch.end();
        normalMapFBO.end();

        //draw normal map on full screen
        Texture normalMap = normalMapFBO.getColorBufferTexture();
        normalMap.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        batch.begin();
        screenCoordinate.set(0, Gdx.graphics.getHeight(), 0);
        camera.unproject(screenCoordinate);
        positionTmp.set(0, Gdx.graphics.getHeight());
        viewport.unproject(positionTmp);
        batch.draw(normalMap, positionTmp.x, positionTmp.y, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight(), 1, 1,
                0, 0, 0, normalMap.getWidth(), normalMap.getHeight(), false, true);
        batch.end();

//        batch.end();
        //render diffuse for background (map)
//        super.update(deltaTime);


        //render first plan (map)


        //render Light


//        rayHandler.setCombinedMatrix(camera);
//        rayHandler.updateAndRender();
    }

    @Override
    public void firstPassProcessEntity(Entity entity, float deltaTime) {
        PhysicsComponent physicsComponent = physicsMapper.get(entity);
        //texture render
        if (textureMapper.has(entity) && physicsMapper.has(entity)) {
            TextureComponent textureComponent = textureMapper.get(entity);
            Vector2 position = physicsMapper.get(entity).body.getPosition();
            position.x = position.x - textureComponent.textureRegion.getRegionWidth() / Constants.PPM / 2;
            position.y = position.y - textureComponent.textureRegion.getRegionHeight() / Constants.PPM / 2;
            drawTexture(textureComponent, position, physicsComponent.body.getAngle() * MathUtils.radiansToDegrees, true);

        }
        //skeleton render
        if (skeletonMapper.has(entity)) {
            SkeletonComponent skeletonComponent = skeletonMapper.get(entity);
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
            skeletonRenderer.draw(polygonBatch, skeletonComponent.skeleton, true, skeletonComponent.normalTexture);
        }
        //sprite render
        if (spriteMapper.has(entity)) {
            SpriteComponent spriteComponent = spriteMapper.get(entity);
            if (spriteComponent.isLifeTimeLimited) {
                spriteComponent.time += deltaTime;
            }

            Vector2 position = positionTmp.set(spriteComponent.x, spriteComponent.y);
            TextureComponent textureComponent = textureMapper.get(entity);
            position.x = position.x - textureComponent.textureRegion.getRegionWidth() / Constants.PPM / 2;
            position.y = position.y - textureComponent.textureRegion.getRegionHeight() / Constants.PPM / 2;
            drawTexture(textureComponent, position, spriteComponent.angle, true);

            if (spriteComponent.isLifeTimeLimited) {
                if (spriteComponent.time >= spriteComponent.lifeTime) {
                    this.getEngine().removeEntity(entity);
                }
            }
        }

    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PhysicsComponent physicsComponent = physicsMapper.get(entity);
        //texture render
        if (textureMapper.has(entity) && physicsMapper.has(entity)) {
            TextureComponent textureComponent = textureMapper.get(entity);
            Vector2 position = physicsMapper.get(entity).body.getPosition();
            position.x = position.x - textureComponent.textureRegion.getRegionWidth() / Constants.PPM / 2;
            position.y = position.y - textureComponent.textureRegion.getRegionHeight() / Constants.PPM / 2;
            drawTexture(textureComponent, position, physicsComponent.body.getAngle() * MathUtils.radiansToDegrees, false);

        }

        //skeleton render
        if (skeletonMapper.has(entity)) {

            SkeletonComponent skeletonComponent = skeletonMapper.get(entity);
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
            skeletonRenderer.draw(polygonBatch, skeletonComponent.skeleton, false, skeletonComponent.normalTexture);

//            debugRenderer.getShapeRenderer().setProjectionMatrix(camera.combined);
//            debugRenderer.draw(skeletonComponent.skeleton);
        }


        //Texture render

        //Runtime sprite render

    }

    private void assignNormalRegion(Texture normalTexture, TextureRegion textureRegion) {
        normalMapRegion.setTexture(normalTexture);
        normalMapRegion.setRegion(textureRegion.getRegionX(), textureRegion.getRegionY(), textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
    }

    private void drawTextureRegion(Sprite sprite, Vector2 position, boolean drawNormal) {
        TextureRegion region;
        if (drawNormal) {
            assignNormalRegion(sprite.getNormalTexture(), sprite.getTextureRegion());
            region = normalMapRegion;
        } else {
            region = sprite.getTextureRegion();
        }
        polygonBatch.draw(region, positionTmp.x + (sprite.isFlipX() ? sprite.getTextureRegion().getRegionWidth() / Constants.PPM : 0), positionTmp.y, (float) sprite.getTextureRegion().getRegionWidth() / Constants.PPM, (float) sprite.getTextureRegion().getRegionHeight() / Constants.PPM,
                sprite.getTextureRegion().getRegionWidth() / Constants.PPM * (sprite.isFlipX() ? -1 : 1), sprite.getTextureRegion().getRegionHeight() / Constants.PPM,
                1, 1, sprite.getAngle());
    }

    private void drawTexture(TextureComponent textureComponent, Vector2 position, float angle, boolean drawNormal) {

        TextureRegion region;
        if (drawNormal) {
            assignNormalRegion(textureComponent.normalTexture, textureComponent.textureRegion);
            region = normalMapRegion;
        } else {
            region = textureComponent.textureRegion;
        }
        polygonBatch.draw(region, position.x + (textureComponent.flipTexture ? textureComponent.textureRegion.getRegionWidth() / Constants.PPM : 0), position.y, (float) textureComponent.textureRegion.getRegionWidth() / 2, (float) textureComponent.textureRegion.getRegionHeight() / 2,
                textureComponent.textureRegion.getRegionWidth() / Constants.PPM * (textureComponent.flipTexture ? -1 : 1), textureComponent.textureRegion.getRegionHeight() / Constants.PPM,
                1, 1, angle);
    }
}
