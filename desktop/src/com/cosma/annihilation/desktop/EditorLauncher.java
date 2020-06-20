package com.cosma.annihilation.desktop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cosma.annihilation.Annihilation;

import java.awt.*;

public class EditorLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		config.title = "Cosma Map Editor";
		config.resizable = false;
		config.samples=3;
		config.width = screenSize.width;
		config.height = screenSize.height/2;
		config.vSyncEnabled = true;

		new LwjglApplication (new Annihilation(true),config);
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
	}
}
