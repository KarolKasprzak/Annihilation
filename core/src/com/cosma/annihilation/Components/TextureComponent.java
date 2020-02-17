package com.cosma.annihilation.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class TextureComponent implements Component {
    public String texturePatch;
    public Texture texture;
    public TextureRegion textureRegion;
    public int renderOrder = 3;
    public  boolean renderAfterLight = false;
    public boolean flipTexture = false;
    public boolean direction = false;
    public boolean renderWithShader = false;
}
