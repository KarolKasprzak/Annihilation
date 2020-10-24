package com.cosma.annihilation.Utils.Serialization;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Components.*;
import com.cosma.annihilation.EntityEngine.core.Component;
import com.cosma.annihilation.EntityEngine.core.Engine;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.Items.Item;
import com.cosma.annihilation.Utils.Dialogs.DialogueManager;
import com.cosma.annihilation.Utils.Util;

import java.util.HashMap;

public class GameEntitySerializer implements Json.Serializer<Entity>  {

    private Json loadJason;
    private HashMap<String, FileHandle> jsonList;
    private DialogueManager dialogueManager;

    public GameEntitySerializer(World world, Engine engine) {

        loadJason = new Json();
        loadJason.setSerializer(Entity.class, new EntityReader(world,engine));

        dialogueManager = new DialogueManager();

        //Load all entity
        jsonList = new HashMap<>();
        FileHandle file = Gdx.files.local("entity");
        for (FileHandle rootDirectory : file.list()) {
            if (rootDirectory.isDirectory()) {
                for (FileHandle childrenDirectory : rootDirectory.list(".json")) {
                    jsonList.put(childrenDirectory.name(), childrenDirectory);
                }
            }
        }
    }


    @Override
    public void write(Json json, Entity object, Class knownType) {
        json.writeObjectStart();
         for (Component component : object.getComponents()) {
             if (component instanceof HealthComponent) {
                 json.writeValue("hp", ((HealthComponent) component).hp);
                 json.writeValue("maxHp", ((HealthComponent) component).maxHP);
                 continue;
             }

             if (component instanceof GateComponent) {
                 json.writeValue("gateName", ((GateComponent) component).gateName);
                 json.writeValue("moveDistance", ((GateComponent) component).moveDistance);
                 json.writeValue("isOpen", ((GateComponent) component).isOpen);
                 continue;
             }

             if (component instanceof ParallaxComponent) {
                 json.writeValue("parallaxName", ((ParallaxComponent) component).parallaxName);
                 json.writeValue("displayH", ((ParallaxComponent) component).displayH);
                 json.writeValue("displayW", ((ParallaxComponent) component).displayW);
                 json.writeValue("widthMultiplier", ((ParallaxComponent) component).widthMultiplier);
                 continue;
             }

             if (component instanceof AiComponent) {
                 json.writeValue("startPosition", ((AiComponent) component).startPosition.x+","+((AiComponent) component).startPosition.y);
                 continue;
             }

             if (component instanceof SerializationComponent) {
                 json.writeValue("entityName", ((SerializationComponent) component).entityName);
                 continue;
             }

             if (component instanceof PlayerComponent) {
                 json.writeValue("mapName", ((PlayerComponent) component).mapName);
                 continue;
             }

             if (component instanceof AnimationComponent) {
                 json.writeValue("id",((AnimationComponent) component).animationId.name());
                 continue;
             }

             if (component instanceof ContainerComponent) {
                 saveItemArray(json,((ContainerComponent) component).itemList,"itemList");
                 continue;
             }

             if (component instanceof PlayerInventoryComponent) {
                 saveItemArray(json,((PlayerInventoryComponent) component).inventoryItems,"inventoryItems");
                 if(((PlayerInventoryComponent) component).equippedWeapon != null){
                     savePlayerItem(json,((PlayerInventoryComponent) component).equippedWeapon,"equippedWeapon");
                 }
                 if(((PlayerInventoryComponent) component).equippedArmour != null){
                     savePlayerItem(json,((PlayerInventoryComponent) component).equippedArmour,"equippedArmour");
                 }
                 continue;
             }

             if (component instanceof PhysicsComponent) {
                 json.writeValue("position",(((PhysicsComponent) component).body.getPosition().x)+","+((PhysicsComponent) component).body.getPosition().y);
                 continue;
             }

             if (component instanceof MapChangeComponent) {
                 json.writeValue("targetMapPath", ((MapChangeComponent) component).targetMapPath);
                 json.writeValue("targetPosition", ((MapChangeComponent) component).playerPositionOnTargetMap.x+","+((MapChangeComponent) component).playerPositionOnTargetMap.y);
             }

             if (component instanceof ActionComponent) {
                 if(((ActionComponent) component).actionTargetName != null){
                     json.writeValue("targetName", ((ActionComponent) component).actionTargetName);
                 }
                 if(((ActionComponent) component).actionTargetPosition != null){
                     json.writeValue("actionTargetX", ((ActionComponent) component).actionTargetPosition.x);
                     json.writeValue("actionTargetY", ((ActionComponent) component).actionTargetPosition.y);
                 }
             }
         }
         json.writeObjectEnd();
    }

