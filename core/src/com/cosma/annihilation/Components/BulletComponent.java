package com.cosma.annihilation.Components;

import com.cosma.annihilation.EntityEngine.core.Component;
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
