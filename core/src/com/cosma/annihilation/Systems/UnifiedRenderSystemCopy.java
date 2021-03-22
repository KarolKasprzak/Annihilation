package com.cosma.annihilation.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
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
import com.cosma.annihilation.Utils.ShaderProvider;
import com.cosma.annihilation.Utils.RenderComparator;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.SkeletonRendererDebug;

public class UnifiedRenderSystemCopy extends SortedIteratingSystem {

    private ComponentMapper<TextureComponent> textureMapper;
    private ComponentMapper<SkeletonComponent> skeletonMapper;
    private ComponentMapper<PhysicsComponent> physicsMapper;
    private ComponentMapper<SpriteComponent> spriteMapper;

    private OrthographicCamera camera;
    private RayHandler rayHandler;
    private SpriteBatch batch;
    private PolygonSpriteBatch polygonBatch;
    private Vector2 positionTmp = new Vector2();
    private SkeletonRenderer skeletonRenderer;
    private SkeletonRendererDebug debugRenderer;
    private ShaderProvider shaderData;
    private FrameBuffer normalMapFBO;
    private FrameBuffer diffuseMapFBO;

    public UnifiedRenderSystemCopy(SpriteBatch batch, OrthographicCamera camera, World world, PolygonSpriteBatch polygonBatch, RayHandler rayHandler, GameMap gameMap) {
        super(Family.one(SkeletonComponent.class, TextureComponent.class).all(DrawOrderComponent.class).get(), new RenderComparator(), Constants.RENDER);
        this.camera = camera;
        this.rayHandler = rayHandler;
        this.batch = batch;
        this.polygonBatch = polygonBatch;
        textureMapper = ComponentMapper.getFor(TextureComponent.class);
        physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);
        skeletonMapper = ComponentMapper.getFor(SkeletonComponent.class);
        spriteMapper = ComponentMapper.getFor(SpriteComponent.class);

        skeletonRenderer = new SkeletonRenderer();
        skeletonRenderer.setPremultipliedAlpha(false);

        normalMapFBO = new FrameBuffer(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

        debugRenderer = new SkeletonRendererDebug();
        debugRenderer.setBoundingBoxes(true);
        debugRenderer.setRegionAttachments(true);
        debugRenderer.setScale(0.01f);

        shaderData = new ShaderProvider(camera, gameMap);
    }


    @Override
    public void update(float deltaTime) {
        GameMap gameMap = this.getEngine().getCurrentMap();

        batch.setProjectionMatrix(camera.combined);
        polygonBatch.setProjectionMatrix(camera.combined);

        polygonBatch.setShader(shaderData.getRenderShader());
        //render background (map)

        polygonBatch.begin();
        shaderData.prepareDataForRenderShader();
        for (Sprite sprite : gameMap.getSpriteMapLayer().getSpriteArray()) {
            positionTmp.set(sprite.getX(), sprite.getY());
            if (sprite instanceof AnimatedSprite) {
                ((AnimatedSprite) sprite).updateAnimation(deltaTime);
            }
            sprite.bindNormalTexture(1);
            sprite.getTextureRegion().getTexture().bind(0);


            polygonBatch.draw(sprite.getTextureRegion(), positionTmp.x + (sprite.isFlipX() ? sprite.getTextureRegion().getRegionWidth() / Constants.PPM : 0), positionTmp.y, (float) sprite.getTextureRegion().getRegionWidth() / Constants.PPM, (float) sprite.getTextureRegion().getRegionHeight() / Constants.PPM,
                    sprite.getTextureRegion().getRegionWidth() / Constants.PPM * (sprite.isFlipX() ? -1 : 1), sprite.getTextureRegion().getRegionHeight() / Constants.PPM,
                    1, 1, sprite.getAngle());

        }
        polygonBatch.end();



        super.update(deltaTime);


        //render first plan (map)


        //render Light


        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PhysicsComponent physicsComponent = physicsMapper.get(entity);



        if (textureMapper.has(entity) && physicsMapper.has(entity)) {
            TextureComponent textureComponent = textureMapper.get(entity);
            polygonBatch.begin();
            shaderData.prepareDataForRenderShader();
            textureComponent.normalTexture.bind(1);
            textureComponent.textureRegion.getTexture().bind(0);
            Vector2 position = physicsMapper.get(entity).body.getPosition();
            position.x = position.x - textureComponent.textureRegion.getRegionWidth() / Constants.PPM / 2;
            position.y = position.y - textureComponent.textureRegion.getRegionHeight() / Constants.PPM / 2;
            drawTextureRegion(textureComponent, position, physicsComponent.body.getAngle() * MathUtils.radiansToDegrees);
            polygonBatch.end();
        }


        //skeleton render
        if (skeletonMapper.has(entity)) {
            polygonBatch.begin();
            SkeletonComponent skeletonComponent = skeletonMapper.get(entity);

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

//            shaderData.prepareDataForRenderShader(!skeletonComponent.skeletonDirection);
            skeletonRenderer.draw(polygonBatch, skeletonComponent.skeleton);
            polygonBatch.end();
//            debugRenderer.getShapeRenderer().setProjectionMatrix(camera.combined);
//            debugRenderer.draw(skeletonComponent.skeleton);
        }


        //Texture render

        //Runtime sprite render
        if (spriteMapper.has(entity)) {


            SpriteComponent spriteComponent = spriteMapper.get(entity);
            if(spriteComponent.isLifeTimeLimited){
                spriteComponent.time += deltaTime;
            }
            Vector2 position = positionTmp.set(spriteComponent.x, spriteComponent.y);
            TextureComponent textureComponent = textureMapper.get(entity);

            polygonBatch.begin();
//            shaderData.prepareDataForRenderShader(false);
            textureComponent.normalTexture.bind(1);

            textureComponent.textureRegion.getTexture().bind(0);

            position.x = position.x -  textureComponent.textureRegion.getRegionWidth() / Constants.PPM / 2;
            position.y = position.y -  textureComponent.textureRegion.getRegionHeight() / Constants.PPM / 2;
            drawTextureRegion(textureComponent, position, spriteComponent.angle);

            polygonBatch.end();
            if(spriteComponent.isLifeTimeLimited){
                if(spriteComponent.time >= spriteComponent.lifeTime){
                    this.getEngine().removeEntity(entity);
                }
            }
        }


    }

    private void drawTextureRegion(TextureComponent textureComponent, Vector2 position, float angle) {


        polygonBatch.draw(textureComponent.textureRegion, position.x + (textureComponent.flipTexture ? textureComponent.textureRegion.getRegionWidth() / Constants.PPM : 0), position.y, (float) textureComponent.textureRegion.getRegionWidth() / 2, (float) textureComponent.textureRegion.getRegionHeight() / 2,
                textureComponent.textureRegion.getRegionWidth() / Constants.PPM * (textureComponent.flipTexture ? -1 : 1), textureComponent.textureRegion.getRegionHeight() / Constants.PPM,
                1, 1, angle);

    }
}
