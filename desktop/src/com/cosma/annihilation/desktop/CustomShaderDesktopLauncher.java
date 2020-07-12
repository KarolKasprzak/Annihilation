package com.cosma.annihilation.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cosma.annihilation.Box2dLightCustomShaderTest;

public class CustomShaderDesktopLauncher {

    public static void main(String[] argv) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "box2d lights test";
        config.width = 800;
        config.height = 480;
        config.samples = 4;

        config.vSyncEnabled = true;

        config.fullscreen = true;
        new LwjglApplication(new Box2dLightCustomShaderTest(), config);
    }

}
