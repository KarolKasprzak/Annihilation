package com.cosma.annihilation.Utils.Dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;


public class DialogueManager {
    private ObjectMap<String,Dialogue> dialogues;

    public Dialogue getDialogue(String dialogueName) {
        return dialogues.get(dialogueName);
    }

    public DialogueManager() {
        dialogues = new ObjectMap<>();
        Json json = new Json();
        json.setSerializer(Dialogue.class, new DialogueSerializer());

        FileHandle file = Gdx.files.local("json/dialogs");
        for (FileHandle dialogueFile : file.list(".dial")) {
            Dialogue dialogue = json.fromJson(Dialogue.class,dialogueFile);
            System.out.println("instant");
            dialogues.put(dialogueFile.nameWithoutExtension(),dialogue);

        }
    }
}
