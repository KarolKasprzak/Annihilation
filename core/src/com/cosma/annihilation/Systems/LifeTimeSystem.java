package com.cosma.annihilation.Systems;

import com.cosma.annihilation.Components.*;
import com.cosma.annihilation.EntityEngine.core.ComponentMapper;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.EntityEngine.core.Family;
import com.cosma.annihilation.EntityEngine.systems.IteratingSystem;
import com.cosma.annihilation.Utils.Constants;


public class LifeTimeSystem extends IteratingSystem{


    private ComponentMapper<LifeTimeComponent> lifeTimeMapper;



    public LifeTimeSystem() {
        super(Family.all(LifeTimeComponent.class).get(), Constants.HEALTH_SYSTEM);

        lifeTimeMapper = ComponentMapper.getFor(LifeTimeComponent.class);
    }


    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        LifeTimeComponent lifeTimeComponent = lifeTimeMapper.get(entity);
        lifeTimeComponent.currentLifeTime += deltaTime;
        if(lifeTimeComponent.maxLifeTime < lifeTimeComponent.currentLifeTime){
          this.getEngine().removeEntity(entity);
        }
    }
}
