package com.cosma.annihilation.Utils.Serialization;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.cosma.annihilation.Ai.HumanAiBasic;
import com.cosma.annihilation.Ai.NpcAiBasic;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Components.*;
import com.cosma.annihilation.Utils.Animation.AnimationFactory;
import com.cosma.annihilation.Utils.CollisionID;
import com.cosma.annihilation.Utils.Enums.AiType;
import com.cosma.annihilation.Utils.Enums.BodyID;
import com.cosma.annihilation.Utils.Enums.EntityAction;
import com.esotericsoftware.spine.*;


public class EntityReader implements Json.Serializer<Entity> {
    private World world;
    private Engine engine;
    private AnimationFactory animationFactory;

    /**
     * use in game
     **/
    EntityReader(World world, Engine engine) {
        this.world = world;
        this.engine = engine;
        animationFactory = new AnimationFactory();
    }

    /**
     * use only in map editor
     **/
    public EntityReader(World world) {
        this.world = world;
        this.engine = null;
        animationFactory = new AnimationFactory();
    }

    @Override
    public void write(Json json, Entity object, Class knownType) {
    }

    @Override
    public Entity read(Json json, JsonValue jsonData, Class type) {
        Entity entity = new Entity();

        if (jsonData.has("BodyComponent")) {

            BodyComponent bodyComponent = new BodyComponent();
            if(jsonData.get("BodyComponent").has("width") && jsonData.get("BodyComponent").has("height")){
                bodyComponent.width = jsonData.get("BodyComponent").get("width").asFloat();
                bodyComponent.height = jsonData.get("BodyComponent").get("height").asFloat();
            }
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.valueOf(jsonData.get("BodyComponent").get("bodyType").asString());
            bodyComponent.body = world.createBody(bodyDef);
            bodyComponent.body.setFixedRotation(jsonData.get("BodyComponent").get("fixedRotation").asBoolean());
            bodyComponent.body.setBullet(jsonData.get("BodyComponent").get("bullet").asBoolean());
            bodyComponent.body.setUserData(entity);
            bodyComponent.body.setTransform(new Vector2(jsonData.get("BodyComponent").get("positionX").asFloat(), jsonData.get("BodyComponent").get("positionY").asFloat()), 0);

            for (JsonValue value : jsonData.get("BodyComponent").get("Fixtures")) {
                FixtureDef fixtureDef = new FixtureDef();

                if (value.has("shapeX") && value.has("shapeY")) {
                    PolygonShape shape = new PolygonShape();
                    shape.setAsBox(value.get("shapeW").asFloat() / 2, value.get("shapeH").asFloat() / 2,
                            new Vector2(value.get("shapeX").asFloat(), value.get("shapeY").asFloat()), value.get("shapeA").asFloat());
                    fixtureDef.shape = shape;
                }

                if (value.has("polygon")) {
                    if (value.get("shapeType").asString().equals("Polygon")) {
                        PolygonShape shape = new PolygonShape();
                        shape.set(value.get("polygon").asFloatArray());
                        fixtureDef.shape = shape;
                    }
                }

                if (value.get("shapeType").asString().equals("Circle")) {
                    CircleShape shape = new CircleShape();
                    shape.setRadius(value.get("radius").asFloat());
                    fixtureDef.shape = shape;
                }
                fixtureDef.isSensor = value.get("sensor").asBoolean();
                fixtureDef.density = value.get("destiny").asFloat();
                fixtureDef.friction = value.get("friction").asFloat();
                fixtureDef.restitution = value.get("restitution").asFloat();

                if (value.has("categoryBits")) {
                    switch (value.get("categoryBits").asString()) {
                        case "player":
                            fixtureDef.filter.categoryBits = CollisionID.PLAYER;
                            break;
                        case "light":
                            fixtureDef.filter.categoryBits = CollisionID.LIGHT;
                            break;
                        case "scenery":
                            fixtureDef.filter.categoryBits = CollisionID.SCENERY;
                            break;
                        case "scenery_bg":
                            fixtureDef.filter.categoryBits = CollisionID.SCENERY_BACKGROUND_OBJECT;
                            break;
                        case "scenery_phy":
                            fixtureDef.filter.categoryBits = CollisionID.SCENERY_PHYSIC_OBJECT;
                            break;
                        case "enemy":
                            fixtureDef.filter.categoryBits = CollisionID.ENEMY;
                            break;
                        case "npc":
                            fixtureDef.filter.categoryBits = CollisionID.NPC;
                            break;
                        case "action":
                            fixtureDef.filter.categoryBits = CollisionID.ACTION;
                            break;

                    }
                }
                if (value.has("maskBits")) {
                    switch (value.get("maskBits").asString()) {
                        case "player":
                            fixtureDef.filter.maskBits = CollisionID.MASK_PLAYER;
                            break;
                        case "light":
                            fixtureDef.filter.maskBits = CollisionID.MASK_LIGHT;
                            break;
                        case "scenery":
                            fixtureDef.filter.maskBits = CollisionID.MASK_SCENERY;
                            break;
                        case "scenery_bg":
                            fixtureDef.filter.maskBits = CollisionID.MASK_SCENERY_BACKGROUND_OBJECT;
                            break;
                        case "scenery_phy":
                            fixtureDef.filter.maskBits = CollisionID.MASK_SCENERY_PHYSIC_OBJECT;
                            break;
                        case "enemy":
                            fixtureDef.filter.maskBits = CollisionID.MASK_ENEMY;
                            break;
                        case "npc":
                            fixtureDef.filter.maskBits = CollisionID.MASK_NPC;
                            break;
                        case "action":
                            fixtureDef.filter.maskBits = CollisionID.MASK_ACTION;
                            break;

                    }
                }
                if(value.has("hasCustomDate")){
                    bodyComponent.body.createFixture(fixtureDef).setUserData(value.get("userDate").asString());
                }else{
                    if(value.has("userDate")){
                        bodyComponent.body.createFixture(fixtureDef).setUserData(BodyID.valueOf(value.get("userDate").asString()));
                    }else{
                        bodyComponent.body.createFixture(fixtureDef);
                    }
                }
                fixtureDef.shape.dispose();
                entity.add(bodyComponent);
            }
        }

        if (jsonData.has("HealthComponent")) {
            HealthComponent healthComponent = new HealthComponent();
            healthComponent.hp = jsonData.get("HealthComponent").get("hp").asInt();
            healthComponent.maxHP = jsonData.get("HealthComponent").get("maxHP").asInt();
            entity.add(healthComponent);
        }

        if (jsonData.has("SkeletonComponent")) {
            SkeletonComponent skeletonComponent = new SkeletonComponent();
            TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(jsonData.get("SkeletonComponent").get("atlasPath").asString()));
            SkeletonJson skeletonJson = new SkeletonJson(atlas);

            skeletonJson.setScale(skeletonJson.getScale()/64);
            SkeletonData skeletonData = skeletonJson.readSkeletonData(Gdx.files.internal(jsonData.get("SkeletonComponent").get("jsonPath").asString()));
            skeletonComponent.skeleton = new Skeleton(skeletonData);

            AnimationStateData stateData = new AnimationStateData(skeletonData);
            stateData.setDefaultMix(0.25f);
            //todo
            //mixing animation *future
//                        stateData.setMix("idle", "walk", 0.2f);
//                        stateData.setMix("walk", "idle", 0.2f);
//

            skeletonComponent.animationState = new AnimationState(stateData);
            entity.add(skeletonComponent);
        }

