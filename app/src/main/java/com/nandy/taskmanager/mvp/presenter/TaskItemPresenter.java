package com.nandy.taskmanager.mvp.presenter;

import android.app.Activity;
import android.content.Intent;

import com.nandy.taskmanager.R;
import com.nandy.taskmanager.activity.TaskDetailsActivity;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.model.TaskStatus;
import com.nandy.taskmanager.mvp.BasePresenter;
import com.nandy.taskmanager.mvp.model.DateFormatModel;
import com.nandy.taskmanager.mvp.model.TaskModel;
import com.nandy.taskmanager.mvp.model.TaskRemindersModel;
import com.nandy.taskmanager.mvp.view.TaskDetailsView;

/**
 * Created by razomer on 18.01.18.
 */

public class TaskItemPresenter extends BasePresenter {

    private final TaskDetailsView mView;

    private TaskModel mTaskModel;
    private TaskRemindersModel mTaskReminderModel;
    private DateFormatModel mDateFormatModel;

    public TaskItemPresenter(TaskDetailsView view) {
        mView = view;
    }

    @Override
    public void start() {
        displayData(mTaskModel.getTask());
    }

    @Override
    public void stop() {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && requestCode == TaskDetailsActivity.REQUEST_CODE_EDIT) {
            Task task = data.getParcelableExtra("task");
            mTaskModel.setTask(task);
            displayData(task);
        }
    }

    public Task getTask() {
        return mTaskModel.getTask();
    }

    private void displayData(Task task) {

        mView.setTitle(task.getTitle());
        mView.setDescription(task.getDescription());
        mView.setPlannedStartTime(mDateFormatModel.formatAsFullDate(task.getPlannedStartDate()));
        mView.setScheduledDuration(mDateFormatModel.convertToMinutes(task.getScheduledDuration()), R.string.minutes);
        mView.setRepeatPeriod(task.getRepeatPeriod().getTextResId());

        displayLocation(task);
        loadImage(task);
        displayStatusInformation(task);
    }

    private void displayStatusInformation(Task task) {
        mView.setStatus(task.getStatus().name());

        switch (task.getStatus()) {

            case NEW:
                mView.setControlButtonText(R.string.start);
                mView.setControlButtonEnabled(true);
                mView.setActualStartDateVisible(false);
                mView.setTimeSpentVisible(false);
                break;

            case ACTIVE:
                mView.setControlButtonEnabled(true);
                mView.setControlButtonText(R.string.finish);
                mView.setActualStartDateVisible(true);
                mView.setTimeSpentVisible(false);
                mView.setActualStartTime(mDateFormatModel.formatAsFullDate(task.getMetadata().getActualStartDate()));
                break;

            case PAUSED:
                mView.setControlButtonEnabled(true);
                mView.setControlButtonText(R.string.finish);
                mView.setActualStartDateVisible(true);
                mView.setActualStartTime(mDateFormatModel.formatAsFullDate(task.getMetadata().getActualStartDate()));
                mView.setTimeSpentVisible(true);
                mView.setTimeSpent(mDateFormatModel.convertToSeconds(task.getMetadata().getTimeSpent()), R.string.hour);

                break;

            case COMPLETED:
                mView.setControlButtonEnabled(false);
                mView.setControlButtonText(R.string.completed);
                mView.setActualStartDateVisible(true);
                mView.setTimeSpentVisible(true);
                mView.setTimeSpent(mDateFormatModel.convertToSeconds(task.getMetadata().getTimeSpent()), R.string.hour);
                mView.setActualStartTime(mDateFormatModel.formatAsFullDate(task.getMetadata().getActualStartDate()));
                break;
        }
    }

    public void setupMenu() {
        Task task = mTaskModel.getTask();
        mView.setResetStartMenuOptionEnabled(task.getStatus() != TaskStatus.NEW);
        mView.setResetEndMenuOptionEnabled(task.getStatus() == TaskStatus.COMPLETED);
        mView.setPauseOptionVisible(task.getStatus() == TaskStatus.ACTIVE);
        mView.setResumeOptionVisible(task.getStatus() == TaskStatus.PAUSED);
    }

    private void displayLocation(Task task) {
        if (task.hasLocation()) {
            mView.setLocationVisible(true);
            mView.setLocation(task.getLocation().toString());
        } else {
            mView.setLocationVisible(false);
        }
    }

    private void loadImage(Task task) {
        if (task.hasImage()) {
            mView.loadImage(task.getImage(), task.hasLocation());
        } else {
            mView.loadImage(R.mipmap.ic_task, task.hasLocation());
        }
    }

    public void toggleStatus() {

        switch (mTaskModel.getTask().getStatus()) {

            case NEW:
                mTaskModel.start();
                mTaskReminderModel.scheduleStartReminder(mTaskModel.getTask());
                break;

            case ACTIVE:
                mTaskModel.complete();
                mTaskReminderModel.cancelReminder(mTaskModel.getTask().getId());
                break;
        }

        displayStatusInformation(mTaskModel.getTask());
        setupMenu();
    }

    public void delete() {
        mTaskReminderModel.cancelReminder(mTaskModel.getTask().getId());
        mTaskModel.delete();
        mView.finish();

//        if (task.hasLocation()){
//            mShceduleModel.scheduleLocationUpdates();
//        }
    }


    public void resetStart() {
        mTaskModel.resetStart();
        mTaskReminderModel.cancelReminder(mTaskModel.getTask().getId());
        displayData(mTaskModel.getTask());
        setupMenu();
    }

    public void resetEnd() {
        Task task = mTaskModel.getTask();
        mTaskModel.resetEnd();
        mTaskReminderModel.cancelReminder(task.getId());
        mTaskReminderModel.scheduleEndReminder(task.getId(), task.getScheduledDuration());
        displayData(mTaskModel.getTask());
        setupMenu();
    }

    public void pause(){
        mTaskModel.pause();
        mTaskReminderModel.cancelReminder(mTaskModel.getTask().getId());
        displayData(mTaskModel.getTask());
        setupMenu();
    }

    public void resume(){
        mTaskModel.resume();
        Task task = mTaskModel.getTask();
        long duration = task.getScheduledDuration() - task.getMetadata().getTimeSpent();
        mTaskReminderModel.scheduleEndReminder(task.getId(), duration);
        displayData(mTaskModel.getTask());
        setupMenu();
    }

    public void setTaskModel(TaskModel mTaskModel) {
        this.mTaskModel = mTaskModel;
    }

    public void setTaskReminderModel(TaskRemindersModel mTaskReminderModel) {
        this.mTaskReminderModel = mTaskReminderModel;
    }

    public void setDateFormatModel(DateFormatModel mDateFormatModel) {
        this.mDateFormatModel = mDateFormatModel;
    }
}
