package com.cosma.annihilation.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.cosma.annihilation.Box2dLight.Light;
import com.cosma.annihilation.Box2dLight.PointLight;
import com.cosma.annihilation.Box2dLight.RayHandler;
import com.cosma.annihilation.Components.*;
import com.cosma.annihilation.Editor.CosmaMap.AnimatedSprite;
import com.cosma.annihilation.Editor.CosmaMap.GameMap;
import com.cosma.annihilation.Editor.CosmaMap.Sprite;
import com.cosma.annihilation.EntityEngine.core.ComponentMapper;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.EntityEngine.core.Family;
import com.cosma.annihilation.EntityEngine.systems.SortedIteratingSystem;
import com.cosma.annihilation.Utils.*;
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
    private ShaderProvider normalMapShader;
    private TextureRegion normalMapRegion;
    private FrameBuffer normalMapFBO;
    private FrameBuffer lightFBO;
    private FrameBuffer diffuseMapFBO;
    private FrameBuffer finalFBO;
    private Matrix4 matrix;
    private ShapeRenderer debugRenderer;
    private SkeletonRendererDebug skeletonRendererDebug;
    private ExtendViewport viewport;

    public UnifiedRenderSystem(SpriteBatch batch, OrthographicCamera camera, PolygonSpriteBatch polygonBatch, RayHandler rayHandler, GameMap gameMap, ExtendViewport viewport) {
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
        matrix = new Matrix4();
        debugRenderer = new ShapeRenderer();
        debugRenderer.setAutoShapeType(true);
        debugRenderer.setColor(1, 1, 1, 0.5f);
        skeletonRenderer = new CustomSkeletonRender();

        skeletonRenderer.setPremultipliedAlpha(false);
        normalMapFBO = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        diffuseMapFBO = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

        lightFBO = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        normalMapRegion = new TextureRegion();

        skeletonRendererDebug = new SkeletonRendererDebug();

        skeletonRendererDebug.setBoundingBoxes(true);
        skeletonRendererDebug.setRegionAttachments(true);
        skeletonRendererDebug.setScale(0.01f);

        normalMapShader = new ShaderProvider(camera, rayHandler, gameMap);

    }

    public void resize() {
        normalMapShader.setPixelPerUnit(viewport);

//        normalMapFBO = new FrameBuffer(Pixmap.Format.RGBA8888,w,h, false);
//        diffuseMapFBO = new FrameBuffer(Pixmap.Format.RGBA8888, w, h, false);
    }

    @Override
    public void update(float deltaTime) {
        GameMap gameMap = this.getEngine().getCurrentMap();


        batch.setProjectionMatrix(camera.combined);
        debugRenderer.setProjectionMatrix(camera.combined);
        polygonBatch.setProjectionMatrix(camera.combined);

        //normal

        normalMapFBO.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        polygonBatch.begin();

        //render normals for background (map)
        for (Sprite sprite : gameMap.getSpriteMapLayer().getSpriteArray()) {
            positionTmp.set(sprite.getX(), sprite.getY());
            if (sprite instanceof AnimatedSprite) {
                ((AnimatedSprite) sprite).updateAnimation(deltaTime);
            }
            if (sprite.getNormalTexture() != null) {
                drawSprite(sprite, true);
            }
        }
        super.firstPassUpdate(deltaTime);
        polygonBatch.flush();
        polygonBatch.end();
        normalMapFBO.end();


        //diffuse
        diffuseMapFBO.begin();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        polygonBatch.begin();
        for (Sprite sprite : gameMap.getSpriteMapLayer().getSpriteArray()) {
            positionTmp.set(sprite.getX(), sprite.getY());
            if (sprite.getNormalTexture() != null) {
                drawSprite(sprite, false);
            }
        }
        super.update(deltaTime);

        polygonBatch.end();
        diffuseMapFBO.end();

        //lights
        lightFBO.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


        polygonBatch.begin();

        for (Light light : rayHandler.getLightList()) {
            if (light instanceof PointLight) {
                TextureRegion region = ((PointLight) light).getLightMap();
                polygonBatch.draw(region, light.getX() - (region.getRegionWidth() / 2 / Constants.PPM), light.getY() - (region.getRegionHeight() / 2 / Constants.PPM), (region.getRegionWidth() / 2) / Constants.PPM, (region.getRegionHeight() / 2) / Constants.PPM,
                        region.getRegionWidth() / Constants.PPM, region.getRegionHeight() / Constants.PPM,
                        1, 1, 0);
            }
        }
        polygonBatch.end();
        lightFBO.end();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


        //final draw
        Texture diffuseTexture = diffuseMapFBO.getColorBufferTexture();
        Texture lightMap = lightFBO.getColorBufferTexture();
        Texture normalTexture = normalMapFBO.getColorBufferTexture();
        lightMap.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        diffuseTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        normalTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        batch.setProjectionMatrix(matrix);
        batch.setShader(normalMapShader.getRenderShader());
        batch.begin();
        normalMapShader.prepareDataForRenderShader();

        lightMap.bind(2);
        normalTexture.bind(1);
        diffuseTexture.bind(0);

        batch.draw(diffuseTexture, -1, 1, 2, -2);
        batch.end();
        batch.setShader(null);

//        batch.setProjectionMatrix(camera.combined);
//        batch.begin();
//        batch.draw(diffuseTexture, 1, 1, 10, 10);
//        batch.end();


//        rayHandler.update();
//        rayHandler.setCombinedMatrix(camera);
//        rayHandler.updateAndRender();

        polygonBatch.begin();
        super.afterLightUpdate(deltaTime);
        polygonBatch.end();


        if (Gdx.input.isKeyPressed(Input.Keys.F12)) {
            System.out.println("Screenshot");
            byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), true);

            for (int i = 4; i < pixels.length; i += 4) {
                pixels[i - 1] = (byte) 255;
            }

            Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
            BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
            PixmapIO.writePNG(Gdx.files.local("screenshot.png"), pixmap);
            pixmap.dispose();
        }

