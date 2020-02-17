package com.cosma.annihilation.Ai;

import com.badlogic.ashley.core.Entity;

public class NpcAiBasic extends AiCore implements ArtificialIntelligence {

    private String aiStatus = "";

    public NpcAiBasic() {

    }

    @Override
    public void update(Entity entity) {
       setFaceToPlayer(entity);
    }


    @Override
    public String getStatus() {
        return aiStatus;
    }
}
