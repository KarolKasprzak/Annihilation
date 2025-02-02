package com.cosma.annihilation.EntityEngine.core;


import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.cosma.annihilation.EntityEngine.utils.ImmutableArray;

import java.util.Comparator;

class SystemManager {
    private SystemComparator systemComparator = new SystemComparator();
    private Array<EntitySystem> systems = new Array<EntitySystem>(true, 16);
    private ImmutableArray<EntitySystem> immutableSystems = new ImmutableArray<EntitySystem>(systems);
    private ObjectMap<Class<?>, EntitySystem> systemsByClass = new ObjectMap<Class<?>, EntitySystem>();
    private SystemListener listener;

    public SystemManager(SystemListener listener) {
        this.listener = listener;
    }

    public void addSystem(EntitySystem system){
        Class<? extends EntitySystem> systemType = system.getClass();
        EntitySystem oldSytem = getSystem(systemType);

        if (oldSytem != null) {
            removeSystem(oldSytem);
        }

        systems.add(system);
        systemsByClass.put(systemType, system);
        systems.sort(systemComparator);
        listener.systemAdded(system);
    }

    public void removeSystem(EntitySystem system){
        if(systems.removeValue(system, true)) {
            systemsByClass.remove(system.getClass());
            listener.systemRemoved(system);
        }
    }

    public void removeAllSystems() {
        while(systems.size > 0) {
            removeSystem(systems.first());
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends EntitySystem> T getSystem(Class<T> systemType) {
        return (T) systemsByClass.get(systemType);
    }

    public ImmutableArray<EntitySystem> getSystems() {
        return immutableSystems;
    }

    private static class SystemComparator implements Comparator<EntitySystem> {
        @Override
        public int compare(EntitySystem a, EntitySystem b) {
            return a.priority > b.priority ? 1 : (a.priority == b.priority) ? 0 : -1;
        }
    }

    interface SystemListener {
        void systemAdded(EntitySystem system);
        void systemRemoved(EntitySystem system);
    }
}