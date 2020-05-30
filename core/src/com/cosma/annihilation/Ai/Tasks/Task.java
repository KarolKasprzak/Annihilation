package com.cosma.annihilation.Ai.Tasks;


import com.cosma.annihilation.EntityEngine.core.Entity;

public abstract class Task {
    private TaskState state;

    protected Task() {
        this.state = TaskState.Prepared;
    }

    public enum TaskState {
        Prepared,
        Success,
        Failure,
        Running
    }

    public void start() {
        this.state = TaskState.Running;
    }

    public abstract void reset();

    public abstract void update(Entity entity, float deltaTime);

    protected void success() {
        this.state = TaskState.Success;
    }

    protected void fail() {
        this.state = TaskState.Failure;
    }

    public boolean isSuccess(){
        return state.equals(TaskState.Success);
    }

    public boolean isPrepared(){
        return state.equals(TaskState.Prepared);
    }

    public boolean isWorking(){
        return state.equals(TaskState.Running);
    }

    public TaskState getState() {
        return state;
    }
}
