package com.cosma.annihilation.Utils.Dialogs;

public class DialogueLine {


    private int id;
    private String dialogText;
    private int nextDialog;
    private boolean hasAction = false;

    public boolean hasAction() {
        return hasAction;
    }

    public void setHasAction(boolean hasAction) {
        this.hasAction = hasAction;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    private String action;

    DialogueLine() {

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDialogText() {
        return dialogText;
    }

    void setDialogText(String dialogText) {
        this.dialogText = dialogText;
    }

    public int getNextDialog() {
        return nextDialog;
    }

    void setNextDialog(int nextDialog) {
        this.nextDialog = nextDialog;
    }


}
