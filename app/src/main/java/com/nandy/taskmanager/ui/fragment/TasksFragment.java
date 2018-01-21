package com.nandy.taskmanager.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.nandy.taskmanager.activity.TaskDetailsActivity;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.mvp.model.TaskRecordsModel;
import com.nandy.taskmanager.mvp.presenter.TasksPresenter;
import com.nandy.taskmanager.mvp.view.TasksListView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yana on 21.01.18.
 */

public class TasksFragment extends Fragment implements TasksListView {


    @BindView(R.id.list_tasks)
    ListView mTaskLisView;

    private TasksPresenter mTasksPresenter;


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
                openDetails(mTasksPresenter.getArguments(position)));


        mTasksPresenter.start();

        if (savedInstanceState == null) {
            mTasksPresenter.loadTasks();
        } else {
            mTasksPresenter.restoreInstanceState(savedInstanceState);
        }

    }


    public void setTasksPresenter(TasksPresenter mTasksPresenter) {
        this.mTasksPresenter = mTasksPresenter;
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
                return true;


            case R.id.action_edit:
                return true;


            case R.id.action_reset_start:
                return true;

            case R.id.action_reset_end:
                return true;

            default:
                return super.onContextItemSelected(item);

        }

    }


    @Override
    public void onResume() {
        super.onResume();
        mTasksPresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mTasksPresenter.pause();
    }

    private void openDetails(Bundle args) {
        Intent intent = new Intent(getContext(), TaskDetailsActivity.class);
        intent.putExtras(args);

        startActivity(intent);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        mTasksPresenter.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CREATE_TASK) {
            Task task = data.getParcelableExtra(Constants.TASK);
            mTasksPresenter.displayTask(task);

        }
    }

    @Override
    public <T extends BaseSwipeAdapter> void setAdapter(T adapter) {
        mTaskLisView.setAdapter(adapter);
    }

    public static TasksFragment newInstance(Context context) {

        TasksFragment fragment = new TasksFragment();

        TasksPresenter presenter = new TasksPresenter(fragment);
        presenter.setRecordsModel(new TaskRecordsModel(context));

        fragment.setTasksPresenter(presenter);

        return fragment;

    }
}