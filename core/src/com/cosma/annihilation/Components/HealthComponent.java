package com.cosma.annihilation.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class HealthComponent implements Component {
    public int hp = 100;
    public int maxHP = 100;

    public boolean isDead = false;
    public boolean isHit = false;
    public Vector2 attackerPosition;

}
