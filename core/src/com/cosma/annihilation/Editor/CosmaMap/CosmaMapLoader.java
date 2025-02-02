package com.cosma.annihilation.Editor.CosmaMap;


import com.cosma.annihilation.Box2dLight.RayHandler;
import com.cosma.annihilation.EntityEngine.core.Engine;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Json;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorObject.RectangleObject;
import com.cosma.annihilation.Utils.Serialization.GameEntitySerializer;
import com.cosma.annihilation.Utils.Util;


public class CosmaMapLoader {
    private GameMap map;
    private World world;
    private Engine engine;
    private RayHandler rayHandler;


    public CosmaMapLoader(World world, RayHandler rayHandler, Engine engine) {
        this.world = world;
        this.engine = engine;
        this.rayHandler = rayHandler;
    }

    public void loadMap(String mapPath) {
        FileHandle mapFile = Gdx.files.local(mapPath);
        Json json = new Json();
        json.setUsePrototypes(false);
        json.setSerializer(Entity.class,new GameEntitySerializer(world,engine));
        map = json.fromJson(GameMap.class, mapFile);

        rayHandler.setAmbientLight(0.8f,0.8f,0.8f,1);

//        rayHandler.setAmbientLight(0.8f);
        createMapObject();
    }

    private void createMapObject(){

        //load objects
            for (RectangleObject object : map.getObjectMapLayer().getObjects().getByType(RectangleObject.class)) {
                Util.createBox2dObject(world, object.getX(), object.getY(), object.getWidth(), object.getHeight(), object.getBodyType(), object.getName(), object.getRotation() * MathUtils.degreesToRadians,object.getUserDate());
            }

        //load lights
//            for (MapPointLight light : map.getLightsMapLayer().getLights().getByType(MapPointLight.class)) {
//                PointLight point = new PointLight(rayHandler, 100, light.getColor(), light.getLightDistance(), light.getX(), light.getY());
//                point.setStaticLight(light.isStaticLight());
//                point.setSoft(light.isSoftLight());
//                point.setSoftnessLength(light.getSoftLength());
//                Filter filter = new Filter();
//                filter.maskBits = CollisionID.MASK_LIGHT;
//                filter.categoryBits = CollisionID.LIGHT;
//                point.setContactFilter(filter);
//                point.setActive(light.isLightEnabled());
//                point.setLightZPosition(light.getLightZ());
//                point.setIntensityForShader(light.getIntensityForShader());
//                point.setLightDistanceForShader(light.getLightFalloffDistance());
//                point.setRenderWithShader(light.isRenderWithShader());
//                map.putLight(light.getName(), point);
//            }
//            for (MapConeLight light :  map.getLightsMapLayer().getLights().getByType(MapConeLight.class)) {
//                ConeLight cone = new ConeLight(rayHandler, 100, light.getColor(), light.getLightDistance(), light.getX(), light.getY(), light.getDirection(), light.getConeDegree());
//                cone.setStaticLight(light.isStaticLight());
//                cone.setSoft(light.isSoftLight());
//                cone.setSoftnessLength(light.getSoftLength());
//                Filter filter = new Filter();
//                filter.maskBits = CollisionID.MASK_LIGHT;
//                filter.categoryBits = CollisionID.LIGHT;
//                cone.setContactFilter(filter);
//                map.putLight(light.getName(), cone);
//            }
    }

    public GameMap getMap() {
        return map;
    }
}
