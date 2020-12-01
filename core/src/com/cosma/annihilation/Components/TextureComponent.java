package com.cosma.annihilation.Components;

import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.EntityEngine.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureComponent implements Component {
    public Texture normalTexture;
    public TextureRegion textureRegion;
    public boolean flipTexture = false;
    public boolean direction = false;

    /**Use this when create texture component in runtime */
    public void setTextureRegion(TextureRegion textureRegion){
        this.textureRegion = textureRegion;
        String path = ((FileTextureData) textureRegion.getTexture().getTextureData()).getFileHandle().pathWithoutExtension();
        normalTexture = Annihilation.getAssets().get(path+"_n.png");
        normalTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

}
