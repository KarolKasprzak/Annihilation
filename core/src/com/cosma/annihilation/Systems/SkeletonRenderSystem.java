package com.cosma.annihilation.Systems;

import box2dLight.RayHandler;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Components.*;
import com.cosma.annihilation.Utils.Constants;
import com.cosma.annihilation.Utils.RenderComparator;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.SkeletonRendererDebug;
import com.badlogic.gdx.math.Interpolation;



public class SkeletonRenderSystem extends IteratingSystem implements Disposable {


    private OrthographicCamera camera;
    private PolygonSpriteBatch batch;
    private World world;
    private SkeletonRenderer skeletonRenderer;
    private  SkeletonRendererDebug debugRenderer;

    private ComponentMapper<SkeletonComponent> skeletonMapper;
    private ComponentMapper<BodyComponent> bodyMapper;


    public SkeletonRenderSystem(OrthographicCamera camera, World world, PolygonSpriteBatch batch) {
        super(Family.all(SkeletonComponent.class, BodyComponent.class).get(),Constants.SKELETONS_RENDER);
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

        if(!skeletonComponent.skeletonDirection){
            skeletonComponent.skeleton.setFlipX(true);
        }else{
            skeletonComponent.skeleton.setFlipX(false);
        }
        skeletonComponent.skeleton.setPosition(bodyComponent.body.getPosition().x ,bodyComponent.body.getPosition().y-(bodyComponent.height/2));
        skeletonComponent.skeleton.updateWorldTransform();
        skeletonComponent.animationState.update(deltaTime);

        debugRenderer.getShapeRenderer().setProjectionMatrix(camera.combined);

        batch.begin();
        skeletonRenderer.draw(batch, skeletonComponent.skeleton);
        batch.end();
//       debugRenderer.draw(skeletonComponent.skeleton);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

}