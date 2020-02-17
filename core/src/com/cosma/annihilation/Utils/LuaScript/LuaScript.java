package com.cosma.annihilation.Utils.LuaScript;

import com.badlogic.gdx.Gdx;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaScript implements Script {
    private Globals globals;
    private LuaValue chunk;
    private boolean scriptFileExists;
    public String scriptFileName;

    public LuaScript(String scriptFileName) {
        this(scriptFileName, JsePlatform.standardGlobals());
    }

    public LuaScript(String scriptFileName, Globals globals){
        this.scriptFileExists = false;
        this.globals = globals;
        this.load(scriptFileName);
    }

    public boolean load(String scriptFileName) {
        this.scriptFileName = scriptFileName;

        if (!Gdx.files.internal(scriptFileName).exists()) {
            this.scriptFileExists = false;
            return false;
        } else {
            this.scriptFileExists = true;
        }

        try {
            chunk = globals.load(Gdx.files.internal(scriptFileName).readString());
        } catch (LuaError e) {
            // If reading the file fails, then log the error to the console
            Gdx.app.log("Debug", "LUA ERROR! " + e.getMessage());
            this.scriptFileExists = false;
            return false;
        }

        // An important step. Calls to script method do not work if the chunk is not called here
        chunk.call();

        return true;
    }

    private boolean executeFunctionParamsAsArray(String functionName, Object[] objects) {
        if (!canExecute()) {
            return false;
        }

        LuaValue luaFunction = globals.get(functionName);

        // Check if a functions with that name exists
        if (luaFunction.isfunction()) {
            LuaValue[] parameters = new LuaValue[objects.length];

            int i = 0;
            for (Object object : objects) {
                // Convert each parameter to a form that's usable by Lua
                parameters[i] = CoerceJavaToLua.coerce(object);
                i++;
            }

            try {
                // Run the function with the converted parameters
                luaFunction.invoke(parameters);
            } catch (LuaError e) {
                // Log the error to the console if failed
                Gdx.app.log("Debug", "LUA ERROR! " + e.getMessage());
                return false;
            }
            return true;
        }
        return false;
    }

    public void registerJavaFunction(TwoArgFunction javaFunction) {
        globals.load(javaFunction);
    }


    @Override
    public boolean canExecute() {
        return scriptFileExists;
    }

    @Override
    public boolean executeInit(Object... objects) {
        return executeFunction("init", objects);
    }

    @Override
    public boolean executeFunction(String functionName, Object... objects) {
        return executeFunctionParamsAsArray(functionName, objects);
    }

    @Override
    public boolean reload() {
        return this.load(this.scriptFileName);
    }
}
