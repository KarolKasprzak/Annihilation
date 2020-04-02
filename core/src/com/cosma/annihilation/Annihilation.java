package com.cosma.annihilation;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.*;
import com.cosma.annihilation.Items.Item;
import com.cosma.annihilation.Items.ItemLoader;
import com.cosma.annihilation.Screens.GameScreen;
import com.cosma.annihilation.Screens.MapEditor;
import com.cosma.annihilation.Screens.MenuScreen;
import com.cosma.annihilation.Utils.AssetLoader;
import com.cosma.annihilation.Utils.Localization;
import com.cosma.annihilation.Utils.StartStatus;

public class Annihilation extends Game {

	private AssetLoader assetLoader;
	private Localization localization;
	private MenuScreen menuScreen;
	private GameScreen gameScreen;
	private StartStatus startStatus;
	private ItemLoader itemLoader;

	public Annihilation() {
		super();
		assetLoader = new AssetLoader();
	}

	@Override
	public void create() {
		startStatus = new StartStatus(1, true);
		JsonReader jsonReader = new JsonReader();
		JsonValue jsonValue = jsonReader.parse(Gdx.files.local("settings.json"));
		localization = new Localization(jsonValue.getString("language"));
		assetLoader.load();
		itemLoader = new ItemLoader();
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

	public StartStatus getStartStatus() {
		return startStatus;
	}

	public void setStartStatus(int saveSlot, boolean newGame) {
		this.startStatus.setNewGame(newGame);
		this.startStatus.setSaveSlot(saveSlot);
	}

	public static Item getItem(String itemID) {
		return ((Annihilation) Gdx.app.getApplicationListener()).itemLoader.getItem(itemID);
	}
	public static Array<String> getItemIdList() {
		return ((Annihilation) Gdx.app.getApplicationListener()).itemLoader.getItemIdList();
	}

	public static AssetManager getAssets() {
		return ((Annihilation) Gdx.app.getApplicationListener()).assetLoader.manager;
	}

	public static String getLocalText(String key) {
		return ((Annihilation) Gdx.app.getApplicationListener()).localization.getText(key);
	}
}

