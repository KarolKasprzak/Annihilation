package com.cosma.annihilation.Utils;


import com.cosma.annihilation.Components.DrawOrderComponent;
import com.cosma.annihilation.EntityEngine.core.ComponentMapper;
import com.cosma.annihilation.EntityEngine.core.Entity;

import java.util.Comparator;

public class RenderComparator implements Comparator<Entity>{
    private ComponentMapper<DrawOrderComponent> pm = ComponentMapper.getFor(DrawOrderComponent.class);

    @Override
    public int compare(Entity o1, Entity o2) {
        return (int)Math.signum(pm.get(o1).drawOrder - pm.get(o2).drawOrder);
    }
}
