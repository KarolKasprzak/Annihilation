package com.cosma.annihilation.Editor.CosmaMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Box2dLight.Light;
import com.cosma.annihilation.Box2dLight.RayHandler;
import com.cosma.annihilation.Components.ActionComponent;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorLights.MapConeLight;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorLights.MapLight;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorLights.MapPointLight;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorObject.RectangleObject;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.Utils.Constants;

import java.util.Arrays;


public class MapRender {

    private ShapeRenderer renderer;
    private int scale;
    private GameMap gameMap;
    private SpriteBatch batch;
    private TextureAtlas iconPack;
    private Vector2 position = new Vector2();
    private ShaderProgram shader;

    private Vector3 lightPosition = new Vector3();
    private float[] lightPositionArray = new float[21];
    private float[] lightColorArray = new float[21];
    private Array<Light> activeLights = new Array<>();

    private OrthographicCamera camera;
    private RayHandler rayHandler;


    public MapRender(ShapeRenderer renderer, GameMap gameMap, SpriteBatch batch, RayHandler rayHandler, OrthographicCamera camera) {
        this.camera = camera;
        this.rayHandler = rayHandler;

        this.batch = batch;
        this.gameMap = gameMap;
        this.scale = gameMap.getTileSize();
        this.renderer = renderer;
        iconPack = Annihilation.getAssets().get("gfx/atlas/editor_icon.atlas", TextureAtlas.class);

        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(Gdx.files.internal("shaders/normalMap/ver.glsl").readString(), Gdx.files.internal("shaders/normalMap/frag.glsl").readString());
        if (!shader.isCompiled())
            throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
        shader.begin();
        shader.setUniformi("u_texture", 0);
        shader.setUniformi("u_normals", 1);
        shader.end();
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

    public void prepareDataForNormalShaderRender() {
        Arrays.fill(lightColorArray, 0);
        Arrays.fill(lightPositionArray, 0);
        activeLights.clear();
        for (Light light : rayHandler.getLightList()) {
            activeLights.add(light);
//            if (camera.frustum.sphereInFrustum(light.getX(), light.getY(), 0, light.getDistance())) {
//                activeLights.add(light);
//            }
        }
        for (int i = 0; i < activeLights.size; i++) {
            if (i < 7) {
                Light light = activeLights.get(i);
                lightPosition.x = light.getX();
                lightPosition.y = light.getY();
                lightPosition.z = 0;

                camera.project(lightPosition);

                lightPositionArray[i * 3] = lightPosition.x;
                lightPositionArray[1 + (i * 3)] = lightPosition.y;
                lightPositionArray[2 + (i * 3)] = 0.05f;

                lightColorArray[i * 3] = light.getColor().r;
                lightColorArray[1 + (i * 3)] = light.getColor().g;
                lightColorArray[2 + (i * 3)] = light.getColor().b;
            }
        }
        shader.setUniform3fv("light[0]", lightPositionArray, 0, 21);
        shader.setUniform3fv("lightColor[0]", lightColorArray, 0, 21);
        shader.setUniformi("xInvert", 0);
        shader.setUniformi("yInvert", 0);
        shader.setUniformf("resolution",Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shader.setUniformf("strength", 1f);
        Color color = gameMap.getLightsMapLayer().getShaderAmbientLightColor();
        shader.setUniformf("ambientColor", color.r, color.g, color.b,gameMap.getLightsMapLayer().getShaderAmbientLightIntensity());
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public void renderMap(float delta,boolean renderWithShader , boolean debugRender) {


        //render sprite
        if(renderWithShader){
            batch.setShader(shader);
        }else{
            batch.setShader(null);
        }

        if (gameMap.getSpriteMapLayer().isLayerVisible()) {
            gameMap.getSpriteMapLayer().getSpriteArray().sort();
            for (Sprite sprite : gameMap.getSpriteMapLayer().getSpriteArray()) {

                if (sprite instanceof AnimatedSprite) {
                    ((AnimatedSprite) sprite).updateAnimation(delta);
                }



                sprite.bindNormalTexture(1);
                sprite.getTextureRegion().getTexture().bind(0);

                batch.begin();
                if (sprite.getTextureRegion() != null) {
                    prepareDataForNormalShaderRender();
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
        batch.flush();

        batch.begin();

        //render entity
        for (Entity entity : gameMap.getEntityArrayList()) {
            if (entity.getComponent(ActionComponent.class) != null) {
                if (entity.getComponent(ActionComponent.class).actionTargetPosition != null) {
                    ActionComponent actionComponent = entity.getComponent(ActionComponent.class);
                    Texture texture = Annihilation.getAssets().get("gfx/interface/target.png", Texture.class);
                    batch.draw(texture, actionComponent.actionTargetPosition.x - (texture.getWidth() / 2 / Constants.PPM), actionComponent.actionTargetPosition.y - (texture.getHeight() / 2 / Constants.PPM),
                            texture.getHeight() / Constants.PPM, texture.getWidth() / Constants.PPM);
                }
            }
        }
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
        if (gameMap.getObjectMapLayer().isLayerVisible()) {
            for (RectangleObject object : gameMap.getObjectMapLayer().getObjects().getByType(RectangleObject.class)) {
                renderer.setColor(Color.WHITE);
                if (object.isHighlighted()) {
                    renderer.setColor(Color.ORANGE);
                }
                renderer.rect(object.getX(), object.getY(), object.getWidth() / 2, object.getHeight() / 2, object.getWidth(), object.getHeight(), 1, 1, object.getRotation());
            }
        }
        renderer.end();
    }
}
