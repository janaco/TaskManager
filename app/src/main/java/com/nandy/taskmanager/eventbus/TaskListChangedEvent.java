package com.nandy.taskmanager.eventbus;

import com.nandy.taskmanager.model.Task;

import java.util.List;

/**
 * Created by yana on 21.01.18.
 */

public class TaskListChangedEvent {

    private List<Task> mTasks;

    public TaskListChangedEvent(List<Task> tasks){
        mTasks = tasks;
    }

    public List<Task> getTasks() {
        return mTasks;
    }
}
