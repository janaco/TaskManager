package com.nandy.taskmanager.mvp.presenter;

import android.os.Bundle;

import com.daimajia.swipe.util.Attributes;
import com.nandy.taskmanager.adapter.TasksAdapter;
import com.nandy.taskmanager.eventbus.TaskListChangedEvent;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.model.TaskStatus;
import com.nandy.taskmanager.mvp.BasePresenter;
import com.nandy.taskmanager.mvp.model.TaskRecordsModel;
import com.nandy.taskmanager.mvp.model.TaskRemindersModel;
import com.nandy.taskmanager.mvp.model.TaskModel;
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
    private TaskRemindersModel mTaskReminderMode;
    private TaskModel mTaskModel;

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

    public void delete(int position) {
        onDeleteOptionSelected(mAdapter.getItem(position), position);
    }

    public Task getTask(int position) {
        return mAdapter.getItem(position);
    }

    @Override
    public void onDeleteOptionSelected(Task task, int position) {
        mRecordsModel.delete(task);
        mTaskReminderMode.cancelReminder(task.getId());
        mAdapter.remove(position);
    }

    @Override
    public void onEditOptionSelected(Task task, int position) {
        mView.startEditTaskActivity(task);
    }

    @Override
    public void onToggleStatus(Task task, int position) {

        switch (task.getStatus()) {

            case NEW:
                task.setStatus(TaskStatus.ACTIVE);
                mTaskModel.start(task);
                mTaskReminderMode.scheduleStartReminder(task);
                break;

            case ACTIVE:
                task.setStatus(TaskStatus.COMPLETED);
                mTaskModel.complete(task);
                mTaskReminderMode.cancelReminder(task.getId());
                break;

            default:
                task.setStatus(TaskStatus.NEW);
                break;
        }

        mRecordsModel.update(task);
        mAdapter.set(task, position);
    }

    public void resetStart(int position) {
        Task task = getTask(position);
        task.setStatus(TaskStatus.NEW);
        mRecordsModel.update(task);
        mTaskReminderMode.cancelReminder(task.getId());
    }

    public void resetEnd(int position) {
        Task task = getTask(position);
        mTaskReminderMode.cancelReminder(task.getId());
        mTaskReminderMode.scheduleEndReminder(task.getId(), task.getScheduledDuration());
    }


    public void setTaskReminderMode(TaskRemindersModel mTaskReminderMode) {
        this.mTaskReminderMode = mTaskReminderMode;
    }

    public void setTaskStatusModel(TaskModel mTaskModel) {
        this.mTaskModel = mTaskModel;
    }
}
