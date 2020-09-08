package com.cosma.annihilation.Components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.cosma.annihilation.EntityEngine.core.Component;

public class ParallaxComponent implements Component {
    public String parallaxName;
    public Array<Texture> textures;
    public float scroll = 0;

    //display size
    public int widthMultiplier = 1;
    public float displayW;
    public float displayH;
    float speed;

}
