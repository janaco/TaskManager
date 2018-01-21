package com.nandy.taskmanager.mvp.presenter;

import android.os.Bundle;

import com.nandy.taskmanager.eventbus.TaskListChangedEvent;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.mvp.BasePresenter;
import com.nandy.taskmanager.mvp.model.DummyDataModel;
import com.nandy.taskmanager.mvp.model.TaskRecordsModel;
import com.nandy.taskmanager.mvp.model.TasksListModel;
import com.nandy.taskmanager.mvp.view.TasksListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by yana on 16.01.18.
 */

public class TasksPresenter extends BasePresenter {

    private TasksListView mView;
    private TasksListModel mTasksListModel;
    private TaskRecordsModel mRecordsModel;

    public TasksPresenter(TasksListView view) {
        mView = view;
    }

    @Override
    public void start() {
        mView.setAdapter(mTasksListModel.getAdapter());
    }

    @Override
    public void stop() {

    }

    @Override
    public void resume() {
        super.resume();
        EventBus.getDefault().register(this);
        refreshList();
    }

    @Override
    public void pause() {
        super.pause();
        EventBus.getDefault().unregister(this);
    }

    private void refreshList(){
        mTasksListModel.refreshList(mRecordsModel.selectAll());
    }

    @Override
    public void saveInstanceState(Bundle outState) {
        mTasksListModel.saveInstanceState(outState);
    }

    @Override
    public void restoreInstanceState(Bundle savedInstanceState) {
        mTasksListModel.restoreInstanceState(savedInstanceState);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTaskListChangedEvent(TaskListChangedEvent event){
        mTasksListModel.displayAll(event.getTasks());
    }

    @Subscribe(threadMode =  ThreadMode.MAIN)
    public void onTasksCleanedEvent(){
        mTasksListModel.clearAll();
    }
    public Bundle getArguments(int position){
        Bundle args = new Bundle();
        args.putParcelable("task", mTasksListModel.get(position));

        return args;

    }

    public void loadTasks() {
        mTasksListModel.displayAll(mRecordsModel.selectAll());
    }

    public void displayTask(Task task) {
        mTasksListModel.display(task);
    }

    public void setTasksListModel(TasksListModel mTasksListModel) {
        this.mTasksListModel = mTasksListModel;
    }

    public void setRecordsModel(TaskRecordsModel mRecordsModel) {
        this.mRecordsModel = mRecordsModel;
    }
}
