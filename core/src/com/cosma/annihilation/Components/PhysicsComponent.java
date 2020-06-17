package com.cosma.annihilation.Components;

import com.cosma.annihilation.EntityEngine.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool;


public class PhysicsComponent implements Component, Pool.Poolable {
    public Body body;
    public float height = 0;
    public float width = 0;

    @Override
    public void reset() {
        Body body;
    }


}

