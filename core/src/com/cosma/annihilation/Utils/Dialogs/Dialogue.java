package com.cosma.annihilation.Utils.Dialogs;

import com.badlogic.gdx.utils.Array;

public class Dialogue {
    private Array<DialogueOptions> dialogueOptions;

    public int getCurrentDialogOptionsId() {
        return currentDialogOptions;
    }

    public void setCurrentDialogOptionsId(int currentDialogOptions) {
        this.currentDialogOptions = currentDialogOptions;
    }

    private int currentDialogOptions = 1;

    public Dialogue() {
        dialogueOptions = new Array<>();
    }

    public Array<DialogueOptions> getDialogueOptions() {
        return dialogueOptions;
    }
}
