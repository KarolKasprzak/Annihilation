package com.cosma.annihilation.desktop;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import com.cosma.annihilation.Annihilation;

import javax.swing.*;
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
        config.resizable = false;
//		config.fullscreen = true;
        config.vSyncEnabled = true;
        config.samples = 0;
        config.width = screenSize.width/2;
        config.height = screenSize.height/2;

        new LwjglApplication(new Annihilation(false), config);
    }
}
