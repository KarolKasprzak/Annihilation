package com.cosma.annihilation.Editor.CosmaMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorLights.MapConeLight;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorLights.MapLight;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorLights.MapPointLight;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorLights.MapSunLight;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorObject.RectangleObject;
import com.cosma.annihilation.Utils.Constants;

public class MapRender {

    private ShapeRenderer renderer;
    private int scale;
    private GameMap gameMap;
    private SpriteBatch batch;
    private TextureAtlas iconPack;
    private Vector2 position = new Vector2();

    public MapRender(ShapeRenderer renderer, GameMap gameMap, SpriteBatch batch) {
        this.batch = batch;
        this.gameMap = gameMap;
        this.scale = gameMap.getTileSize();
        this.renderer = renderer;
        iconPack = Annihilation.getAssets().get("gfx/atlas/editor_icon.atlas", TextureAtlas.class);
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

    public void renderMap(float delta) {
        batch.begin();
        if (gameMap.getLayers().getCount() > 0) {
            for (TileMapLayer mapLayer : gameMap.getLayers().getByType(TileMapLayer.class)) {
                if (mapLayer.isLayerVisible()) {
                    for (int x = 0; x < gameMap.getWidth(); x++) {
                        for (int y = 0; y < gameMap.getHeight(); y++) {
                            if (mapLayer.getTile(x, y) == null) {
                                continue;
                            }
                            Tile tile = mapLayer.getTile(x, y);
                            if (tile.getTextureRegion() == null) {
                                continue;
                            }
                            TextureRegion texture = tile.getTextureRegion();
                            batch.draw(tile.getTextureRegion(), x, y, texture.getRegionWidth()/gameMap.getTileSize(), texture.getRegionHeight()/gameMap.getTileSize());
                        }
                    }
                }
            }

            for (SpriteMapLayer mapLayer : gameMap.getLayers().getByType(SpriteMapLayer.class)) {
                if (mapLayer.isLayerVisible()) {
                    for (Sprite sprite : mapLayer.getSpriteArray()) {
                        if(sprite instanceof AnimatedSprite){
                            ((AnimatedSprite) sprite).updateAnimation(delta);
                        }
                        if (sprite.getTextureRegion() != null) {
                            position.set(sprite.getX(), sprite.getY());
                            batch.draw(sprite.getTextureRegion(), position.x + (sprite.isFlipX() ? sprite.getTextureRegion().getRegionWidth()/Constants.PPM : 0), position.y, (float) sprite.getTextureRegion().getRegionWidth()/Constants.PPM/2, (float) sprite.getTextureRegion().getRegionHeight()/Constants.PPM/2,
                                    sprite.getTextureRegion().getRegionWidth()/Constants.PPM * (sprite.isFlipX() ? -1 : 1), sprite.getTextureRegion().getRegionHeight()/Constants.PPM,
                                    1, 1, sprite.getAngle());

                        }
                    }
                }
            }

            for (LightsMapLayer mapLayer : gameMap.getLayers().getByType(LightsMapLayer.class)) {
                if (mapLayer.isLayerVisible()) {
                    for (MapLight light : mapLayer.getLights()) {
                        if (light instanceof MapPointLight) {
                            TextureAtlas.AtlasRegion texture = iconPack.findRegion("point_light");
                            if (light.isHighlighted()) {
                                texture = iconPack.findRegion("point_light_h");
                            }
                            batch.draw(texture, light.getX() - (texture.getRegionWidth() / gameMap.getTileSize()) / 4, light.getY() - (texture.getRegionHeight() / gameMap.getTileSize()) / 4, texture.getRegionWidth() / gameMap.getTileSize() / 2, texture.getRegionHeight() / gameMap.getTileSize() / 2);
                        }

                        if (light instanceof MapConeLight || light instanceof MapSunLight) {
                            TextureAtlas.AtlasRegion texture = iconPack.findRegion("cone_light");
                            if (light.isHighlighted()) {
                                texture = iconPack.findRegion("point_light_h");
                            }
                            batch.draw(texture, light.getX() - (texture.getRegionWidth() / gameMap.getTileSize()) / 4, light.getY() - (texture.getRegionHeight() / gameMap.getTileSize()) / 4, texture.getRegionWidth() / gameMap.getTileSize() / 2, texture.getRegionHeight() / gameMap.getTileSize() / 2);
                        }
                    }
                }
            }

        }
        batch.end();
        renderer.begin();
        for (ObjectMapLayer layer : gameMap.getLayers().getByType(ObjectMapLayer.class)) {
            if (layer.isLayerVisible()) {
                for (RectangleObject object : layer.getObjects().getByType(RectangleObject.class)) {
                    renderer.setColor(Color.WHITE);
                    if (object.isHighlighted()) {
                        renderer.setColor(Color.ORANGE);
                    }
                    renderer.rect(object.getX(), object.getY(), object.getWidth() / 2, object.getHeight() / 2, object.getWidth(), object.getHeight(), 1, 1, object.getRotation());
                }

            }
        }
        renderer.end();
    }
}
