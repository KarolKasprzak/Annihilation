package com.cosma.annihilation.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.cosma.annihilation.Ai.HumanAiTest;
import com.cosma.annihilation.Ai.Tasks.Task;
import com.cosma.annihilation.Utils.Enums.AiType;

public class AiComponent implements Component{

    public float speed = 1.5f;
    public Vector2 startPosition = new Vector2();
    public int patrolRange = 3;
    
    /**-1 = left / 1 = right     */
    public int faceDirection = -1;

    public boolean isHearEnemy = false;
    public Task task = new HumanAiTest();
    public Vector2 enemyPosition = new Vector2();
    public boolean isPaused = false;
    public AiType aiType;

}