        if (jsonData.has("TextureComponent")) {
            //TODO
            TextureComponent textureComponent = new TextureComponent();
            if (jsonData.get("TextureComponent").has("patch")) {
                textureComponent.texture = Annihilation.getAssets().get(jsonData.get("TextureComponent").get("patch").asString());
            }
            if (jsonData.get("TextureComponent").has("renderOrder")) {
                textureComponent.renderOrder = jsonData.get("TextureComponent").get("renderOrder").asInt();
            }
            entity.add(textureComponent);
        }

        if (jsonData.has("SerializationComponent")) {
            SerializationComponent serializationComponent = new SerializationComponent();
            serializationComponent.entityName = jsonData.get("SerializationComponent").get("entityName").asString();
            entity.add(serializationComponent);
        }

        if (jsonData.has("DialogueComponent")) {
            DialogueComponent dialogueComponent = new DialogueComponent();
            dialogueComponent.dialogId = jsonData.get("DialogueComponent").get("dialogId").asString();
            entity.add(dialogueComponent);
        }

        if (jsonData.has("GateComponent")) {
            GateComponent gateComponent = new GateComponent();
            entity.add(gateComponent);
        }

        if (jsonData.has("StateComponent")) {
            StateComponent stateComponent = new StateComponent();
            entity.add(stateComponent);
        }

        if (jsonData.has("ContainerComponent")) {
            ContainerComponent containerComponent = new ContainerComponent();
            containerComponent.name = jsonData.get("ContainerComponent").get("name").asString();
            containerComponent.itemList = new Array<>();
            entity.add(containerComponent);
        }

        if (jsonData.has("ActionComponent")) {
            ActionComponent actionComponent = new ActionComponent();
            actionComponent.action = EntityAction.valueOf(jsonData.get("ActionComponent").get("action").asString());
            entity.add(actionComponent);
        }

        if (jsonData.has("PlayerComponent")) {
            PlayerComponent playerComponent = new PlayerComponent();
            entity.add(playerComponent);
        }

        if (jsonData.has("AnimationComponent")) {
            AnimationComponent animationComponent = new AnimationComponent();
            animationComponent.animationId = AnimationFactory.AnimationId.valueOf(jsonData.get("AnimationComponent").getString("id"));
            animationComponent.animationMap = animationFactory.createAnimationMap(animationComponent.animationId);
            entity.add(animationComponent);
        }

        if (jsonData.has("PlayerInventoryComponent")) {
            PlayerInventoryComponent playerInventoryComponent = new PlayerInventoryComponent();
            entity.add(playerInventoryComponent);
        }

        if (jsonData.has("PlayerStatsComponent")) {
            PlayerStatsComponent playerStatsComponent = new PlayerStatsComponent();
            entity.add(playerStatsComponent);

        }

        if (jsonData.has("AiComponent")) {
            AiComponent aiComponent = new AiComponent();
            aiComponent.aiType = AiType.valueOf(jsonData.get("AiComponent").getString("aiType"));
            switch(aiComponent.aiType){
                case HUMAN_NPC:
                    aiComponent.ai = new NpcAiBasic();
                    break;
                case HUMAN_ENEMY:
                    aiComponent.ai = new HumanAiBasic();
                    break;
            }
            entity.add(aiComponent);
        }

        if (engine != null) {
            engine.addEntity(entity);
        }
        return entity;
    }

}
