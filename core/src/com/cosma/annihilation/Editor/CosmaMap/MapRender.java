package com.cosma.annihilation.Editor.CosmaMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Box2dLight.RayHandler;
import com.cosma.annihilation.Components.ActionComponent;
import com.cosma.annihilation.Components.ParallaxComponent;
import com.cosma.annihilation.Components.PhysicsComponent;
import com.cosma.annihilation.Components.SkeletonComponent;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorLights.MapConeLight;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorLights.MapLight;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorLights.MapPointLight;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorObject.MapMaterialObject;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorObject.RectangleObject;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.Utils.Constants;
import com.cosma.annihilation.Utils.ShaderProvider;
import com.esotericsoftware.spine.SkeletonRenderer;


public class MapRender {

    private ShapeRenderer renderer;
    private int scale;
    private GameMap gameMap;
    private SpriteBatch batch;
    private TextureAtlas iconPack;
    private Vector2 position = new Vector2();
    private ShaderProvider shaderData;
    private PolygonSpriteBatch polygonSpriteBatch;
    private SkeletonRenderer skeletonRenderer;
    private OrthographicCamera camera;

    public MapRender(ShapeRenderer renderer, GameMap gameMap, SpriteBatch batch, RayHandler rayHandler, OrthographicCamera camera) {
        this.camera = camera;
        this.batch = batch;
        this.gameMap = gameMap;
        this.scale = gameMap.getTileSize();
        this.renderer = renderer;
        polygonSpriteBatch = new PolygonSpriteBatch();
        skeletonRenderer = new SkeletonRenderer();
        iconPack = Annihilation.getAssets().get("gfx/atlas/editor_icon.atlas", TextureAtlas.class);

        shaderData = new ShaderProvider(camera,rayHandler,gameMap);
    }

