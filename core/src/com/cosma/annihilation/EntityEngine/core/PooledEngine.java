package com.cosma.annihilation.EntityEngine.core;

import box2dLight.RayHandler;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.ReflectionPool;
import com.cosma.annihilation.Utils.StartStatus;

/**
 * Supports {@link Entity} and {@link Component} pooling. This improves performance in environments where creating/deleting
 * entities is frequent as it greatly reduces memory allocation.
 * <ul>
 * <li>Create entities using {@link #createEntity()}</li>
 * <li>Create components using {@link #createComponent(Class)}</li>
 * <li>Components should implement the {@link Poolable} interface when in need to reset its state upon removal</li>
 * </ul>
 * @author David Saltares
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class PooledEngine extends Engine {

    private EntityPool entityPool;
    private ComponentPools componentPools;



    /**
     * Creates new PooledEngine with the specified pools size configurations.
     * @param entityPoolInitialSize initial number of pre-allocated entities.
     * @param entityPoolMaxSize maximum number of pooled entities.
     * @param componentPoolInitialSize initial size for each component type pool.
     * @param componentPoolMaxSize maximum size for each component type pool.
     */
    public PooledEngine (int entityPoolInitialSize, int entityPoolMaxSize, int componentPoolInitialSize, int componentPoolMaxSize, World world, RayHandler rayHandler, StartStatus startStatus) {
        super(world, rayHandler, startStatus);

        entityPool = new EntityPool(entityPoolInitialSize, entityPoolMaxSize);
        componentPools = new ComponentPools(componentPoolInitialSize, componentPoolMaxSize);
    }

    /** @return Clean {@link Entity} from the Engine pool. In order to add it to the {@link Engine}, use {@link #addEntity(Entity)}. @{@link Override {@link Engine#createEntity()}} */
    @Override
    public Entity createEntity () {
        return entityPool.obtain();
    }

    /**
     * Retrieves a new {@link Component} from the {@link Engine} pool. It will be placed back in the pool whenever it's removed
     * from an {@link Entity} or the {@link Entity} itself it's removed.
     * Overrides the default implementation of Engine (creating a new Object)
     */
    @Override
    public <T extends Component> T createComponent (Class<T> componentType) {
        return componentPools.obtain(componentType);
    }

    /**
     * Removes all free entities and components from their pools. Although this will likely result in garbage collection, it will
     * free up memory.
     */
    public void clearPools () {
        entityPool.clear();
        componentPools.clear();
    }

    @Override
    protected void removeEntityInternal (Entity entity) {
        super.removeEntityInternal(entity);

        if (entity instanceof PooledEntity) {
            entityPool.free((PooledEntity)entity);
        }
    }

    private class PooledEntity extends Entity implements Poolable {
        @Override
        Component removeInternal(Class<? extends Component> componentClass) {
            Component removed = super.removeInternal(componentClass);
            if (removed != null) {
                componentPools.free(removed);
            }

            return removed;
        }

        @Override
        public void reset () {
            removeAll();
            flags = 0;
            componentAdded.removeAllListeners();
            componentRemoved.removeAllListeners();
            scheduledForRemoval = false;
            removing = false;
        }
    }

    private class EntityPool extends Pool<PooledEntity> {

        public EntityPool (int initialSize, int maxSize) {
            super(initialSize, maxSize);
        }

        @Override
        protected PooledEntity newObject () {
            return new PooledEntity();
        }
    }

    private class ComponentPools {
        private ObjectMap<Class<?>, ReflectionPool> pools;
        private int initialSize;
        private int maxSize;

        public ComponentPools (int initialSize, int maxSize) {
            this.pools = new ObjectMap<Class<?>, ReflectionPool>();
            this.initialSize = initialSize;
            this.maxSize = maxSize;
        }

        public <T> T obtain (Class<T> type) {
            ReflectionPool pool = pools.get(type);

            if (pool == null) {
                pool = new ReflectionPool(type, initialSize, maxSize);
                pools.put(type, pool);
            }

            return (T)pool.obtain();
        }

        public void free (Object object) {
            if (object == null) {
                throw new IllegalArgumentException("object cannot be null.");
            }

            ReflectionPool pool = pools.get(object.getClass());

            if (pool == null) {
                return; // Ignore freeing an object that was never retained.
            }

            pool.free(object);
        }

        public void freeAll (Array objects) {
            if (objects == null) throw new IllegalArgumentException("objects cannot be null.");

            for (int i = 0, n = objects.size; i < n; i++) {
                Object object = objects.get(i);
                if (object == null) continue;
                free(object);
            }
        }

        public void clear () {
            for (Pool pool : pools.values()) {
                pool.clear();
            }
        }
    }
}
