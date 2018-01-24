package com.nandy.taskmanager.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.nandy.taskmanager.Constants;
import com.nandy.taskmanager.R;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.mvp.contract.TasksContract;
import com.nandy.taskmanager.mvp.model.TaskRecordsModel;
import com.nandy.taskmanager.mvp.model.TaskRemindersModel;
import com.nandy.taskmanager.mvp.model.TaskModel;
import com.nandy.taskmanager.mvp.presenter.TasksPresenter;

import org.kaerdan.presenterretainer.PresenterFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yana on 21.01.18.
 */

public class TasksFragment extends PresenterFragment<TasksPresenter, TasksContract.View> implements TasksContract.View {

    @BindView(R.id.list_tasks)
    ListView mTaskLisView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        registerForContextMenu(mTaskLisView);

        mTaskLisView.setOnItemClickListener((adapterView, itemView, position, l) ->
                getPresenter().openDetails(position));

        Log.d("SCREEN_ROTATION_", "onCreate: " + savedInstanceState + "\npresenter: " + getPresenter());
        if (savedInstanceState != null &&
                getPresenter() != null) {
            getPresenter().setSavedViewState(savedInstanceState);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.menu_list_item, menu);
        super.onCreateContextMenu(menu, v, menuInfo);

    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;

        switch (item.getItemId()) {


            case R.id.action_delete:
                getPresenter().delete(position);
                return true;


            case R.id.action_edit:
                getPresenter().edit(position);
                return true;


            case R.id.action_reset_start:
                getPresenter().resetStart(position);
                return true;

            case R.id.action_reset_end:
                getPresenter().resetEnd(position);
                return true;

            default:
                return super.onContextItemSelected(item);

        }
    }


    @Override
    public void launchActivityForResult(Bundle args, Class<?> cls, int requestCode) {
        Intent intent = new Intent(getContext(), cls);
        intent.putExtras(args);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPresenter().resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        getPresenter().pause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CREATE_TASK) {
            Task task = data.getParcelableExtra(Constants.TASK);
            getPresenter().openDetails(task);

        }
    }

    @Override
    public void launchActivity(Bundle args, Class<?> cls) {
        Intent intent = new Intent(getContext(), cls);
        intent.putExtras(args);
        startActivity(intent);
    }

    @Override
    public <T extends BaseSwipeAdapter> void setAdapter(T adapter) {
        mTaskLisView.setAdapter(adapter);
    }

    @Override
    protected TasksPresenter onCreatePresenter() {
        TasksPresenter presenter = new TasksPresenter();
        presenter.setRecordsModel(new TaskRecordsModel(getContext()));
        presenter.setTaskStatusModel(new TaskModel(getContext()));
        presenter.setTaskReminderMode(new TaskRemindersModel(getContext()));

        return presenter;
    }

    @Override
    protected TasksFragment getPresenterView() {
        return this;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (getPresenter() != null) {
            getPresenter().saveInstanceState(outState, mTaskLisView.getFirstVisiblePosition());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void scrollToPosition(int position) {
        mTaskLisView.setSelection(position);
    }
}