package com.cosma.annihilation.Components;

import com.cosma.annihilation.EntityEngine.core.Component;
import com.badlogic.gdx.math.Vector2;

public class HealthComponent implements Component {
    public int hp = 100;
    public int maxHP = 100;

    public boolean isDead = false;
    public boolean isHit = false;
    public Vector2 attackerPosition;

    public void decreaseHp(int value){
        hp = hp - value;
    }

    public void increaseHp(int value){
        hp = hp + value;
    }

    public void hit(Vector2 attackerPosition){
        isHit = true;
        this.attackerPosition = attackerPosition;
    }

}
