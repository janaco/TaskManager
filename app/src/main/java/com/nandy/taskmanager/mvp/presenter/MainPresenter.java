package com.nandy.taskmanager.mvp.presenter;

import android.os.Bundle;

import com.nandy.taskmanager.Constants;
import com.nandy.taskmanager.activity.CreateTaskActivity;
import com.nandy.taskmanager.activity.SettingsActivity;
import com.nandy.taskmanager.eventbus.TaskListChangedEvent;
import com.nandy.taskmanager.eventbus.TasksCleanedEvent;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.mvp.contract.MainActivityContract;
import com.nandy.taskmanager.mvp.model.CreateTaskModel;
import com.nandy.taskmanager.mvp.model.DummyDataModel;
import com.nandy.taskmanager.mvp.model.TaskRecordsModel;

import org.greenrobot.eventbus.EventBus;
import org.kaerdan.presenterretainer.Presenter;

import java.util.List;

/**
 * Created by yana on 21.01.18.
 */

public class MainPresenter implements Presenter<MainActivityContract.View>, MainActivityContract.Presenter {

    private MainActivityContract.View mView;

    private DummyDataModel mDummyDataMode;
    private TaskRecordsModel mRecordsModel;

    @Override
    public void onAttachView(MainActivityContract.View view) {
        mView = view;
    }

    @Override
    public void onDetachView() {
        mView = null;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onCreateTaskClick() {
        Bundle args = new Bundle();
        args.putInt("mode", CreateTaskModel.MODE_CREATE);
        mView.launchActivityForResult(args, CreateTaskActivity.class, Constants.REQUEST_CREATE_TASK);
    }

    @Override
    public void onFillListOptionSelected() {
        List<Task> tasks = mDummyDataMode.generateDummyData();
        EventBus.getDefault().post(new TaskListChangedEvent(tasks));
    }

    @Override
    public void onClearAllOptionSelected() {
        mRecordsModel.clearAll();
        EventBus.getDefault().post(new TasksCleanedEvent());
    }

    @Override
    public void onSettingsOptionSelected() {
        mView.launchActivity(null,  SettingsActivity.class);
    }

    @Override
    public void onExitOptionSelected() {
        mView.finish();
    }

    public void setDummyDataMode(DummyDataModel mDummyDataMode) {
        this.mDummyDataMode = mDummyDataMode;
    }

    public void setRecordsModel(TaskRecordsModel mRecordsModel) {
        this.mRecordsModel = mRecordsModel;
    }

}
