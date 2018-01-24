package com.nandy.taskmanager.mvp.presenter;

import android.os.Bundle;

import com.daimajia.swipe.util.Attributes;
import com.nandy.taskmanager.activity.CreateTaskActivity;
import com.nandy.taskmanager.activity.TaskDetailsActivity;
import com.nandy.taskmanager.adapter.TasksAdapter;
import com.nandy.taskmanager.eventbus.TaskListChangedEvent;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.model.TaskStatus;
import com.nandy.taskmanager.mvp.contract.TasksContract;
import com.nandy.taskmanager.mvp.model.CreateTaskModel;
import com.nandy.taskmanager.mvp.model.TaskModel;
import com.nandy.taskmanager.mvp.model.TaskRecordsModel;
import com.nandy.taskmanager.mvp.model.TaskRemindersModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import static com.nandy.taskmanager.activity.TaskDetailsActivity.REQUEST_CODE_EDIT;

/**
 * Created by yana on 16.01.18.
 */

public class TasksPresenter implements TasksContract.Presenter, TasksAdapter.OnItemOptionSelectedListener {

    private static final String PARAM_FIRST_VISIBLE_POSITION = "first_visible_position";
    private static final String PARAM_TASKS = "tasks";

    private TasksContract.View mView;
    private TaskRecordsModel mRecordsModel;
    private TaskRemindersModel mTaskReminderMode;
    private TaskModel mTaskModel;

    private TasksAdapter mAdapter;

    private Bundle mSavedInstanceState;

    @Override
    public void onAttachView(TasksContract.View view) {
        mView = view;

        if (mAdapter == null) {
            mAdapter = new TasksAdapter();
            mAdapter.setOnItemOptionSelectedListener(this);
            mAdapter.setMode(Attributes.Mode.Single);
        }
        mView.setAdapter(mAdapter);

        if (mSavedInstanceState != null) {
            restoreViewState();
        }

    }

    private void restoreViewState() {
        List<Task> tasks = mSavedInstanceState.getParcelableArrayList(PARAM_TASKS);
        mAdapter.setItems(tasks);
        mView.scrollToPosition(mSavedInstanceState.getInt(PARAM_FIRST_VISIBLE_POSITION));
        mSavedInstanceState = null;
    }

    @Override
    public void onDetachView() {
        mView = null;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void resume() {
        EventBus.getDefault().register(this);
        loadTasks();
    }

    @Override
    public void pause() {
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTaskListChangedEvent(TaskListChangedEvent event) {
        mAdapter.addAll(event.getTasks());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTasksCleanedEvent() {
        mAdapter.clearAll();
    }

    @Override
    public void openDetails(int position) {
        openDetails(getTask(position));
    }

    @Override
    public void openDetails(Task task) {
        Bundle args = new Bundle();
        args.putParcelable("task", task);
        mView.launchActivity(args, TaskDetailsActivity.class);

    }

    private void loadTasks() {
        mAdapter.setItems(mRecordsModel.selectAll());
    }

    @Override
    public void add(Task task) {
        mAdapter.add(task);
    }

    @Override
    public void delete(int position) {
        onDeleteOptionSelected(getTask(position), position);
    }

    @Override
    public void onDeleteOptionSelected(Task task, int position) {
        mTaskModel.delete(task);
        mTaskReminderMode.cancelReminder(task.getId());
        mAdapter.remove(position);
    }

    @Override
    public void edit(int position) {
        onEditOptionSelected(getTask(position), position);
    }

    @Override
    public void onEditOptionSelected(Task task, int position) {
        Bundle args = new Bundle();
        args.putParcelable("task", getTask(position));
        args.putInt("mode", CreateTaskModel.MODE_EDIT);
        mView.launchActivityForResult(args, CreateTaskActivity.class, REQUEST_CODE_EDIT);
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

    @Override

    public void resetStart(int position) {
        Task task = getTask(position);
        mTaskModel.resetStart(task);
        mTaskReminderMode.cancelReminder(task.getId());
    }

    @Override
    public void resetEnd(int position) {
        Task task = getTask(position);
        mTaskModel.resetStart(task);
        mTaskReminderMode.cancelReminder(task.getId());
        mTaskReminderMode.scheduleEndReminder(task.getId(), task.getScheduledDuration());
    }


    @Override
    public void saveInstanceState(Bundle outState, int firstVisiblePosition) {
        outState.putInt(PARAM_FIRST_VISIBLE_POSITION, firstVisiblePosition);
        outState.putParcelableArrayList(PARAM_TASKS, mAdapter.getItems());
    }

    @Override
    public void setSavedViewState(Bundle savedInstanceState) {
        mSavedInstanceState = savedInstanceState;
    }

    private Task getTask(int position) {
        return mAdapter.getItem(position);
    }

    public void setTaskReminderMode(TaskRemindersModel mTaskReminderMode) {
        this.mTaskReminderMode = mTaskReminderMode;
    }

    public void setTaskStatusModel(TaskModel mTaskModel) {
        this.mTaskModel = mTaskModel;
    }

    public void setRecordsModel(TaskRecordsModel mRecordsModel) {
        this.mRecordsModel = mRecordsModel;
    }


}
