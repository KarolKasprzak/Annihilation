package com.cosma.annihilation.Components;

import com.badlogic.gdx.math.Vector2;
import com.cosma.annihilation.EntityEngine.core.Component;

public class GateComponent implements Component {
    public boolean isOpen = false;
    public boolean isMoving = false;
    public float moveDistance = 2;
    public Vector2 targetPosition = new Vector2();
    public String gateName;
}
