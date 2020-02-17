package com.cosma.annihilation.Utils.Serialization;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.cosma.annihilation.Components.*;
import com.cosma.annihilation.Gui.Inventory.InventoryItemLocation;
import com.cosma.annihilation.Utils.Dialogs.DialogueManager;
import com.cosma.annihilation.Utils.Util;

import java.util.HashMap;

public class GameEntitySerializer implements Json.Serializer<Entity>  {

    private Json loadJason;
    private HashMap<String, FileHandle> jsonList;
    private DialogueManager dialogueManager;

    public GameEntitySerializer(World world, Engine engine) {

        loadJason = new Json();
        loadJason.setSerializer(Entity.class, new EntitySerializer(world,engine));

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
                 json.writeArrayStart("itemList");
                 for (InventoryItemLocation location : ((ContainerComponent) component).itemLocations) {
                      saveItems(json,location);
                 }
                 json.writeArrayEnd();
                 continue;
             }

             if (component instanceof PlayerInventoryComponent) {
                 json.writeArrayStart("inventoryItem");
                 for (InventoryItemLocation location : ((PlayerInventoryComponent) component).inventoryItem) {
                  saveItems(json,location);
                 }
                 json.writeArrayEnd();
                 json.writeArrayStart("equippedItem");
                 for (InventoryItemLocation location : ((PlayerInventoryComponent) component).equippedItem) {
                     saveItems(json,location);
                 }
                 json.writeArrayEnd();
                 continue;
             }

             if (component instanceof BodyComponent) {
                 json.writeValue("position",(((BodyComponent) component).body.getPosition().x)+","+((BodyComponent) component).body.getPosition().y);
                 continue;
             }

             if (component instanceof GateComponent) {
                 json.writeValue("targetMapPath", ((GateComponent) component).targetMapPath);
                 json.writeValue("targetPosition", ((GateComponent) component).playerPositionOnTargetMap.x+","+((GateComponent) component).playerPositionOnTargetMap.y);
             }
         }
         json.writeObjectEnd();
    }

    @Override
    public Entity read(Json json, JsonValue jsonData, Class type) {

        Entity entity = loadJason.fromJson(Entity.class, jsonList.get(jsonData.get("entityName").asString()));
        for(Component component: entity.getComponents()){
            if(component instanceof BodyComponent){
                ((BodyComponent) component).body.setTransform(Util.jsonStringToVector2(jsonData.get("position").asString()),0);
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
                    ((ContainerComponent) component).itemLocations = new Array<>();
                    for (JsonValue value : jsonData.get("itemList")){
                        InventoryItemLocation location = new InventoryItemLocation();
                        location.setTableIndex(value.get("tableIndex").asInt());
                        location.setItemID(value.get("itemID").asString());
                        location.setItemsAmount(value.get("itemsAmount").asInt());
                        ((ContainerComponent) component).itemLocations.add(location);
                    }
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

            if(component instanceof GateComponent){
                if(jsonData.has("targetPosition")){
                    ((GateComponent) component).playerPositionOnTargetMap = Util.jsonStringToVector2(jsonData.get("targetPosition").asString());
                }
                if(jsonData.has("targetMapPath")){

                    ((GateComponent) component).targetMapPath = jsonData.get("targetMapPath").asString();
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
        }
        return entity;
    }
    static void saveItems(Json json,InventoryItemLocation location){
        json.writeObjectStart();
        json.writeValue("tableIndex", location.getTableIndex());
        json.writeValue("itemID", location.getItemID());
        json.writeValue("itemsAmount", location.getItemsAmount());
        json.writeObjectEnd();
    }
}
