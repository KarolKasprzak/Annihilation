package com.cosma.annihilation.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cosma.annihilation.Annihilation;

import java.awt.*;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		config.title = "Annihilation";
		config.resizable = false;
		config.fullscreen = false;
		config.width = screenSize.width /2;
		config.height = screenSize.height /2;

		new LwjglApplication (new Annihilation(),config);

	}
}
