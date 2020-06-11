package com.cosma.annihilation.Systems;


import box2dLight.Light;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.cosma.annihilation.Components.*;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorLights.MapLight;
import com.cosma.annihilation.Editor.CosmaMap.LightsMapLayer;
import com.cosma.annihilation.EntityEngine.core.ComponentMapper;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.EntityEngine.core.Family;
import com.cosma.annihilation.EntityEngine.systems.IteratingSystem;
import com.cosma.annihilation.Utils.Constants;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.SkeletonRendererDebug;
import shaders.LightShader;


public class SkeletonRenderSystem extends IteratingSystem implements Disposable {


    private OrthographicCamera camera;
    private PolygonSpriteBatch batch;
    private World world;
    private SkeletonRenderer skeletonRenderer;
    private SkeletonRendererDebug debugRenderer;

    private ComponentMapper<SkeletonComponent> skeletonMapper;
    private ComponentMapper<BodyComponent> bodyMapper;

    private ShaderProgram shader;

    private Vector3 ambientColor = new Vector3();
    private Vector3 lightColor = new Vector3();
    private Vector3 lightPosition = new Vector3();
    private Vector2 resolution = new Vector2();
    private Vector3 attenuation = new Vector3();

    Texture normalMapTexture;



    public SkeletonRenderSystem(OrthographicCamera camera, World world, PolygonSpriteBatch batch) {
        super(Family.all(SkeletonComponent.class, BodyComponent.class).get(), Constants.SKELETONS_RENDER);
        this.batch = batch;
        this.camera = camera;
        this.world = world;

        debugRenderer = new SkeletonRendererDebug();
        debugRenderer.setBoundingBoxes(true);
        debugRenderer.setRegionAttachments(true);
        debugRenderer.setScale(0.01f);
        skeletonRenderer = new SkeletonRenderer();
        skeletonRenderer.setPremultipliedAlpha(false);
        skeletonMapper = ComponentMapper.getFor(SkeletonComponent.class);
        bodyMapper = ComponentMapper.getFor(BodyComponent.class);

        String vertexShader = Gdx.files.internal("shaders/bump/ver.glsl").readString();
        String fragmentShader = Gdx.files.internal("shaders/bump/frag.glsl").readString();
        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(vertexShader, fragmentShader);
        if (!shader.isCompiled()) throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
        shader.begin();
        shader.setUniformi("u_texture", 0);
        shader.setUniformi("u_normals", 1);
        shader.end();

        normalMapTexture = new Texture(Gdx.files.internal("gfx/skeletons/player/player_nrm.png"));
    }

    @Override
    public void update(float deltaTime) {

        batch.setProjectionMatrix(camera.combined);
        super.update(deltaTime);
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        SkeletonComponent skeletonComponent = skeletonMapper.get(entity);
        BodyComponent bodyComponent = bodyMapper.get(entity);

        if (entity.getComponent(PlayerComponent.class) == null) {
            skeletonComponent.animationState.apply(skeletonComponent.skeleton);
        }

        if (!skeletonComponent.skeletonDirection) {
            skeletonComponent.skeleton.setFlipX(true);
        } else {
            skeletonComponent.skeleton.setFlipX(false);
        }
        skeletonComponent.skeleton.setPosition(bodyComponent.body.getPosition().x, bodyComponent.body.getPosition().y - (bodyComponent.height / 2));
        skeletonComponent.skeleton.updateWorldTransform();
        skeletonComponent.animationState.update(deltaTime);

        debugRenderer.getShapeRenderer().setProjectionMatrix(camera.combined);

        MapLight mapLight = this.getEngine().getCurrentMap().getLayers().getByType(LightsMapLayer.class).first().getLights().getLight(0);
//        lightPosition.x = mapLight.getX();
//        lightPosition.y = mapLight.getY();


        lightPosition.x = Gdx.input.getX();
        lightPosition.y = (Gdx.graphics.getHeight() - Gdx.input.getY());

        lightPosition.z = 0;
//        camera.project(lightPosition);

        Color color = Color.WHITE;
        Color lightColor = mapLight.getColor();
        ambientColor.x = color.r;
        ambientColor.y = color.g;
        ambientColor.z = color.b;
        this.lightColor.x = lightColor.r;
        this.lightColor.y = lightColor.g;
        this.lightColor.z = lightColor.b;
        resolution.set(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        attenuation.x = 0.9f;
        attenuation.y = 1;
        attenuation.z = 1;
        lightPosition.z = 0.05f;


        batch.setShader(shader);
        batch.begin();
        shader.setUniformi("yInvert", 0);
        if(skeletonComponent.skeletonDirection){
            shader.setUniformi("zInvert", 0);
        }else{
            shader.setUniformi("zInvert", 1);
        }

        shader.setUniformf("resolution", resolution);
        shader.setUniformf("ambientColor", ambientColor);
        shader.setUniformf("ambientIntensity", 0.35f);
        shader.setUniformf("attenuation", attenuation);
        shader.setUniformf("light", lightPosition);
        shader.setUniformf("lightColor", this.lightColor);
        shader.setUniformi("useNormals", 1);
        shader.setUniformi("useShadow", 1);
        shader.setUniformf("strength", 1f);

//        normalMapTexture.bind(1);
        skeletonComponent.normalTexture.bind(1);
        skeletonComponent.diffuseTexture.bind(0);

        skeletonRenderer.draw(batch, skeletonComponent.skeleton);
        batch.end();
        batch.setShader(null);
//       debugRenderer.draw(skeletonComponent.skeleton);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

}