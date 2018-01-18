package com.nandy.taskmanager.mvp.presenter;

import android.os.Bundle;

import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.mvp.BasePresenter;
import com.nandy.taskmanager.mvp.model.DummyDataModel;
import com.nandy.taskmanager.mvp.model.TasksListModel;
import com.nandy.taskmanager.mvp.view.TasksListView;

import java.util.List;

/**
 * Created by yana on 16.01.18.
 */

public class TasksPresenter extends BasePresenter {

    private TasksListView mView;
    private TasksListModel mTasksListModel;
    private DummyDataModel mDummyDataModel;

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
    public void saveInstanceState(Bundle outState) {
        mTasksListModel.saveInstanceState(outState);
    }

    @Override
    public void restoreInstanceState(Bundle savedInstanceState) {
        mTasksListModel.restoreInstanceState(savedInstanceState);
    }

    public void generateDummyData() {
        List<Task> tasks = mDummyDataModel.generateDummyData(mTasksListModel.getTasksCount(), 30);
        mTasksListModel.displayAll(tasks);
    }

    public Bundle getArguments(int position){
        Bundle args = new Bundle();
        args.putParcelable("task", mTasksListModel.get(position));

        return args;

    }

    public void clearAllTasks() {
        mTasksListModel.clearAll();
    }

    public void loadTasks() {
        mTasksListModel.displayAll(mTasksListModel.loadTasks());
    }

    public void displayTask(Task task) {
        mTasksListModel.display(task);
    }

    public void setTasksListModel(TasksListModel mTasksListModel) {
        this.mTasksListModel = mTasksListModel;
    }

    public void setDummyDataModel(DummyDataModel mDummyDataModel) {
        this.mDummyDataModel = mDummyDataModel;
    }
}
