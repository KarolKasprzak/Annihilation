package com.cosma.annihilation.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class BulletComponent implements Component, Pool.Poolable {

    public int dmg = 0;
    public boolean isBulletHit = false;

    @Override
    public void reset() {
    dmg = 0;
    isBulletHit = false;
    }
}
