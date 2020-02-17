package com.cosma.annihilation.Utils.LuaScript;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ObjectMap;

import javax.swing.text.html.parser.Entity;

public class ScriptManager {
    private ObjectMap<String,LuaScript> scripts;

    public ScriptManager(Engine engine, World world) {
        scripts = new ObjectMap<>();
        FileHandle scriptsPath = Gdx.files.local("scripts/");
        for(FileHandle script: scriptsPath.list(".lua")){
            scripts.put(script.nameWithoutExtension(),new LuaScript(script.path()));
            System.out.println(script.nameWithoutExtension());
        }
    }

    public void runScript(String key, Entity entity){
                scripts.get(key).executeFunction("script",entity);
    }

    public void runScript(String key){
        System.out.println();
        scripts.get(key).executeFunction("script",this);
    }

}