    @Override
    public Entity read(Json json, JsonValue jsonData, Class type) {

        Entity entity = loadJason.fromJson(Entity.class, jsonList.get(jsonData.get("entityName").asString()));
        for(Component component: entity.getComponents()){

            if(component instanceof ParallaxComponent){
                ((ParallaxComponent) component).parallaxName = jsonData.get("parallaxName").asString();
                ((ParallaxComponent) component).textures = Annihilation.getParallax(((ParallaxComponent) component).parallaxName);
                ((ParallaxComponent) component).displayH = jsonData.get("displayH").asFloat();
                ((ParallaxComponent) component).displayW = jsonData.get("displayW").asFloat();
                ((ParallaxComponent) component).widthMultiplier = jsonData.get("widthMultiplier").asInt();
            }

            if(component instanceof PhysicsComponent){
                ((PhysicsComponent) component).body.setTransform(Util.jsonStringToVector2(jsonData.get("position").asString()),0);
                continue;
            }
            //TEST!! *TODO
            if(component instanceof AiComponent){
                if(jsonData.has("startPosition")){
                    ((AiComponent) component).startPosition = Util.jsonStringToVector2(jsonData.get("startPosition").asString());
                }
            }


            if(component instanceof ContainerComponent){
                if(jsonData.has("itemList")){
                    ((ContainerComponent) component).itemList = loadItemArray(jsonData,"itemList");
                }
            }

            if(component instanceof  GateComponent){
                if(jsonData.has("isOpen")){
                    ((GateComponent) component).isOpen = jsonData.get("isOpen").asBoolean();
                }
                if(jsonData.has("moveDistance")){
                    ((GateComponent) component).moveDistance = jsonData.get("moveDistance").asInt();
                }
                if(jsonData.has("gateName")){
                    ((GateComponent) component).gateName = jsonData.get("gateName").asString();
                }
            }

            if(component instanceof PlayerInventoryComponent){
                if(jsonData.has("inventoryItems")){
                    ((PlayerInventoryComponent) component).inventoryItems = loadItemArray(jsonData,"inventoryItems");
                }

                if(jsonData.has("equippedWeapon")){
                    ((PlayerInventoryComponent) component).equippedWeapon = loadItemArray(jsonData,"equippedWeapon").first();
                }
                if(jsonData.has("equippedArmour")){
                    ((PlayerInventoryComponent) component).equippedWeapon = loadItemArray(jsonData,"equippedArmour").first();
                }
            }

            if(component instanceof PlayerComponent){
                if(jsonData.has("mapName")){
                    ((PlayerComponent) component).mapName = jsonData.get("mapName").asString();
                }
            }

            if(component instanceof DialogueComponent){
                    ((DialogueComponent) component).dialog = dialogueManager.getDialogue(((DialogueComponent) component).dialogId);
            }

            if(component instanceof MapChangeComponent){
                if(jsonData.has("targetPosition")){
                    ((MapChangeComponent) component).playerPositionOnTargetMap = Util.jsonStringToVector2(jsonData.get("targetPosition").asString());
                }
                if(jsonData.has("targetMapPath")){

                    ((MapChangeComponent) component).targetMapPath = jsonData.get("targetMapPath").asString();
                }
            }

            if(component instanceof HealthComponent){
                if(jsonData.has("hp")){
                    ((HealthComponent) component).hp = jsonData.get("hp").asInt();
                }
                if(jsonData.has("maxHp")){
                    ((HealthComponent) component).maxHP = jsonData.get("maxHp").asInt();
                }
            }
            if(component instanceof ActionComponent){
                if(jsonData.has("targetName")){
                    ((ActionComponent) component).actionTargetName = jsonData.get("targetName").asString();
                }
                if(jsonData.has("actionTargetX")){
                    ((ActionComponent) component).actionTargetPosition = new Vector2(
                            jsonData.get("actionTargetX").asFloat(),jsonData.get("actionTargetY").asFloat()
                    );
                }
            }
        }
        return entity;
    }

    private void savePlayerItem(Json json, Item item, String valueName){
        json.writeArrayStart(valueName);
        item.write(json);
        json.writeArrayEnd();
    }

    private void saveItemArray(Json json, Array<Item> itemsArray,String arrayName){
            json.writeArrayStart(arrayName);
            for(Item item: itemsArray){
                item.write(json);
            }
            json.writeArrayEnd();
    }
    private Array<Item> loadItemArray(JsonValue arrayValue,String arrayName){
        Array<Item> array = new Array<>();
        for (JsonValue value : arrayValue.get(arrayName)){
            Item item = Annihilation.getItem(value.get("itemID").asString());
            item.setTableIndex(value.get("tableIndex").asInt());
            if(value.has("itemAmount")){
                item.setItemAmount(value.get("itemAmount").asInt());
            }
            if(value.has("ammoInClip")){
                item.setAmmoInClip(value.get("ammoInClip").asInt());
            }
            array.add(item);
        }
        return array;
    }
}
