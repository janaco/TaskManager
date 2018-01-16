package com.nandy.taskmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nandy.taskmanager.Constants;
import com.nandy.taskmanager.R;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.mvp.model.TasksListModel;
import com.nandy.taskmanager.mvp.presenter.TasksPresenter;
import com.nandy.taskmanager.mvp.view.TasksListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListActivity extends AppCompatActivity implements TasksListView{


    @BindView(R.id.list_tasks)
    ListView listTasks;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private TasksPresenter mTasksPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        mTasksPresenter = new TasksPresenter(this);
        mTasksPresenter.setTasksListModel(new TasksListModel(getApplicationContext()));

        mTasksPresenter.start();

        if (savedInstanceState == null){
            mTasksPresenter.loadTasks();
        }

    }

    @OnClick(R.id.btn_create_task)
    void onCreateTaskClick() {
        startActivityForResult(new Intent(getApplicationContext(), TaskActivity.class), Constants.REQUEST_CREATE_TASK);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mTasksPresenter.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
      mTasksPresenter.restoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == Constants.REQUEST_CREATE_TASK){
            Task task = data.getParcelableExtra(Constants.TASK);
            mTasksPresenter.displayTask(task);

        }
    }

    @Override
    public <T extends ArrayAdapter> void setAdapter(T adapter) {
        listTasks.setAdapter(adapter);
    }
}
