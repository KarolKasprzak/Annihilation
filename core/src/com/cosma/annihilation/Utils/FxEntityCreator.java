package com.cosma.annihilation.Utils;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Components.BulletComponent;
import com.cosma.annihilation.Components.DrawOrderComponent;
import com.cosma.annihilation.Components.PhysicsComponent;
import com.cosma.annihilation.Components.TextureComponent;
import com.cosma.annihilation.EntityEngine.core.Engine;
import com.cosma.annihilation.EntityEngine.core.Entity;

public class FxEntityCreator {
    private Engine engine;
    private World world;

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public FxEntityCreator(World world) {
        this.world = world;
    }

    public Entity createBulletShellEntity(float x, float y){
        Entity entity = new Entity();
        PhysicsComponent physicsComponent = new PhysicsComponent();
        BulletComponent bulletComponent = new BulletComponent();
        TextureComponent textureComponent = new TextureComponent();
        DrawOrderComponent drawOrderComponent = new DrawOrderComponent();
        drawOrderComponent.drawOrder = 6;

        textureComponent.setTextureRegion(Annihilation.getAssets().get("gfx/atlas/fx_textures.atlas", TextureAtlas.class).findRegion("bullet_shell"));


        System.out.println(world.getBodyCount());
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


        System.out.println(world.getBodyCount());

//        fixtureDef.filter.categoryBits = CollisionID.SCENERY_BACKGROUND_OBJECT ;
//        fixtureDef.filter.maskBits = CollisionID.MASK_SCENERY_BACKGROUND_OBJECT;
        physicsComponent.body.setUserData(entity);
        entity.add(drawOrderComponent);
        entity.add(textureComponent);
        entity.add(physicsComponent);
        entity.add(bulletComponent);

        return entity;
    }


}
