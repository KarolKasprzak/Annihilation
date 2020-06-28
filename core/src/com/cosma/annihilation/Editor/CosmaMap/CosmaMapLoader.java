package com.cosma.annihilation.Editor.CosmaMap;

import com.badlogic.gdx.graphics.Color;
import com.cosma.annihilation.Box2dLight.ConeLight;
import com.cosma.annihilation.Box2dLight.DirectionalLight;
import com.cosma.annihilation.Box2dLight.PointLight;
import com.cosma.annihilation.Box2dLight.RayHandler;
import com.cosma.annihilation.EntityEngine.core.Engine;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Json;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorLights.MapConeLight;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorLights.MapPointLight;
import com.cosma.annihilation.Editor.CosmaMap.CosmaEditorObject.RectangleObject;
import com.cosma.annihilation.Utils.CollisionID;
import com.cosma.annihilation.Utils.Serialization.GameEntitySerializer;
import com.cosma.annihilation.Utils.Util;

import java.awt.*;


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

//        rayHandler.setAmbientLight(map.getLightsMapLayer().getAmbientLightColor());
//        rayHandler.setAmbientLight(map.getLightsMapLayer().getAmbientLightIntensity());
        createMapObject();
    }

    private void createMapObject(){

        //load objects
            for (RectangleObject object : map.getObjectMapLayer().getObjects().getByType(RectangleObject.class)) {
                Util.createBox2dObject(world, object.getX(), object.getY(), object.getWidth(), object.getHeight(), object.getBodyType(), object.getName(), object.getRotation() * MathUtils.degreesToRadians,object.getUserDate());
            }

        //load lights
            for (MapPointLight light : map.getLightsMapLayer().getLights().getByType(MapPointLight.class)) {
                PointLight point = new PointLight(rayHandler, light.getRaysNumber(), light.getColor(), light.getLightDistance(), light.getX(), light.getY());
                point.setStaticLight(light.isStaticLight());
                point.setSoft(light.isSoftLight());
                point.setSoftnessLength(light.getSoftLength());
                Filter filter = new Filter();
                filter.maskBits = CollisionID.MASK_LIGHT;
                filter.categoryBits = CollisionID.LIGHT;
                point.setContactFilter(filter);
                map.putLight(light.getName(), point);
            }
            for (MapConeLight light :  map.getLightsMapLayer().getLights().getByType(MapConeLight.class)) {
                ConeLight cone = new ConeLight(rayHandler, light.getRaysNumber(), light.getColor(), light.getLightDistance(), light.getX(), light.getY(), light.getDirection(), light.getConeDegree());
                cone.setStaticLight(light.isStaticLight());
                cone.setSoft(light.isSoftLight());
                cone.setSoftnessLength(light.getSoftLength());
                Filter filter = new Filter();
                filter.maskBits = CollisionID.MASK_LIGHT;
                filter.categoryBits = CollisionID.LIGHT;
                cone.setContactFilter(filter);
                map.putLight(light.getName(), cone);
            }
    }

    public GameMap getMap() {
        return map;
    }
}
