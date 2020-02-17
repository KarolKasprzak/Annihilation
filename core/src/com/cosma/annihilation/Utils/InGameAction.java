package com.cosma.annihilation.Utils;

import com.cosma.annihilation.Gui.DialogueWindow;

public class InGameAction {
//   private World world;
//   private Engine engine;
    public InGameAction() {
//        this.world = world;
//        this.engine = engine;
    }


    public void runAction(String actionKey, DialogueActionI action){

        switch (actionKey){
            case "exitDialog":
                exitDialog(action);
                break;

        }


    }


    private void exitDialog(DialogueActionI action){
        action.action();
    }

}
