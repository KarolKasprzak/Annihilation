package com.cosma.annihilation.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector3;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Systems.PlayerControlSystem;
import com.cosma.annihilation.Utils.AssetLoader;
import com.cosma.annihilation.World.WorldBuilder;

public class GameScreen implements Screen, InputProcessor {

    private WorldBuilder worldBuilder;
    private InputMultiplexer im;
    private AssetLoader assetLoader;

    public GameScreen(Annihilation game, AssetLoader assetLoader) {
        this.assetLoader = assetLoader;
        im = new InputMultiplexer();
        im.addProcessor(this);
        worldBuilder = new WorldBuilder(game.isGameLoaded(),im);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(im);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        worldBuilder.update(delta);
        }

    @Override
    public void resize(int width, int height) {
        worldBuilder.resize(width,height);
    }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//       Vector3 worldCoordinates = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
//       Vector3 vec = worldBuilder.getCamera().unproject(worldCoordinates);
//

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
