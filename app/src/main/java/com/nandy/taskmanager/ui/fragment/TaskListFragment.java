package com.nandy.taskmanager.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.nandy.taskmanager.R;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.mvp.contract.TaskListContract;
import com.nandy.taskmanager.mvp.model.TaskRecordsModel;
import com.nandy.taskmanager.mvp.model.TaskRemindersModel;
import com.nandy.taskmanager.mvp.model.TaskModel;
import com.nandy.taskmanager.mvp.presenter.TaskListPresenter;

import org.kaerdan.presenterretainer.PresenterFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yana on 21.01.18.
 */

public class TaskListFragment
        extends PresenterFragment<TaskListContract.Presenter, TaskListContract.View>
        implements TaskListContract.View {

    public static final int REQUEST_CREATE_TASK = 101;
    public static final String PARAM_TASK = "task";

    @BindView(R.id.list_tasks)
    ListView mTaskLisView;
    @BindView(R.id.layout_progress)
    View mProgressView;
    @BindView(R.id.txt_message)
    TextView mNoTasksTextView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasks_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        registerForContextMenu(mTaskLisView);

        mSwipeRefreshLayout.setOnRefreshListener(() -> getPresenter().refresh());
        mTaskLisView.setOnItemClickListener((adapterView, itemView, position, l) ->
                getPresenter().openDetails(position));

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

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CREATE_TASK) {
            Task task = data.getParcelableExtra(PARAM_TASK);
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
    public void setListViewVisible(boolean visible) {
        mTaskLisView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setNoTasksMessageVisible(boolean visible) {
        mNoTasksTextView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setProgressViewVisible(boolean visible) {
        mProgressView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    protected TaskListContract.Presenter onCreatePresenter() {
        TaskListPresenter presenter = new TaskListPresenter();
        presenter.setTaskStatusModel(new TaskModel(getContext()));

        return presenter;
    }

    @Override
    protected TaskListFragment getPresenterView() {
        return this;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        getPresenter().saveInstanceState(outState, mTaskLisView.getFirstVisiblePosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void scrollToPosition(int position) {
        mTaskLisView.setSelection(position);
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        mSwipeRefreshLayout.setRefreshing(refreshing);
    }
}