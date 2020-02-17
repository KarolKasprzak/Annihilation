package com.cosma.annihilation.Utils.Dialogs;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class DialogueSerializer implements Json.Serializer<Dialogue> {
    @Override
    public void write(Json json, Dialogue object, Class knownType) {

    }

    @Override
    public Dialogue read(Json json, JsonValue jsonData, Class type) {
        Dialogue dialogue = new Dialogue();
        for (JsonValue value : jsonData.get("dialogs")) {
             DialogueOptions dialogueOptions = new DialogueOptions();

             dialogueOptions.setAnswer(value.getString("answer"));
             dialogueOptions.setIndex(value.getInt("id"));

             for(JsonValue dialogueJsonValue : value.get("dialogueOptions")){
                 DialogueLine dialogueLine = new DialogueLine();
                 dialogueLine.setId(dialogueJsonValue.getInt("id"));
                 if(dialogueJsonValue.has("hasAction")){
                     dialogueLine.setHasAction(dialogueJsonValue.getBoolean("hasAction"));
                     dialogueLine.setAction(dialogueJsonValue.getString("action"));
                 }
                 dialogueLine.setNextDialog(dialogueJsonValue.getInt("nextDialog"));
                 dialogueLine.setDialogText(dialogueJsonValue.getString("dialogText"));
                 dialogueOptions.getDialogLines().add(dialogueLine);
             }

             dialogue.getDialogueOptions().add(dialogueOptions);
        }

        return dialogue;
    }
}
