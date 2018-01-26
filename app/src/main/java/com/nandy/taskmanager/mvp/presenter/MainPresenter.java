package com.nandy.taskmanager.mvp.presenter;

import android.os.Bundle;
import android.util.Log;

import com.nandy.taskmanager.Constants;
import com.nandy.taskmanager.OnServiceConnectedListener;
import com.nandy.taskmanager.SubscriptionUtils;
import com.nandy.taskmanager.activity.CreateTaskActivity;
import com.nandy.taskmanager.activity.SettingsActivity;
import com.nandy.taskmanager.eventbus.TaskChangedEvent;
import com.nandy.taskmanager.eventbus.TaskListChangedEvent;
import com.nandy.taskmanager.eventbus.TaskRemovedEvent;
import com.nandy.taskmanager.eventbus.TasksCleanedEvent;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.mvp.contract.MainActivityContract;
import com.nandy.taskmanager.mvp.model.DummyDataModel;
import com.nandy.taskmanager.mvp.model.TaskRecordsModel;
import com.nandy.taskmanager.mvp.model.TasksInRadiusTrackingModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.kaerdan.presenterretainer.Presenter;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yana on 21.01.18.
 */

public class MainPresenter implements Presenter<MainActivityContract.View>,
        MainActivityContract.Presenter, OnServiceConnectedListener {

    private MainActivityContract.View mView;

    private DummyDataModel mDummyDataMode;
    private TaskRecordsModel mRecordsModel;
    private TasksInRadiusTrackingModel mTaskTrakingModel;

    private Disposable mClearListSubscription;
    private Disposable mDummyDataSubscription;

    @Override
    public void onAttachView(MainActivityContract.View view) {
        mView = view;
        mTaskTrakingModel.startTracking();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetachView() {
        mView = null;
        mTaskTrakingModel.unbind();
        SubscriptionUtils.dispose(mClearListSubscription);
        SubscriptionUtils.dispose(mDummyDataSubscription);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        mTaskTrakingModel.destroy();
        mTaskTrakingModel = null;
    }

    @Override
    public void onServiceConnected() {
        updateTaskWithLocationList();
    }

    private void updateTaskWithLocationList(){
        Single.create((SingleOnSubscribe<List<Task>>) e -> e.onSuccess(mRecordsModel.selectAllWithLocation()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tasks -> mTaskTrakingModel.setTasks(tasks));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTaskListChanged(TasksCleanedEvent event){
        updateTaskWithLocationList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTasksCleanedEvent(){
        mTaskTrakingModel.onTasksCleaned();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTaskChangedEvent(TaskChangedEvent event){
        mTaskTrakingModel.onTaskChanged(event.task);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTaskRemovedEvent(TaskRemovedEvent event){
        mTaskTrakingModel.onTaskRemoved(event.task);
    }

    @Override
    public void onCreateTaskClick() {
        Bundle args = new Bundle();
        args.putInt(Constants.PARAM_MODE, Constants.MODE_CREATE);
        mView.launchActivityForResult(args, CreateTaskActivity.class, Constants.REQUEST_CREATE_TASK);
    }

    @Override
    public void onFillListOptionSelected() {

        mDummyDataSubscription = Single.create(
                (SingleOnSubscribe<List<Task>>) e -> e.onSuccess(mDummyDataMode.generateDummyData()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tasks -> EventBus.getDefault().post(new TaskListChangedEvent(tasks)),
                        Throwable::printStackTrace);
    }

    @Override
    public void onClearAllOptionSelected() {

        mClearListSubscription = Completable
                .create(e -> {
                    mRecordsModel.clearAll();
                    e.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> EventBus.getDefault().post(new TasksCleanedEvent()), Throwable::printStackTrace);
    }

    @Override
    public void onSettingsOptionSelected() {
        mView.launchActivity(null, SettingsActivity.class);
    }

    @Override
    public void onExitOptionSelected() {
        mView.finish();
    }

    public void setDummyDataMode(DummyDataModel dummyDataMode) {
        mDummyDataMode = dummyDataMode;
    }

    public void setRecordsModel(TaskRecordsModel recordsModel) {
        mRecordsModel = recordsModel;
    }

    public void setTaskInRadiusTrakingModel(TasksInRadiusTrackingModel taskInRadiusTrakingModel){
        mTaskTrakingModel = taskInRadiusTrakingModel;
    }

}
