package com.cosma.annihilation.Ai;


import com.badlogic.ashley.core.Entity;

public interface ArtificialIntelligence {
    void update(Entity entity);
    String getStatus();
}
