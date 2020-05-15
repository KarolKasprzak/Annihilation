package com.cosma.annihilation.Components;

import com.badlogic.ashley.core.Component;
import com.cosma.annihilation.Utils.Enums.EntityAction;

public class ActionComponent implements Component {
    public EntityAction action;
    public Object actionTarget;
    public String actionTargetName;
    public float offsetX;
    public float offsetY;
}
