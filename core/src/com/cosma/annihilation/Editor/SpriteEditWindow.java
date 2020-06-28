package com.cosma.annihilation.Editor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.cosma.annihilation.Editor.CosmaMap.Sprite;
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
