package com.cosma.annihilation.Gui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.cosma.annihilation.Annihilation;
import com.cosma.annihilation.Components.DialogueComponent;
import com.cosma.annihilation.Components.PlayerComponent;
import com.cosma.annihilation.Utils.Dialogs.DialogueLine;
import com.cosma.annihilation.Utils.Dialogs.DialogueOptions;
import com.cosma.annihilation.Utils.DialogueActionI;
import com.cosma.annihilation.Utils.InGameAction;
import com.cosma.annihilation.Utils.Util;

public class DialogueWindow extends Window implements DialogueActionI {

    private Skin skin;
    private PlayerComponent playerComponent;
    private Table dialogueTable;
    private InGameAction actionManager;
    private DialogueWindow dialogueWindow;
    private Engine engine;

    public DialogueWindow(Skin skin, Engine engine) {
        super("", skin);

        this.skin = skin;
        this.engine = engine;

        actionManager = new InGameAction();

        setFillParent(true);

        debugAll();

        Pixmap bgPixmap = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(0, 1, 0, 0f);
        bgPixmap.fill();
        TextureRegionDrawable textureRegionDrawableBg = new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap)));

        setBackground(textureRegionDrawableBg);

        dialogueTable = new Table();
        add(new Image(new TextureRegion(Annihilation.getAssets().get("gfx/interface/test.png",Texture.class))));
//        dialogueTable.setBackground(new TextureRegionDrawable(new TextureRegion(Annihilation.getAssets().get("gfx/interface/gui_frame.png",Texture.class))));

        playerComponent = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first().getComponent(PlayerComponent.class);

        TextButton exitButton = new TextButton("exit", skin);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
              closeDialogueWindow();
            }
        });

        add(exitButton).center();
        row();
        add(dialogueTable).bottom().expand().height(Gdx.graphics.getHeight()/3).fillX();
        dialogueWindow = this;
    }



    private void clearDialogueTable() {
        dialogueTable.clearChildren();
    }

    public void displayDialogue(DialogueComponent dialogueComponent){
        clearDialogueTable();
        for(DialogueOptions dialogueOptions: dialogueComponent.dialog.getDialogueOptions()){
            if(dialogueOptions.getIndex() == dialogueComponent.dialog.getCurrentDialogOptionsId()){
                drawDialogueOptions(dialogueOptions,dialogueComponent);
            }
        }
    }

    private void closeDialogueWindow(){
        remove();
        engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first().getComponent(PlayerComponent.class).canPerformAction = true;
        engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first().getComponent(PlayerComponent.class).canMoveOnSide = true;
    }


    private void drawDialogueOptions(DialogueOptions dialogueOptions,DialogueComponent dialogueComponent) {


        for (DialogueLine dialogueLine : dialogueOptions.getDialogLines()) {
            Label dialogueLabel = new Label(dialogueLine.getDialogText(), skin);
            Util.setLabelColorToGreen(dialogueLabel);
            dialogueLabel.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    dialogueOptions.setSelectedOption(dialogueLine.getId());
                    dialogueComponent.dialog.setCurrentDialogOptionsId(dialogueLine.getNextDialog());
                    displayDialogue(dialogueComponent);
                    if(dialogueLine.hasAction()){
                        actionManager.runAction(dialogueLine.getAction(),dialogueWindow);
                        return true;
                    }
                    return super.touchDown(event, x, y, pointer, button);

                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    Util.setLabelColorToGreen(dialogueLabel);
                }

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    dialogueLabel.setColor(45, 45, 0, 255);
                }
            });
            dialogueTable.row();
            dialogueTable.add(dialogueLabel).center();
            dialogueTable.row();

        }
    }

    @Override
    public void action() {
        playerComponent.canPerformAction = true;
        closeDialogueWindow();
    }
}
