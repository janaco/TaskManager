package com.nandy.taskmanager.mvp.model;

import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.model.TaskStatus;

/**
 * Created by razomer on 18.01.18.
 */

public class TaskDetailsModel {

    private Task mTask;

    public TaskDetailsModel(Task task) {
        mTask = task;
    }

    public Task getTask() {
        return mTask;
    }


    public void setTask(Task mTask) {
        this.mTask = mTask;
    }

    public void setStatus(TaskStatus status) {
        mTask.setStatus(status);
    }
}
