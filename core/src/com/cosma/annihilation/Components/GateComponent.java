package com.cosma.annihilation.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class GateComponent implements Component {
    public String targetMapPath;
    public Vector2 playerPositionOnTargetMap = new Vector2();
}
