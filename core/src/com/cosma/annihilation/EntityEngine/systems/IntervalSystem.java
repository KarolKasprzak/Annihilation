package com.cosma.annihilation.EntityEngine.systems;


import com.cosma.annihilation.EntityEngine.core.EntitySystem;

/**
 * A simple {@link EntitySystem} that does not run its update logic every call to {@link EntitySystem#update(float)}, but after a
 * given interval. The actual logic should be placed in {@link IntervalSystem#updateInterval()}.
 * @author David Saltares
 */
public abstract class IntervalSystem extends EntitySystem {
    private float interval;
    private float accumulator;

    /**
     * @param interval time in seconds between calls to {@link IntervalSystem#updateInterval()}.
     */
    public IntervalSystem (float interval) {
        this(interval, 0);
    }

    /**
     * @param interval time in seconds between calls to {@link IntervalSystem#updateInterval()}.
     * @param priority
     */
    public IntervalSystem (float interval, int priority) {
        super(priority);
        this.interval = interval;
        this.accumulator = 0;
    }

    public float getInterval() {
        return interval;
    }

    @Override
    public final void update (float deltaTime) {
        accumulator += deltaTime;

        while (accumulator >= interval) {
            accumulator -= interval;
            updateInterval();
        }
    }

    /**
     * The processing logic of the system should be placed here.
     */
    protected abstract void updateInterval ();
}