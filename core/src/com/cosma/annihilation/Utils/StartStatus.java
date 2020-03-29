package com.cosma.annihilation.Utils;

public class StartStatus {
    private int saveSlot;
    private boolean newGame;

    public int getSaveSlot() {
        return saveSlot;
    }

    public void setSaveSlot(int saveSlot) {
        this.saveSlot = saveSlot;
    }

    public void setNewGame(boolean newGame) {
        this.newGame = newGame;
    }

    public StartStatus(int saveSlot, boolean newGame) {
        this.saveSlot = saveSlot;
        this.newGame = newGame;
    }

    public boolean isNewGame() {
        return newGame;
    }
}
