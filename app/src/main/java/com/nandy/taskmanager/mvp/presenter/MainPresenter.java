package com.nandy.taskmanager.mvp.presenter;

import com.nandy.taskmanager.eventbus.TaskListChangedEvent;
import com.nandy.taskmanager.eventbus.TasksCleanedEvent;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.mvp.model.DummyDataModel;
import com.nandy.taskmanager.mvp.model.TaskRecordsModel;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by yana on 21.01.18.
 */

public class MainPresenter {

    private DummyDataModel mDummyDataMode;
    private TaskRecordsModel mRecordsModel;

    public void generateDummyData(){
        List<Task> tasks = mDummyDataMode.generateDummyData();
        EventBus.getDefault().post(new TaskListChangedEvent(tasks));
    }

    public void clearAllTasks(){
        mRecordsModel.clearAll();
        EventBus.getDefault().post(new TasksCleanedEvent());
    }

    public void setDummyDataMode(DummyDataModel mDummyDataMode) {
        this.mDummyDataMode = mDummyDataMode;
    }

    public void setRecordsModel(TaskRecordsModel mRecordsModel) {
        this.mRecordsModel = mRecordsModel;
    }
}
