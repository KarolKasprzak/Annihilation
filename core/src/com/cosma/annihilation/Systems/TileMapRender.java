package com.cosma.annihilation.Systems;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.cosma.annihilation.Editor.CosmaMap.*;
import com.cosma.annihilation.EntityEngine.core.Engine;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.EntityEngine.core.Family;
import com.cosma.annihilation.EntityEngine.systems.IteratingSystem;
import com.cosma.annihilation.Utils.Constants;

public class TileMapRender extends IteratingSystem {

    private OrthographicCamera camera;
    private GameMap tiledMap;
    private Vector2 position = new Vector2();
    protected Batch batch;
    private ShaderProgram normalShader;


    public TileMapRender(OrthographicCamera camera, GameMap tiledMap) {
        super(Family.all().get(), Constants.TILE_MAP_RENDER);
        this.batch = new SpriteBatch();
        this.camera = camera;
        this.tiledMap = tiledMap;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        this.normalShader = engine.getNormalMapShaderInstance();

    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        normalShader.begin();
        normalShader.setUniformi("u_normals", 1);
        normalShader.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (TileMapLayer mapLayer : tiledMap.getLayers().getByType(TileMapLayer.class)) {
            if (mapLayer.isLayerVisible()) {
                for (int x = 0; x < tiledMap.getWidth(); x++) {
                    for (int y = 0; y < tiledMap.getHeight(); y++) {
                        Tile tile = mapLayer.getTile(x, y);
                        if (tile == null) {
                            continue;
                        }
                        if (tile.getTextureRegion() == null) {
                            continue;
                        }
                        TextureRegion texture = tile.getTextureRegion();
                        batch.draw(texture, x, y, texture.getRegionWidth() / tiledMap.getTileSize(), texture.getRegionHeight() / tiledMap.getTileSize());
                    }
                }
            }
        }
        batch.end();

        batch.setShader(null);
        for (SpriteMapLayer mapLayer : tiledMap.getLayers().getByType(SpriteMapLayer.class)) {
            if (mapLayer.isLayerVisible()) {
                for (Sprite sprite : mapLayer.getSpriteArray()) {
                    position.set(sprite.getX(), sprite.getY());
                    if (sprite instanceof AnimatedSprite) {
                        ((AnimatedSprite) sprite).updateAnimation(deltaTime);
                    }
                    sprite.bindNormalTexture(1);
                    sprite.getTextureRegion().getTexture().bind(0);
                    batch.begin();


                   this.getEngine().prepareDataForNormalShaderRender(normalShader, false, false);
                    batch.draw(sprite.getTextureRegion(), position.x + (sprite.isFlipX() ? sprite.getTextureRegion().getRegionWidth() / Constants.PPM : 0), position.y, (float) sprite.getTextureRegion().getRegionWidth() / Constants.PPM, (float) sprite.getTextureRegion().getRegionHeight() / Constants.PPM,
                            sprite.getTextureRegion().getRegionWidth() / Constants.PPM * (sprite.isFlipX() ? -1 : 1), sprite.getTextureRegion().getRegionHeight() / Constants.PPM,
                            1, 1, sprite.getAngle());
                    batch.end();
                }
            }
        }
        batch.setShader(null);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }
}
