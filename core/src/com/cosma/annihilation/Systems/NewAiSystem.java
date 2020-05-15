package com.cosma.annihilation.Systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.ai.steer.behaviors.Wander;

public class NewAiSystem extends IteratingSystem {
    public NewAiSystem(Family family) {
        super(family);

    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }
}
