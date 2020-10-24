package com.cosma.annihilation.Components;

import com.cosma.annihilation.EntityEngine.core.Component;
import com.badlogic.gdx.math.Vector2;

public class MapChangeComponent implements Component {
    public String targetMapPath;
    public Vector2 playerPositionOnTargetMap = new Vector2();
}
