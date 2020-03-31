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
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Components.*;
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
                 savePlayerItem(json,((PlayerInventoryComponent) component).equippedWeapon,"equippedWeapon");
                 savePlayerItem(json,((PlayerInventoryComponent) component).equippedArmour,"equippedArmour");
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
                    ((ContainerComponent) component).itemList = loadItemArray(jsonData,"itemList");

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
