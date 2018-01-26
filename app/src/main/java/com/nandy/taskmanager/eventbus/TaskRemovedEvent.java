package com.nandy.taskmanager.eventbus;

import com.nandy.taskmanager.model.Task;

/**
 * Created by yana on 26.01.18.
 */

public class TaskRemovedEvent {

    public final Task task;

    public TaskRemovedEvent(Task task) {
        this.task = task;
    }
}
