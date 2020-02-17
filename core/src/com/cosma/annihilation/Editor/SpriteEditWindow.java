package com.cosma.annihilation.Editor;

import com.cosma.annihilation.Editor.CosmaMap.Sprite;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisWindow;

public class SpriteEditWindow  extends VisWindow {
    public SpriteEditWindow(Sprite sprite) {
        super("Sprite edit.");
        VisLabel positionX = new VisLabel("position X: " + sprite.getX());
        VisLabel positionY = new VisLabel("position Y: " + sprite.getY());

        add(positionX);
        row();
        add(positionY);

        setCenterOnAdd(true);
        addCloseButton();
        pack();
        setSize(getWidth(),getHeight()*2);
    }
}
