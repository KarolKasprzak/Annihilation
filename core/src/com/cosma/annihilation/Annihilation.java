package com.cosma.annihilation;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;
import com.cosma.annihilation.Screens.GameScreen;
import com.cosma.annihilation.Screens.MapEditor;
import com.cosma.annihilation.Screens.MenuScreen;
import com.cosma.annihilation.Utils.AssetLoader;

import java.util.Locale;

public class Annihilation extends Game {

	private AssetLoader assetLoader;
	private I18NBundle myBundle;
	private MenuScreen menuScreen;
	private GameScreen gameScreen;
	private Boolean isGameLoaded;

	public Annihilation() {
		super();
		assetLoader = new AssetLoader();
	}

	@Override
	public void create() {
		isGameLoaded = false;
		assetLoader.load();
		FileHandle mapTextures = Gdx.files.local("locale/loc");
		myBundle = I18NBundle.createBundle(mapTextures,Locale.UK);
		menuScreen = new MenuScreen(this);
		this.setScreen(menuScreen);
	}

	@Override
	public void dispose() {
		assetLoader.dispose();
		menuScreen.dispose();
		if(gameScreen != null){
			gameScreen.dispose();
		}
	}

	public void setGameScreen() {
		gameScreen = new GameScreen(this, assetLoader);
		this.setScreen(gameScreen);
	}

	public void setEditorScreen() {
		MapEditor mapEditor = new MapEditor(this);
		this.setScreen(mapEditor);
	}

	public void setGameState(Boolean gameLoaded) {
		isGameLoaded = gameLoaded;
	}

	public Boolean isGameLoaded() {
		return isGameLoaded;
	}

	public static AssetManager getAssets() {
		return ((Annihilation) Gdx.app.getApplicationListener()).assetLoader.manager;
	}

	public static AssetLoader getAssetsLoader() {
		return ((Annihilation) Gdx.app.getApplicationListener()).assetLoader;
	}

	public static AssetManager getAssets(String patch) {
		return ((Annihilation) Gdx.app.getApplicationListener()).assetLoader.manager.get(patch);
	}

	public static String getLocal(String key) {
		return ((Annihilation) Gdx.app.getApplicationListener()).myBundle.get(key);
	}

	public static String getLocal(String key, Object... args) {
		return ((Annihilation) Gdx.app.getApplicationListener()).myBundle.format(key,args);
	}
}

