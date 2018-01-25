package com.nandy.taskmanager.mvp.presenter;

import android.os.Bundle;
import android.util.Log;

import com.nandy.taskmanager.Constants;
import com.nandy.taskmanager.SubscriptionUtils;
import com.nandy.taskmanager.activity.CreateTaskActivity;
import com.nandy.taskmanager.activity.SettingsActivity;
import com.nandy.taskmanager.eventbus.TaskListChangedEvent;
import com.nandy.taskmanager.eventbus.TasksCleanedEvent;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.mvp.contract.MainActivityContract;
import com.nandy.taskmanager.mvp.model.DummyDataModel;
import com.nandy.taskmanager.mvp.model.TaskRecordsModel;

import org.greenrobot.eventbus.EventBus;
import org.kaerdan.presenterretainer.Presenter;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yana on 21.01.18.
 */

public class MainPresenter implements Presenter<MainActivityContract.View>, MainActivityContract.Presenter {

    private MainActivityContract.View mView;

    private DummyDataModel mDummyDataMode;
    private TaskRecordsModel mRecordsModel;

    private Disposable mClearListSubscription;
    private Disposable mDummyDataSubscription;

    @Override
    public void onAttachView(MainActivityContract.View view) {
        mView = view;
    }

    @Override
    public void onDetachView() {
        mView = null;
        SubscriptionUtils.dispose(mClearListSubscription);
        SubscriptionUtils.dispose(mDummyDataSubscription);
    }

    @Override
    public void onDestroy() {

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
                .subscribe(
                        () ->
                        {
                            Log.d("EVENT_BUS", "post.TasksCleanedEvent");
                            EventBus.getDefault().post(new TasksCleanedEvent());
                        }, Throwable::printStackTrace
                );
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

}
