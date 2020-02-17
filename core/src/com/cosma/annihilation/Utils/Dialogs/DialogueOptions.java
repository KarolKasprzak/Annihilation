package com.cosma.annihilation.Utils.Dialogs;

import com.badlogic.gdx.utils.Array;

public class DialogueOptions {
    private int index;
    private int selectedOption;
    private Array<DialogueLine> dialogLines;
    private String answer;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public DialogueOptions() {
        dialogLines = new Array<>();

    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(int selectedOption) {
        this.selectedOption = selectedOption;
    }

    public Array<DialogueLine> getDialogLines() {
        return dialogLines;
    }
}
