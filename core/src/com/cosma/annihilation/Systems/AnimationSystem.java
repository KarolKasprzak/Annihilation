package com.cosma.annihilation.Systems;

import com.cosma.annihilation.Components.*;
import com.cosma.annihilation.EntityEngine.core.ComponentMapper;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.EntityEngine.core.Family;
import com.cosma.annihilation.EntityEngine.systems.IteratingSystem;
import com.cosma.annihilation.Utils.*;

public class AnimationSystem extends IteratingSystem {

    private ComponentMapper<TextureComponent> textureMapper;
    private ComponentMapper<AnimationComponent> animationMapper;

    public AnimationSystem() {
        super(Family.all(TextureComponent.class, AnimationComponent.class).get(), Constants.ANIMATION);
        textureMapper = ComponentMapper.getFor(TextureComponent.class);
        animationMapper = ComponentMapper.getFor(AnimationComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TextureComponent textureComponent = textureMapper.get(entity);
        AnimationComponent animationComponent = animationMapper.get(entity);

        textureComponent.flipTexture = !animationComponent.spriteDirection;

        animationComponent.time += deltaTime;
        animationComponent.currentAnimation = animationComponent.animationMap.get(animationComponent.animationState.toString());

        if (animationMapper.get(entity).currentAnimation != null) {
            textureComponent.textureRegion = animationComponent.currentAnimation.getKeyFrame(animationComponent.time);
        }
    }
}
