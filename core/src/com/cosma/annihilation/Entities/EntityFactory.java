package com.cosma.annihilation.Entities;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Components.*;
import com.cosma.annihilation.Utils.Enums.BodyID;
import com.cosma.annihilation.Utils.CollisionID;

public class EntityFactory {

    private static EntityFactory instance = null;
    private Engine engine;
    private World world;

    private EntityFactory() {
    }


    public void setEngine(Engine engine) {

        this.engine = engine;
    }

    public void setWorld(World world) {

        this.world = world;
    }

    public static EntityFactory getInstance() {
        if (instance == null) {
            instance = new EntityFactory();
        }

        return instance;
    }


    public Entity createBulletEntity(float x, float y,float targetX, float targetY, float speed,boolean flip){
        Entity entity = engine.createEntity();
        BodyComponent bodyComponent = engine.createComponent(BodyComponent.class);
        BulletComponent bulletComponent = engine.createComponent(BulletComponent.class);
        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);

        textureComponent.texture = Annihilation.getAssets().get("gfx/textures/bullet_trace.png");
        textureComponent.renderAfterLight = true;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyComponent.body = world.createBody(bodyDef);
        bodyComponent.body.setUserData(entity);
        bodyComponent.body.setBullet(true);
        //Physic fixture
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.3f, 0.01f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.isSensor = true;
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 1f;
        fixtureDef.filter.categoryBits = CollisionID.BULLET;
        fixtureDef.filter.maskBits = CollisionID.MASK_BULLET;
        bodyComponent.body.createFixture(fixtureDef).setUserData(BodyID.BULLET);

        float velx = targetX - x;
        float vely = targetY  - y;
        float length = (float) Math.sqrt(velx * velx + vely * vely);

        if(flip){
            bodyComponent.body.setLinearVelocity(speed,vely/length*speed);
            textureComponent.flipTexture = true;
        }else{
            bodyComponent.body.setLinearVelocity(-speed,vely/length*speed);
        }
        bodyComponent.body.setTransform(bodyComponent.body.getPosition(),MathUtils.atan2(bodyComponent.body.getLinearVelocity().y,bodyComponent.body.getLinearVelocity().x));

        entity.add(textureComponent);
        entity.add(bodyComponent);
        entity.add(bulletComponent);

        return entity;
    }

    public Entity createBulletShellEntity(float x, float y){
        Entity entity = engine.createEntity();
        BodyComponent bodyComponent = engine.createComponent(BodyComponent.class);
        BulletComponent bulletComponent = engine.createComponent(BulletComponent.class);
        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);

        textureComponent.texture = Annihilation.getAssets().get("gfx/textures/bullet_shell.png");
        textureComponent.renderAfterLight = false;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyComponent.body = world.createBody(bodyDef);
        bodyComponent.body.setUserData(entity);
        bodyComponent.body.setBullet(false);
        //Physic fixture
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.02f, 0.01f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.2f;
        fixtureDef.friction = 0.1f;
        fixtureDef.restitution = 0.4f;

        fixtureDef.filter.categoryBits = CollisionID.SCENERY_BACKGROUND_OBJECT ;
        fixtureDef.filter.maskBits = CollisionID.MASK_SCENERY_BACKGROUND_OBJECT;

        bodyComponent.body.createFixture(fixtureDef).setUserData(BodyID.BULLET_SHELL);

        entity.add(textureComponent);
        entity.add(bodyComponent);
        entity.add(bulletComponent);

        return entity;
    }

    public Entity createShootSplashEntity(float x, float y,boolean flip){
        Entity entity =  engine.createEntity();
        SpriteComponent spriteComponent = engine.createComponent(SpriteComponent.class);
        spriteComponent.texture = Annihilation.getAssets().get("gfx/textures/splash.png");
        spriteComponent.x = x;
        spriteComponent.y = y;
        spriteComponent.isLifeTimeLimited = true;
        spriteComponent.lifeTime = 0.18f;

        if(flip){
            spriteComponent.flipX = true;
        }
        entity.add(spriteComponent);

        return entity;
    }

    public Entity createBloodSplashEntity(float x, float y, float angle){
        Entity entity =  engine.createEntity();
        SpriteComponent spriteComponent = engine.createComponent(SpriteComponent.class);
        spriteComponent.texture = Annihilation.getAssets().get("gfx/textures/blood.png");
        spriteComponent.x = x;
        spriteComponent.y = y;
        spriteComponent.isLifeTimeLimited = true;
        spriteComponent.lifeTime = 20;
        spriteComponent.angle = angle;
        entity.add(spriteComponent);

        return entity;
    }


//    public Entity createDoorEntity(){
//        Entity entity = new Entity();
//        BodyComponent bodyComponent = new BodyComponent();
//        TextureComponent textureComponent = new TextureComponent();
//        ActionComponent actionComponent = new ActionComponent();
//        HealthComponent healthComponent = new HealthComponent();
//        DoorComponent doorComponent = new DoorComponent();
//
//        healthComponent.hp = 100;
//        healthComponent.maxHP = 100;
//
//
//
//
//        actionComponent.action = EntityAction.OPEN_DOOR;
//
//        //----------Body Component----------------------
//        BodyDef bodyDef = new BodyDef();
//        bodyDef.type = BodyDef.BodyType.KinematicBody;
//        bodyComponent.body = world.createBody(bodyDef);
//        bodyComponent.body.setUserData(entity);
//        //Physic fixture
//        PolygonShape shape = new PolygonShape();
//        shape.setAsBox( 0.1f,1);
//        FixtureDef fixtureDef = new FixtureDef();
//        fixtureDef.shape = shape;
//        fixtureDef.density = 8f;
//        fixtureDef.friction = 1f;
//        fixtureDef.filter.categoryBits = CollisionID.CAST_SHADOW;
//        bodyComponent.body.createFixture(fixtureDef);
//        //Sensor fixture
//        CircleShape sensorShape = new CircleShape();
//        sensorShape.setRadius(1);
//        FixtureDef touchSensorFixture = new FixtureDef();
//        touchSensorFixture.shape = sensorShape;
//        touchSensorFixture.isSensor = true;
//        touchSensorFixture.filter.categoryBits = CollisionID.NO_SHADOW;
//
//        //-----------Body Component End----------------------
//        entity.add(doorComponent);
//        entity.add(textureComponent);
//        entity.add(bodyComponent);
//        entity.add(actionComponent);
//        entity.add(healthComponent);
//        engine.addEntity(entity);
//
//        return entity;
//    }
}