//        SkeletonComponent skeletonComponent = getEngine().getPlayerEntity().getComponent(SkeletonComponent.class);
//        skeletonRendererDebug.getShapeRenderer().setProjectionMatrix(camera.combined);
//        skeletonRendererDebug.draw(skeletonComponent.skeleton);


//        debugRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        for(Entity entity1: getEngine().getEntitiesFor(Family.all(SpriteComponent.class).get())){
//            SpriteComponent spriteComponent = entity1.getComponent(SpriteComponent.class);
//            debugRenderer.rect(spriteComponent.x,spriteComponent.y,spriteComponent.rectangle.width,spriteComponent.rectangle.height);
//        }
//        debugRenderer.end();

    }


    @Override
    public void anotherProcessEntity(Entity entity, float deltaTime) {
        if (textureMapper.has(entity) && physicsMapper.has(entity)) {
            TextureComponent textureComponent = textureMapper.get(entity);
            if (textureComponent.renderAfterLight) {
                PhysicsComponent physicsComponent = physicsMapper.get(entity);
                Vector2 position = physicsComponent.body.getPosition();
                position.x = position.x - textureComponent.textureRegion.getRegionWidth() / Constants.PPM / 2;
                position.y = position.y - textureComponent.textureRegion.getRegionHeight() / Constants.PPM / 2;
                drawTexture(textureComponent, position, physicsComponent.body.getAngle() * MathUtils.radiansToDegrees, false);
            }
        }
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
            if (!entity.hasComponent(PlayerComponent.class)) {
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

            if (skeletonComponent.skeletonDirection) {
                skeletonRenderer.draw(polygonBatch, skeletonComponent.skeleton, true, skeletonComponent.normalTexture);
            } else {
                polygonBatch.setShader(normalMapShader.getFlipShader());
                skeletonRenderer.draw(polygonBatch, skeletonComponent.skeleton, true, skeletonComponent.normalTexture);
                polygonBatch.setShader(null);
            }
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
            skeletonRenderer.draw(polygonBatch, skeletonComponent.skeleton, false, skeletonComponent.normalTexture);
        }
        //Runtime sprite render
        if (spriteMapper.has(entity)) {
            SpriteComponent spriteComponent = spriteMapper.get(entity);
            if (spriteComponent.drawDiffuse) {
                Vector2 position = positionTmp.set(spriteComponent.x, spriteComponent.y);
                TextureComponent textureComponent = textureMapper.get(entity);
                position.x = position.x - textureComponent.textureRegion.getRegionWidth() / Constants.PPM / 2;
                position.y = position.y - textureComponent.textureRegion.getRegionHeight() / Constants.PPM / 2;
                drawTexture(textureComponent, position, spriteComponent.angle, false);
            }
        }
    }

    private void assignNormalRegion(Texture normalTexture, TextureRegion textureRegion) {
        normalMapRegion.setTexture(normalTexture);
        normalMapRegion.setRegion(textureRegion.getRegionX(), textureRegion.getRegionY(), textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
    }

    private void drawSprite(Sprite sprite, boolean drawNormal) {
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
        polygonBatch.draw(region, position.x + (textureComponent.flipTexture ? textureComponent.textureRegion.getRegionWidth() / Constants.PPM : 0), position.y, (textureComponent.textureRegion.getRegionWidth() / 2) / Constants.PPM, (textureComponent.textureRegion.getRegionHeight() / 2) / Constants.PPM,
                textureComponent.textureRegion.getRegionWidth() / Constants.PPM * (textureComponent.flipTexture ? -1 : 1), textureComponent.textureRegion.getRegionHeight() / Constants.PPM,
                1, 1, angle);
    }
}
