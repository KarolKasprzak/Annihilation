package com.cosma.annihilation.Components;

import com.cosma.annihilation.EntityEngine.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;

public class ParticleComponent implements Component {
    public ParticleEffect particleEffect = new ParticleEffect();


    public void loadDate(String fileName){
        particleEffect.load(Gdx.files.internal("gfx/particles/"+fileName),(Gdx.files.internal("gfx/particles")));
        particleEffect.scaleEffect(0.1f);
    }
}
