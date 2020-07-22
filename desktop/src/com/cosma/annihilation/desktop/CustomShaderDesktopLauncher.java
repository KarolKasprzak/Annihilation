package com.cosma.annihilation.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cosma.annihilation.Box2dLightCustomShaderTest;

import java.awt.*;

public class CustomShaderDesktopLauncher {

    public static void main(String[] argv) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        config.title = "box2d lights test";
        config.width = screenSize.width/2;
        config.height = screenSize.height/2;
        config.samples = 4;

        config.vSyncEnabled = true;

        config.fullscreen = true;
        new LwjglApplication(new Box2dLightCustomShaderTest(), config);
    }

}
