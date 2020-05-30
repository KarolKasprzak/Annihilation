package com.cosma.annihilation.Components;


import com.badlogic.gdx.math.Vector2;
import com.cosma.annihilation.EntityEngine.core.Component;
import com.cosma.annihilation.Utils.Enums.EntityAction;

public class ActionComponent implements Component {
    public EntityAction action;
    public Object actionTarget;
    public String actionTargetName;
    public Vector2 actionTargetPosition;
    public float offsetX;
    public float offsetY;
}