    public void renderGrid() {
        renderer.begin();
        for (int x = 0; x < gameMap.getWidth(); x++) {
            for (int y = 0; y < gameMap.getHeight(); y++) {
                renderer.setColor(Color.BLACK);
                renderer.rect(0, 0, gameMap.getWidth(), gameMap.getHeight());
                renderer.setColor(0, 0, 0, 0.2f);
                renderer.line(x, y, x + gameMap.getTileSize() / scale, y);
                renderer.line(x, y, x, y + gameMap.getTileSize() / scale);
            }
        }
        renderer.end();
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public void renderMap(float delta, boolean debugRender, boolean renderMaterialObject) {


        //render sprite
        batch.setShader(shaderData.getRenderShader());


        if (gameMap.getSpriteMapLayer().isLayerVisible()) {
            gameMap.getSpriteMapLayer().getSpriteArray().sort();
            for (Sprite sprite : gameMap.getSpriteMapLayer().getSpriteArray()) {

                if (sprite instanceof AnimatedSprite) {
                    ((AnimatedSprite) sprite).updateAnimation(delta);
                }

                batch.begin();
                if (sprite.getTextureRegion() != null) {
                    shaderData.prepareData(false);
                    sprite.bindNormalTexture(1);
                    sprite.getTextureRegion().getTexture().bind(0);
                    position.set(sprite.getX(), sprite.getY());
                    batch.draw(sprite.getTextureRegion(), position.x + (sprite.isFlipX() ? sprite.getTextureRegion().getRegionWidth() / Constants.PPM : 0), position.y, (float) sprite.getTextureRegion().getRegionWidth() / Constants.PPM / 2, (float) sprite.getTextureRegion().getRegionHeight() / Constants.PPM / 2,
                            sprite.getTextureRegion().getRegionWidth() / Constants.PPM * (sprite.isFlipX() ? -1 : 1), sprite.getTextureRegion().getRegionHeight() / Constants.PPM,
                            1, 1, sprite.getAngle());
                    if(sprite.isHighlighted()){
                        batch.draw(iconPack.findRegion("color"), position.x + (sprite.isFlipX() ? sprite.getTextureRegion().getRegionWidth() / Constants.PPM : 0), position.y, (float) sprite.getTextureRegion().getRegionWidth() / Constants.PPM / 2, (float) sprite.getTextureRegion().getRegionHeight() / Constants.PPM / 2,
                                sprite.getTextureRegion().getRegionWidth() / Constants.PPM * (sprite.isFlipX() ? -1 : 1), sprite.getTextureRegion().getRegionHeight() / Constants.PPM,
                                1, 1, sprite.getAngle());
                    }
                }
                batch.end();
            }
        }
        batch.setShader(null);

        batch.begin();

        //render entity
        renderer.begin();
        renderer.set(ShapeRenderer.ShapeType.Line);
        for (Entity entity : gameMap.getEntityArrayList()) {
            if (entity.hasComponent(ParallaxComponent.class)){
                Body body = entity.getComponent(PhysicsComponent.class).body;
                ParallaxComponent parallaxComponent = entity.getComponent(ParallaxComponent.class);
                renderer.rect(body.getPosition().x - parallaxComponent.displayW/2, body.getPosition().y - parallaxComponent.displayH/2,parallaxComponent.displayW,parallaxComponent.displayH);
            }
            if (entity.getComponent(ActionComponent.class) != null) {
                if (entity.getComponent(ActionComponent.class).actionTargetPosition != null) {
                    ActionComponent actionComponent = entity.getComponent(ActionComponent.class);
                    Texture texture = Annihilation.getAssets().get("gfx/interface/target.png", Texture.class);
                    batch.draw(texture, actionComponent.actionTargetPosition.x - (texture.getWidth() / 2 / Constants.PPM), actionComponent.actionTargetPosition.y - (texture.getHeight() / 2 / Constants.PPM),
                            texture.getHeight() / Constants.PPM, texture.getWidth() / Constants.PPM);
                }
            }
        }
        renderer.end();




        //render lights
        if (gameMap.getLightsMapLayer().isLayerVisible() && debugRender) {
            for (MapLight light : gameMap.getLightsMapLayer().getLights()) {
                if (light instanceof MapPointLight) {
                    TextureAtlas.AtlasRegion texture = iconPack.findRegion("point_light");
                    if (light.isHighlighted()) {
                        texture = iconPack.findRegion("point_light_h");
                    }
                    batch.draw(texture, light.getX() - (texture.getRegionWidth() / gameMap.getTileSize()) / 2, light.getY() - (texture.getRegionHeight() / gameMap.getTileSize()) / 2, texture.getRegionWidth() / gameMap.getTileSize(), texture.getRegionHeight() / gameMap.getTileSize());

                }

                if (light instanceof MapConeLight) {
                    TextureAtlas.AtlasRegion texture = iconPack.findRegion("cone_light");
                    if (light.isHighlighted()) {
                        texture = iconPack.findRegion("cone_light_h");
                    }
                    batch.draw(texture, light.getX() - (texture.getRegionWidth() / gameMap.getTileSize()) / 2, light.getY() - (texture.getRegionHeight() / gameMap.getTileSize()) / 2, texture.getRegionWidth() / gameMap.getTileSize(), texture.getRegionHeight() / gameMap.getTileSize());
                }
            }
        }

        batch.end();


        renderer.begin();
        renderer.set(ShapeRenderer.ShapeType.Filled);
        if (gameMap.getObjectMapLayer().isLayerVisible() && debugRender) {
            for (RectangleObject object : gameMap.getObjectMapLayer().getObjects().getByType(RectangleObject.class)) {
                renderer.setColor(0.6f,0.6f,0.6f,0.5f);
                if (object.isHighlighted()) {
                    renderer.setColor(Color.ORANGE);
                }
                renderer.rect(object.getX(), object.getY(), object.getWidth() / 2, object.getHeight() / 2, object.getWidth(), object.getHeight(), 1, 1, object.getRotation());
            }
        }

        if (renderMaterialObject) {
            for (MapMaterialObject object : gameMap.getMapMaterialObjects()) {
                renderer.setColor(1,1,1,0.5f);
                if (object.isHighlighted()) {
                    renderer.setColor(Color.ORANGE);
                }
                renderer.rect(object.getX(), object.getY(), object.getWidth() / 2, object.getHeight() / 2, object.getWidth(), object.getHeight(), 1, 1, 0);
            }
        }
        renderer.end();
        polygonSpriteBatch.setProjectionMatrix(camera.combined);
        polygonSpriteBatch.begin();
        for(Entity entity: gameMap.getEntityArrayList()){
            if(entity.hasComponent(SkeletonComponent.class)){
                SkeletonComponent skeletonComponent = entity.getComponent(SkeletonComponent.class);
                PhysicsComponent physicsComponent = entity.getComponent(PhysicsComponent.class);
                skeletonComponent.setSkeletonAnimation(true,"idle",0,true);
                skeletonComponent.animationState.update(delta);
                skeletonComponent.skeleton.updateWorldTransform();

                skeletonComponent.skeleton.setPosition(physicsComponent.body.getPosition().x, physicsComponent.body.getPosition().y - (physicsComponent.height / 2));
                skeletonComponent.animationState.apply(skeletonComponent.skeleton);
                skeletonRenderer.draw(polygonSpriteBatch,skeletonComponent.skeleton);
            }
        }
        polygonSpriteBatch.end();

    }
}
