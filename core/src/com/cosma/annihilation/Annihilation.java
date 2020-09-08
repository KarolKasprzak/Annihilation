package com.cosma.annihilation;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.*;
import com.cosma.annihilation.Items.Item;
import com.cosma.annihilation.Items.ItemLoader;
import com.cosma.annihilation.Screens.GameScreen;
import com.cosma.annihilation.Screens.EditorScreen;
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
	private Cursor crosshairCursor;
	private Cursor arrowCursor;
	private boolean startEditor;
	static Label.LabelStyle labelStyle;

	public Annihilation(boolean startEditor) {
		super();
		this.startEditor = startEditor;
		assetLoader = new AssetLoader();
	}

	public static Label.LabelStyle getLabelStyle() {
		return labelStyle;
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

		FreeTypeFontGenerator freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("gfx/fonts/digital.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 12;
		parameter.color = Color.GREEN;
		BitmapFont uiFont = freeTypeFontGenerator.generateFont(parameter);
		labelStyle = new Label.LabelStyle();
		labelStyle.font = uiFont;

		crosshairCursor = Gdx.graphics.newCursor(new Pixmap(Gdx.files.local("gfx/interface/crossCursor.png")), 32, 32);
		arrowCursor = Gdx.graphics.newCursor(new Pixmap(Gdx.files.local("gfx/interface/arrowCursor.png")), 32, 32);
		Gdx.graphics.setCursor(arrowCursor);
		if(startEditor){
			setEditorScreen();
		}else{
			this.setScreen(menuScreen);
		}
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
		EditorScreen editorScreen = new EditorScreen();
		this.setScreen(editorScreen);
	}

	public StartStatus getStartStatus() {
		return startStatus;
	}

	public void setStartStatus(int saveSlot, boolean newGame) {
		this.startStatus.setNewGame(newGame);
		this.startStatus.setSaveSlot(saveSlot);
	}

	public static void setArrowCursor() {
		Gdx.graphics.setCursor(((Annihilation) Gdx.app.getApplicationListener()).arrowCursor);
	}

	public static void setWeaponCursor() {
		Gdx.graphics.setCursor(((Annihilation) Gdx.app.getApplicationListener()).crosshairCursor);
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

	public static Array<Texture> getParallax(String name) {
		return ((Annihilation) Gdx.app.getApplicationListener()).assetLoader.getParallax(name);
	}

	public static  Array<String> getAvailableParallaxNames(){
		return  ((Annihilation) Gdx.app.getApplicationListener()).assetLoader.getAvailableParallaxNames();
	}

	public static String getLocalText(String key) {
		return ((Annihilation) Gdx.app.getApplicationListener()).localization.getText(key);
	}
}

