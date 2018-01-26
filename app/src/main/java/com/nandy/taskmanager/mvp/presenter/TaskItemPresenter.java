package com.nandy.taskmanager.mvp.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.nandy.taskmanager.Constants;
import com.nandy.taskmanager.R;
import com.nandy.taskmanager.SubscriptionUtils;
import com.nandy.taskmanager.activity.CreateTaskActivity;
import com.nandy.taskmanager.activity.TaskDetailsActivity;
import com.nandy.taskmanager.eventbus.TaskChangedEvent;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.enums.TaskStatus;
import com.nandy.taskmanager.mvp.contract.TaskDetailsContract;
import com.nandy.taskmanager.mvp.model.DateFormatModel;
import com.nandy.taskmanager.mvp.model.TaskModel;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by razomer on 18.01.18.
 */

public class TaskItemPresenter implements TaskDetailsContract.Presenter {

    private TaskDetailsContract.View mView;

    private TaskModel mTaskModel;
    private DateFormatModel mDateFormatModel;

    private Disposable mTaskStatusSubscriprion;
    private Disposable mDeleteTaskSubscription;

    @Override
    public void onAttachView(TaskDetailsContract.View view) {
        mView = view;
        displayData(mTaskModel.getTask());
    }

    @Override
    public void onDetachView() {
    }

    @Override
    public void onDestroy() {
        mView = null;
        SubscriptionUtils.dispose(mTaskStatusSubscriprion);
        SubscriptionUtils.dispose(mDeleteTaskSubscription);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE_EDIT) {
            Task task = data.getParcelableExtra(Constants.PARAM_TASK);
            mTaskModel.setTask(task);
            displayData(task);
        }
    }


    private void displayData(Task task) {

        mView.setTitle(task.getTitle());
        mView.setDescription(task.getDescription());
        mView.setPlannedStartTime(mDateFormatModel.formatAsFullDate(task.getPlannedStartDate()));
        mView.setScheduledDuration(task.getScheduledDuration().getTextResId());
        mView.setRepeatPeriod(task.getRepeatPeriod().getTextResId());

        displayLocation(task);
        loadImage(task);
        displayStatusInformation(task);
    }

    @Override
    public void edit() {
        Bundle args = new Bundle();
        args.putParcelable(Constants.PARAM_TASK, mTaskModel.getTask());
        args.putInt(Constants.PARAM_MODE, Constants.MODE_EDIT);

        mView.launchActivityForResult(args, CreateTaskActivity.class, Constants.REQUEST_CODE_EDIT);
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
                mView.setTimeSpent(mDateFormatModel.formatDuration(task.getMetadata().getTimeSpent()));

                break;

            case COMPLETED:
                mView.setControlButtonEnabled(false);
                mView.setControlButtonText(R.string.completed);
                mView.setActualStartDateVisible(true);
                mView.setTimeSpentVisible(true);
                mView.setTimeSpent(mDateFormatModel.formatDuration(task.getMetadata().getTimeSpent()));
                mView.setActualStartTime(mDateFormatModel.formatAsFullDate(task.getMetadata().getActualStartDate()));
                break;
        }
    }

    @Override
    public void setupMenu() {
        setupMenu(mTaskModel.getTask());
    }

    private void setupMenu(Task task) {
        mView.setResetStartMenuOptionEnabled(task.getStatus() != TaskStatus.NEW);
        mView.setResetEndMenuOptionEnabled(task.getStatus() == TaskStatus.COMPLETED);
        mView.setPauseOptionVisible(task.getStatus() == TaskStatus.ACTIVE);
        mView.setResumeOptionVisible(task.getStatus() == TaskStatus.PAUSED);
    }

    private void displayLocation(Task task) {
        if (task.hasLocation()) {
            mView.setLocationVisible(true);
            mView.setLocation(task.getMetadata().getLocation().getAddress());
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
                mTaskStatusSubscriprion =
                        Single.create((SingleOnSubscribe<Task>) e -> e.onSuccess(mTaskModel.start()))
                                .doOnSuccess(updatedTask -> EventBus.getDefault().post(new TaskChangedEvent(updatedTask)))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(this::onTaskStatusChanged, Throwable::printStackTrace);
                break;

            case ACTIVE:
                mTaskStatusSubscriprion =
                        Single.create((SingleOnSubscribe<Task>) e -> e.onSuccess(mTaskModel.complete()))
                                .doOnSuccess(updatedTask -> EventBus.getDefault().post(new TaskChangedEvent(updatedTask)))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(this::onTaskStatusChanged, Throwable::printStackTrace);
                break;
        }
    }

    public void delete() {
        mDeleteTaskSubscription = mTaskModel.delete().subscribe(() -> mView.finish(), Throwable::printStackTrace);
    }


    public void resetStart() {
        mTaskStatusSubscriprion = mTaskModel.resetStart().subscribe(this::onTaskStatusChanged, Throwable::printStackTrace);
    }

    public void resetEnd() {
        mTaskStatusSubscriprion = mTaskModel.resetEnd().subscribe(this::onTaskStatusChanged, Throwable::printStackTrace);
    }

    public void pause() {
        mTaskStatusSubscriprion = mTaskModel.pause().subscribe(this::onTaskStatusChanged, Throwable::printStackTrace);
    }

    public void resume() {
        mTaskStatusSubscriprion = mTaskModel.resume().subscribe(this::onTaskStatusChanged, Throwable::printStackTrace);
    }

    private void onTaskStatusChanged(Task task) {
        displayData(task);
        setupMenu(task);
    }

    public void setTaskModel(TaskModel taskModel) {
        mTaskModel = taskModel;
    }

    public void setDateFormatModel(DateFormatModel dateFormatModel) {
        mDateFormatModel = dateFormatModel;
    }

}
