package com.cosma.annihilation.desktop;


import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import com.cosma.annihilation.Annihilation;

import java.awt.*;


public class DesktopLauncher {
    public static void main(String[] arg) {

//        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
//        ApplicationListener appListener = new Annihilation(false);
//        Canvas canvas = new Canvas();
//        new LwjglApplication(appListener, config, canvas);
//
//        Frame frame = new Frame();
//        frame.add(canvas);
////        frame.setExtendedState(JFrame.);
//        frame.setUndecorated(true);
//
//        frame.setAlwaysOnTop(false);
//
//
////        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
////        DisplayMode dispMode = device.getDisplayMode();
//////        device.setFullScreenWindow(frame);
////        device.setDisplayMode(dispMode);
//        frame.setVisible(true);

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
        config.title = "Annihilation";
//        config.resizable = false;
		config.fullscreen = true;
        config.vSyncEnabled = true;
        config.pauseWhenBackground = true;
//        config.samples = 3;
      config.width = screenSize.width;
       config.height = screenSize.height;


        new LwjglApplication(new Annihilation(false), config);
    }
}
