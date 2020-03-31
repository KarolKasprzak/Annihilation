package com.cosma.annihilation.Gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.cosma.annihilation.Annihilation;

public abstract class GuiWindow extends Window {


    public GuiWindow(String title, Skin skin) {
        super(title, skin);
    }

    public void setBackgroundTexture(String texturePath) {
        this.background(new TextureRegionDrawable(new TextureRegion(Annihilation.getAssets().get(texturePath, Texture.class))));
    }

    public float getSizeRatio(){
        return  Gdx.graphics.getHeight()/getBackground().getMinHeight();
    }

    /** use after setting background**/
   public void setWindowSizeToScreenSize(){
       if(Gdx.graphics.getHeight() > getBackground().getMinHeight()*2){
           setSize(getBackground().getMinWidth()*2,getBackground().getMinHeight()*2);
       }else{
           float sizeRatio = getSizeRatio();
           setSize(getBackground().getMinWidth()*sizeRatio,getBackground().getMinHeight()*sizeRatio);
       }
   }


    public void addCloseButton () {
        addCloseButton(20,20);
    }

    public void addCloseButton (float height, float width) {
        Label titleLabel = getTitleLabel();
        Table titleTable = getTitleTable();
        ImageButton closeButton = new ImageButton(new TextureRegionDrawable(Annihilation.getAssets().get("gfx/atlas/game_icon.atlas", TextureAtlas.class).findRegion("close_icon")));
        closeButton.setSize(closeButton.getImage().getImageWidth(),closeButton.getImage().getImageWidth());
        titleTable.add(closeButton).size(width,height).padRight(1).padTop(15);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                close();
            }
        });
        closeButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                event.cancel();
                return true;
            }
        });

        if (titleLabel.getLabelAlign() == Align.center && titleTable.getChildren().size == 2)
            titleTable.getCell(titleLabel).padLeft(closeButton.getWidth() * 2);
    }


    public void moveToCenter() {
        Stage parent = getStage();
        if (parent != null) setPosition((parent.getWidth() - getWidth()) / 2, (parent.getHeight() - getHeight()) / 2);
    }

    public void close() {
        remove();
    }

    public boolean isOpen() {
        if (getStage() == null) {
            return false;
        } else {
            Stage stage = getStage();
            for (Actor actor : stage.getActors()) {
                if (actor.getClass().isInstance(this)) {
                    return true;
                }
            }
        }
        return false;
    }
}
