package com.nandy.taskmanager.mvp.presenter;

import android.app.Activity;
import android.content.Intent;

import com.nandy.taskmanager.R;
import com.nandy.taskmanager.activity.TaskDetailsActivity;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.model.TaskStatus;
import com.nandy.taskmanager.mvp.BasePresenter;
import com.nandy.taskmanager.mvp.model.DateFormatModel;
import com.nandy.taskmanager.mvp.model.TaskDetailsModel;
import com.nandy.taskmanager.mvp.model.TaskScheduleModel;
import com.nandy.taskmanager.mvp.model.TaskRecordsModel;
import com.nandy.taskmanager.mvp.view.TaskDetailsView;

import java.util.Locale;

/**
 * Created by razomer on 18.01.18.
 */

public class TaskDetailsPresenter extends BasePresenter {

    private TaskDetailsView mView;
    private TaskDetailsModel mDetailsModel;
    private DateFormatModel mDateFormatModel;
    private TaskRecordsModel mRecordsModel;
    private TaskScheduleModel mShceduleModel;

    public TaskDetailsPresenter(TaskDetailsView view) {
        mView = view;
    }

    public void setDetailsModel(TaskDetailsModel mDetailsModel) {
        this.mDetailsModel = mDetailsModel;
    }

    public void setScheduleModel(TaskScheduleModel mShceduleModel) {
        this.mShceduleModel = mShceduleModel;
    }

    public void setDateFormatModel(DateFormatModel mDateFormatModel) {
        this.mDateFormatModel = mDateFormatModel;
    }

    public void setRecordsModel(TaskRecordsModel mRecordsModel) {
        this.mRecordsModel = mRecordsModel;
    }

    @Override
    public void start() {
        displayData(mDetailsModel.getTask());
    }

    @Override
    public void stop() {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && requestCode == TaskDetailsActivity.REQUEST_CODE_EDIT) {
            Task task = data.getParcelableExtra("task");
            mDetailsModel.setTask(task);
            displayData(task);
        }
    }

    public Task getTask() {
        return mDetailsModel.getTask();
    }

    private void displayData(Task task) {

        mView.setStatus(task.getStatus().name());
        mView.setTitle(task.getTitle());
        mView.setDescription(task.getDescription());
        mView.setTime(String.format(Locale.getDefault(), "%s",
                mDateFormatModel.formatAsFullDate(task.getStartDate())));

        if (task.hasLocation()) {
            mView.setLocation(task.getLocation().toString());
        }

        if (task.hasImage()) {
            mView.loadImage(task.getImage(), task.hasLocation());
        } else {
            mView.loadImage(R.mipmap.ic_task, task.hasLocation());
        }

        mView.setControlButtonEnabled(task.getStatus() != TaskStatus.COMPLETED);

        if (task.getStatus() == TaskStatus.NEW) {
            mView.setControlButtonText(R.string.start);
        } else {
            mView.setControlButtonText(R.string.finish);
        }

        setupControlButton(task.getStatus());
    }

    private void setupControlButton(TaskStatus status) {
        mView.setControlButtonEnabled(status != TaskStatus.COMPLETED);

        switch (status) {

            case NEW:
                mView.setControlButtonText(R.string.start);
                break;

            case COMPLETED:
                mView.setControlButtonText(R.string.completed);
                break;

            default:
                mView.setControlButtonText(R.string.finish);
                break;
        }
    }

    public void toggleStatus() {
        Task task = mDetailsModel.getTask();
        TaskStatus currentStatus = task.getStatus();
        TaskStatus newStatus;

        switch (currentStatus) {

            case NEW:
                newStatus = TaskStatus.ACTIVE;
                mShceduleModel.scheduleTaskAutoComplete(task.getId(), task.getMaxDuration());
                mRecordsModel.start(task.getId(), System.currentTimeMillis());
                break;

            case ACTIVE:
                newStatus = TaskStatus.COMPLETED;
                mRecordsModel.end(task.getId(), System.currentTimeMillis());
                break;

            default:
                newStatus = TaskStatus.NEW;
                break;
        }

        mDetailsModel.setStatus(newStatus);
        mRecordsModel.update(mDetailsModel.getTask());
        mView.setStatus(newStatus.name());
        setupControlButton(newStatus);

    }

    public void delete() {
        Task task = mDetailsModel.getTask();
        mShceduleModel.cancelReminder((int) task.getId());
        mRecordsModel.delete(task);

        if (task.hasLocation()){
            mShceduleModel.scheduleLocationUpdates();
        }
    }
}
