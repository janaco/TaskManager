package com.nandy.taskmanager.mvp.model;

import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.model.TaskStatus;

/**
 * Created by razomer on 18.01.18.
 */

public class TaskDetailsModel {

    private Task mTask;

    public TaskDetailsModel(Task task){
        mTask = task;
    }

    public Task getTask(){
        return mTask;
    }


    public void setTask(Task mTask) {
        this.mTask = mTask;
    }

    public void toggleStatus(){

        switch (mTask.getStatus()){

            case NEW:
                mTask.setStatus(TaskStatus.ACTIVE);
                break;

            case ACTIVE:
                mTask.setStatus(TaskStatus.COMPLETED);
                break;

            case COMPLETED:
                mTask.setStatus(TaskStatus.ACTIVE);
                break;
        }
    }
}
