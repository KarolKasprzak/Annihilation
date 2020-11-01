package com.cosma.annihilation.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;


public class AssetLoader {
    public AssetManager manager;
    private ObjectMap<String, Array<Texture>> parallaxMap;
    private Array<TextureAtlas> atlasList;

    public AssetLoader() {

        manager = new AssetManager();
        atlasList = new Array<>();
    }


    public void load() {

        loadFonts();

        //Load parallax textures
        parallaxMap = new ObjectMap<>();
        FileHandle parallaxTextures = Gdx.files.local("gfx/parallax/");
        for (FileHandle file : parallaxTextures.list()) {
            if (file.isDirectory()) {
                Array<Texture> textureArray = new Array<>();
                for (FileHandle textureFile : file.list(".png")) {
                    Texture texture = new Texture(textureFile);
                    textureArray.add(texture);
                }

                for (int i = 0; i < textureArray.size; i++) {
                    textureArray.get(i).setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.ClampToEdge);
                }

                parallaxMap.put(file.name(), textureArray);
            }
        }

        //Load map tile textures
        FileHandle mapTextures = Gdx.files.local("map/map_tiles/");
        for (FileHandle texture : mapTextures.list(".atlas")) {
            manager.load(texture.path(), TextureAtlas.class);
        }
        //Load map textures
        FileHandle gfxMapAtlas = Gdx.files.local("gfx/map_textures/");
        for (FileHandle file : gfxMapAtlas.list()) {
            if (file.isDirectory()) {
                for (FileHandle texture : file.list(".atlas")) {
                    manager.load(texture.path(), TextureAtlas.class);
                }
                for (FileHandle texture : file.list(".png")) {
                    if (texture.nameWithoutExtension().contains("_n")) {
                        manager.load(texture.path(), Texture.class);
                    }
                }
            }
        }

        //Load icon
        FileHandle iconTextures = Gdx.files.local("gfx/textures/icon/");
        for (FileHandle texture : iconTextures.list(".png")) {
            manager.load(texture.path(), Texture.class);
        }
        //Load interface textures
        FileHandle interfaceTextures = Gdx.files.local("gfx/interface/");
        for (FileHandle texture : interfaceTextures.list(".png")) {
            manager.load(texture.path(), Texture.class);

        }
        //Load textures
        FileHandle textures = Gdx.files.local("gfx/textures/");
        for (FileHandle texture : textures.list(".png")) {
            manager.load(texture.path(), Texture.class);
        }
        //Load texture atlas
        FileHandle gfxAtlas = Gdx.files.local("gfx/atlas/");
        for (FileHandle file : gfxAtlas.list()) {
            if (file.isDirectory()) {
                for (FileHandle texture : file.list(".atlas")) {
                    manager.load(texture.path(), TextureAtlas.class);
                }
            }
        }
        for (FileHandle texture : gfxAtlas.list(".png")) {
            if (texture.nameWithoutExtension().contains("_n")) {
                manager.load(texture.path(), Texture.class);
            }
        }

        FileHandle skeletonAtlas = Gdx.files.local("gfx/skeletons/");
        for (FileHandle file : skeletonAtlas.list()) {
            if (file.isDirectory()) {
                for (FileHandle texture : file.list(".atlas")) {
                    manager.load(texture.path(), TextureAtlas.class);
                }
            }
        }

        for (FileHandle texture : gfxAtlas.list(".atlas")) {
            manager.load(texture.path(), TextureAtlas.class);
        }

        //Load player textures
        FileHandle playerAtlas = Gdx.files.local("gfx/player/");
        for (FileHandle texture : playerAtlas.list(".atlas")) {
            manager.load(texture.path(), TextureAtlas.class);
        }

        //Load sfx
        FileHandle sounds = Gdx.files.local("sfx/");
        for (FileHandle file : sounds.list()) {
            if (file.isDirectory()) {
                for (FileHandle sound : file.list(".wav")) {
                    manager.load(sound.path(), Sound.class);
                }
            }
        }


        for (FileHandle texture : playerAtlas.list(".png")) {
            manager.load(texture.path(), Texture.class);
        }
        //Load locale files
        FileHandle locale = Gdx.files.internal("locale");
        for (FileHandle local : locale.list()) {
            manager.load(local.pathWithoutExtension(), I18NBundle.class);
        }
        //Load skin files
        manager.load("gfx/interface/skin/skin.json", Skin.class);

        manager.finishLoading();

        manager.getAll(TextureAtlas.class, atlasList);
        System.out.println("Loaded!");
    }

    public TextureRegion getTextureRegion(String atlasName, String regionName) {
        for (TextureAtlas textureAtlas : atlasList) {
            if (atlasName.equals(((FileTextureData) textureAtlas.getTextures().first().getTextureData()).getFileHandle().nameWithoutExtension())) {
                return textureAtlas.findRegion(regionName);
            }
        }
        return null;
    }

    private void loadFonts() {
        manager.setLoader(BitmapFont.class, new BitmapFontLoader(new InternalFileHandleResolver()));
    }

    public Array<Texture> getParallax(String name) {
        if (parallaxMap.containsKey(name)) {
            return parallaxMap.get(name);
        } else {
            return null;
        }
    }

    public Array<String> getAvailableParallaxNames() {
        return parallaxMap.keys().toArray();
    }

    public void dispose() {
        manager.dispose();
    }
}
