package com.cosma.annihilation.Components;


import com.cosma.annihilation.EntityEngine.core.Component;

public class BulletComponent implements Component {
    public float targetX;
    public float targetY;
    public int dmg = 0;
    public boolean isBulletHit = false;
}


