package com.cosma.annihilation.Utils;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Components.*;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.Utils.Enums.BodyID;

public class FxEntityCreator {
    private World world;


    public FxEntityCreator(World world) {
        this.world = world;
    }


    public Entity createBulletEntity(float x, float y, float targetX, float targetY, float speed, boolean flip){
        Entity entity = new Entity();
        PhysicsComponent physicsComponent = new PhysicsComponent();
        BulletComponent bulletComponent = new BulletComponent();
        TextureComponent textureComponent = new TextureComponent();
        DrawOrderComponent drawOrderComponent = new DrawOrderComponent();



        drawOrderComponent.drawOrder = 6;
        textureComponent.renderAfterLight = true;
        textureComponent.setTextureRegion(Annihilation.getAssets().get("gfx/atlas/fx_textures.atlas", TextureAtlas.class).findRegion("bullet_shell"));
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        physicsComponent.body = world.createBody(bodyDef);
        physicsComponent.body.setUserData(entity);
        physicsComponent.body.setBullet(true);
        //Physic fixture
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.1f, 0.01f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.isSensor = true;
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 1f;
        fixtureDef.filter.categoryBits = CollisionID.BULLET;
        fixtureDef.filter.maskBits = CollisionID.MASK_BULLET;
        physicsComponent.body.createFixture(fixtureDef).setUserData(BodyID.BULLET);
        physicsComponent.body.setGravityScale(0);


        float velx = targetX - x;
        float vely = targetY  - y;
        float length = (float) Math.sqrt(velx * velx + vely * vely);

        if(flip){
            physicsComponent.body.setLinearVelocity(speed,vely/length*speed);
            textureComponent.flipTexture = true;
        }else{
            physicsComponent.body.setLinearVelocity(-speed,vely/length*speed);
        }
        physicsComponent.body.setTransform(physicsComponent.body.getPosition(),MathUtils.atan2(physicsComponent.body.getLinearVelocity().y, physicsComponent.body.getLinearVelocity().x));

        bulletComponent.targetX = targetX;
        bulletComponent.targetY = targetY;

        entity.add(textureComponent);
        entity.add(physicsComponent);
        entity.add(bulletComponent);
        entity.add(drawOrderComponent);

        return entity;
    }



    public Entity createBulletShellEntity(float x, float y){
        Entity entity = new Entity();
        PhysicsComponent physicsComponent = new PhysicsComponent();
        BulletComponent bulletComponent = new BulletComponent();
        TextureComponent textureComponent = new TextureComponent();

        DrawOrderComponent drawOrderComponent = new DrawOrderComponent();
        LifeTimeComponent lifeTimeComponent = new LifeTimeComponent();
        drawOrderComponent.drawOrder = 6;
        textureComponent.setTextureRegion(Annihilation.getAssets().get("gfx/atlas/fx_textures.atlas", TextureAtlas.class).findRegion("bullet_shell"));
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        physicsComponent.body = world.createBody(bodyDef);
        physicsComponent.body.setUserData(entity);
        physicsComponent.body.setBullet(false);
        //Physic fixture
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.04f, 0.01f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.2f;
        fixtureDef.friction = 0.1f;
        fixtureDef.restitution = 0.4f;
        physicsComponent.body.createFixture(fixtureDef);
//        fixtureDef.filter.categoryBits = CollisionID.SCENERY_BACKGROUND_OBJECT ;
//        fixtureDef.filter.maskBits = CollisionID.MASK_SCENERY_BACKGROUND_OBJECT;
        physicsComponent.body.setUserData(entity);
        entity.add(drawOrderComponent);
        entity.add(textureComponent);
        entity.add(physicsComponent);
        entity.add(bulletComponent);
        entity.add(lifeTimeComponent);
        return entity;
    }


}
