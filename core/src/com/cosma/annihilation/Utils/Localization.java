package com.cosma.annihilation.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

public class Localization {
    private ObjectMap<String,String> stringMap;

    public Localization(String languageCode) {
        stringMap = new ObjectMap<>();
        JsonReader jsonReader = new JsonReader();
        //load all text
        FileHandle path = Gdx.files.local("localization/"+languageCode);
        for(FileHandle file: path.list(".loc")){
            JsonValue jsonValue = jsonReader.parse(file);
            for(JsonValue entry: jsonValue){
               stringMap.put(entry.name,entry.asString());
            }
        }
    }

    public String getText(String key){
        if(!stringMap.containsKey(key)){
            return key + " is not defined ";
        }
        return stringMap.get(key);
    }
}
