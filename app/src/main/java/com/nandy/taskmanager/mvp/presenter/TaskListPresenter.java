package com.nandy.taskmanager.mvp.presenter;

import android.os.Bundle;
import android.util.Log;

import com.daimajia.swipe.util.Attributes;
import com.nandy.taskmanager.Constants;
import com.nandy.taskmanager.SubscriptionUtils;
import com.nandy.taskmanager.activity.CreateTaskActivity;
import com.nandy.taskmanager.activity.TaskDetailsActivity;
import com.nandy.taskmanager.adapter.TasksAdapter;
import com.nandy.taskmanager.enums.TaskStatus;
import com.nandy.taskmanager.eventbus.TaskListChangedEvent;
import com.nandy.taskmanager.eventbus.TasksCleanedEvent;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.mvp.contract.TaskListContract;
import com.nandy.taskmanager.mvp.model.TaskModel;
import com.nandy.taskmanager.mvp.model.TaskRecordsModel;
import com.nandy.taskmanager.mvp.model.TaskRemindersModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yana on 16.01.18.
 */

public class TaskListPresenter implements TaskListContract.Presenter, TasksAdapter.OnItemOptionSelectedListener {

    private TaskListContract.View mView;
    private TaskModel mTaskModel;

    private TasksAdapter mAdapter;

    private Bundle mSavedInstanceState;

    private Disposable mLoadTasksSubscription;
    private Disposable mRemoveTaskSubscription;
    private Disposable mTaskStatusSubscription;

    @Override
    public void onAttachView(TaskListContract.View view) {
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
        List<Task> tasks = mSavedInstanceState.getParcelableArrayList(Constants.PARAM_TASKS);
        mAdapter.setItems(tasks);
        mView.scrollToPosition(mSavedInstanceState.getInt(Constants.PARAM_FIRST_VISIBLE_POSITION));
        mSavedInstanceState = null;
    }

    @Override
    public void onDetachView() {
        mView = null;
        SubscriptionUtils.dispose(mLoadTasksSubscription);
        SubscriptionUtils.dispose(mRemoveTaskSubscription);
        SubscriptionUtils.dispose(mTaskStatusSubscription);
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void refresh() {
        loadTasks(true);
    }

    @Override
    public void resume() {
        EventBus.getDefault().register(this);
        loadTasks(false);

    }

    @Override
    public void pause() {
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTaskListChangedEvent(TaskListChangedEvent event) {
        mAdapter.addAll(event.getTasks());
        mView.setListViewVisible(mAdapter.getCount() > 0);
        mView.setNoTasksMessageVisible(mAdapter.getCount() == 0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTasksCleanedEvent(TasksCleanedEvent event) {
        mAdapter.clearAll();
        mView.setNoTasksMessageVisible(mAdapter.getCount() == 0);
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

    private void loadTasks(boolean byUser) {

        mLoadTasksSubscription =
                mTaskModel.getAll()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                            if (byUser) {
                                mView.setRefreshing(true);
                            } else {
                                mView.setListViewVisible(mAdapter.getCount() > 0);
                                mView.setProgressViewVisible(mAdapter.getCount() == 0);
                            }

                        })
                        .doFinally(() -> {
                            mView.setProgressViewVisible(false);
                            mView.setRefreshing(false);
                        })
                        .subscribe(tasks ->

                        {
                            mAdapter.setItems(tasks);
                            mView.setListViewVisible(true);
                            mView.setNoTasksMessageVisible(tasks.size() == 0);

                        }, Throwable::printStackTrace);
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
        mRemoveTaskSubscription = mTaskModel.delete(task)
                .subscribe(() -> {
                    mAdapter.remove(position);

                    if (mAdapter.getCount() == 0) {
                        mView.setNoTasksMessageVisible(true);
                    }
                }, Throwable::printStackTrace);

    }

    @Override
    public void edit(int position) {
        onEditOptionSelected(getTask(position), position);
    }

    @Override
    public void onEditOptionSelected(Task task, int position) {
        Bundle args = new Bundle();
        args.putParcelable(Constants.PARAM_TASK, getTask(position));
        args.putInt(Constants.PARAM_MODE, Constants.MODE_EDIT);
        mView.launchActivityForResult(args, CreateTaskActivity.class, Constants.REQUEST_CODE_EDIT);
    }

    @Override
    public void onToggleStatus(Task task, int position) {

        switch (task.getStatus()) {

            case NEW:
                mTaskStatusSubscription = mTaskModel.start(task)
                        .subscribe(result -> updateListItem(result, position), Throwable::printStackTrace);
                break;

            case ACTIVE:
                mTaskStatusSubscription = mTaskModel.complete(task)
                        .subscribe(result -> updateListItem(result, position), Throwable::printStackTrace);
                break;
        }

    }


    @Override
    public void resetStart(int position) {
        mTaskStatusSubscription = mTaskModel.resetStart(getTask(position))
                .subscribe(task -> updateListItem(task, position), Throwable::printStackTrace);
    }

    private void updateListItem(Task task, int position) {
        mAdapter.set(task, position);
    }

    @Override
    public void resetEnd(int position) {
        mTaskStatusSubscription = mTaskModel.resetEnd(getTask(position))
                .subscribe(task -> updateListItem(task, position));

    }

    @Override
    public void saveInstanceState(Bundle outState, int firstVisiblePosition) {
        outState.putInt(Constants.PARAM_FIRST_VISIBLE_POSITION, firstVisiblePosition);
        outState.putParcelableArrayList(Constants.PARAM_TASKS, mAdapter.getItems());
    }

    @Override
    public void setSavedViewState(Bundle savedInstanceState) {
        mSavedInstanceState = savedInstanceState;
    }

    private Task getTask(int position) {
        return mAdapter.getItem(position);
    }

    public void setTaskStatusModel(TaskModel taskModel) {
        mTaskModel = taskModel;
    }


}
