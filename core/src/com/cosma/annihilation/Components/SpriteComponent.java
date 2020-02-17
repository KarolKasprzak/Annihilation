package com.cosma.annihilation.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Pool;

public class SpriteComponent implements Component,Pool.Poolable {
    public float time = 0;
    public float lifeTime = 0;
    public boolean isLifeTimeLimited = false;
    public boolean flipX = false;
    public boolean flipY = false;
    public float x;
    public float y;
    public float angle = 0;
    public Texture texture;

    @Override
    public void reset() {
        time = 0;
        flipX = false;
        flipY = false;
    }
}
