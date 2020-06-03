package com.cosma.annihilation.Gui.MainMenu;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.cosma.annihilation.Components.ActionComponent;
import com.cosma.annihilation.Components.PlayerComponent;
import com.cosma.annihilation.EntityEngine.core.Engine;
import com.cosma.annihilation.EntityEngine.core.Entity;
import com.cosma.annihilation.Gui.GuiWindow;

public class NoteWindow extends GuiWindow {


    private boolean isOpen = false;
    private Engine engine;
    private Label label;

    public NoteWindow(Skin skin, Engine engine, float parentWidth) {
        super("Note", skin);
        this.engine = engine;

        label = new Label("",skin);
        label.setWrap(true);

        Table rootTable = new Table();
        this.add(rootTable).size(parentWidth,parentWidth * 1.2f);

        rootTable.add(label).fill().expand();
    }



    public void initialize() {
        Entity playerEntity = engine.getPlayerEntity();
        if (playerEntity.getComponent(PlayerComponent.class).processedEntity.getComponent(ActionComponent.class).textToDisplay != null) {
            label.setText(playerEntity.getComponent(PlayerComponent.class).processedEntity.getComponent(ActionComponent.class).textToDisplay);
            isOpen = true;
        }
    }

    @Override
    public void close() {
        isOpen = false;
        super.close();
        this.getTitleLabel().setText("");
    }
}
