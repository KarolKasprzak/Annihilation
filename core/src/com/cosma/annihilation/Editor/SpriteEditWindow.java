package com.cosma.annihilation.Editor;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.cosma.annihilation.Editor.CosmaMap.AnimatedSprite;
import com.cosma.annihilation.Editor.CosmaMap.Sprite;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kotcrab.vis.ui.widget.spinner.IntSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.SimpleFloatSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;

public class SpriteEditWindow  extends VisWindow {
    public SpriteEditWindow(Sprite sprite) {
        super("Sprite edit.");
        VisLabel positionX = new VisLabel("position X: " + sprite.getX());
        VisLabel positionY = new VisLabel("position Y: " + sprite.getY());

        final Spinner orderSpinner = new Spinner("render order", new IntSpinnerModel(sprite.getRenderOrder(), 0, 10, 1));

//        orderSpinner.getTextField().
//        orderSpinner.getTextField().addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                System.out.println("order : " + ((IntSpinnerModel)orderSpinner.getModel()).getValue());
//            }
//        });
        orderSpinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sprite.setRenderOrder(((IntSpinnerModel)orderSpinner.getModel()).getValue());
            }
        });

        if(sprite instanceof AnimatedSprite){
            VisCheckBox isLoop = new VisCheckBox("animation loop");
            if(((AnimatedSprite) sprite).getPlayMode().equals(Animation.PlayMode.LOOP)){
                isLoop.setChecked(true);
            }else{
                isLoop.setChecked(false);
            }
            isLoop.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if(isLoop.isChecked()){
                        ((AnimatedSprite) sprite).setPlayMode(Animation.PlayMode.LOOP);
                    }else{
                        ((AnimatedSprite) sprite).setPlayMode(Animation.PlayMode.NORMAL);
                    }
                }
            });
            add(isLoop);
        }
        row();
        add(positionX);
        row();
        add(positionY);
        row();
        add(orderSpinner);

        setCenterOnAdd(true);
        addCloseButton();
        setModal(true);
        pack();
        setSize(getWidth(),getHeight()*2);
    }
}
