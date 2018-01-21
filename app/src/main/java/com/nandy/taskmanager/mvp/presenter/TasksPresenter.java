package com.nandy.taskmanager.mvp.presenter;

import android.os.Bundle;

import com.daimajia.swipe.util.Attributes;
import com.nandy.taskmanager.adapter.TasksAdapter;
import com.nandy.taskmanager.eventbus.TaskListChangedEvent;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.mvp.BasePresenter;
import com.nandy.taskmanager.mvp.model.TaskRecordsModel;
import com.nandy.taskmanager.mvp.view.TasksListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * Created by yana on 16.01.18.
 */

public class TasksPresenter extends BasePresenter implements TasksAdapter.OnItemOptionSelectedListener {

    private TasksListView mView;
    private TaskRecordsModel mRecordsModel;
    private TasksAdapter mAdapter;

    public TasksPresenter(TasksListView view) {
        mView = view;
    }

    @Override
    public void start() {
        mAdapter = new TasksAdapter();
        mAdapter.setOnItemOptionSelectedListener(this);
        mAdapter.setMode(Attributes.Mode.Single);
        mView.setAdapter(mAdapter);
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

    private void refreshList() {
        mAdapter.refresh(mRecordsModel.selectAll());
    }

    @Override
    public void saveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("tasks", mAdapter.getItems());

    }

    @Override
    public void restoreInstanceState(Bundle savedInstanceState) {
        ArrayList<Task> tasksList = savedInstanceState.getParcelableArrayList("tasks");
        mAdapter.addAll(tasksList);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTaskListChangedEvent(TaskListChangedEvent event) {
        mAdapter.addAll(event.getTasks());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTasksCleanedEvent() {
        mAdapter.clearAll();
    }

    public Bundle getArguments(int position) {
        Bundle args = new Bundle();
        args.putParcelable("task", mAdapter.getItem(position));

        return args;

    }

    public void loadTasks() {
        mAdapter.addAll(mRecordsModel.selectAll());
    }

    public void displayTask(Task task) {
        mAdapter.add(task);
    }


    public void setRecordsModel(TaskRecordsModel mRecordsModel) {
        this.mRecordsModel = mRecordsModel;
    }

    @Override
    public void onDeleteOptionSelected(Task task, int position) {

    }

    @Override
    public void onEditOptionSelected(Task task, int position) {

    }

    @Override
    public void onToggleStatus(Task task, int position) {

    }
}
