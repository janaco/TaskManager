package com.nandy.taskmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nandy.taskmanager.Constants;
import com.nandy.taskmanager.R;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.mvp.model.CreateTaskModel;
import com.nandy.taskmanager.mvp.model.DummyDataModel;
import com.nandy.taskmanager.mvp.model.TasksListModel;
import com.nandy.taskmanager.mvp.presenter.TasksPresenter;
import com.nandy.taskmanager.mvp.view.TasksListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListActivity extends AppCompatActivity implements TasksListView {


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

        listTasks.setOnItemClickListener((adapterView, view, position, l) ->
                openDetails(mTasksPresenter.getArguments(position)));

        mTasksPresenter = new TasksPresenter(this);
        mTasksPresenter.setTasksListModel(new TasksListModel(getApplicationContext()));
        mTasksPresenter.setDummyDataModel(new DummyDataModel(getApplicationContext()));

        mTasksPresenter.start();

        if (savedInstanceState == null) {
            mTasksPresenter.loadTasks();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mTasksPresenter.refreshList();
    }

    private void openDetails(Bundle args) {
        Intent intent = new Intent(this, TaskDetailsActivity.class);
        intent.putExtras(args);

        startActivity(intent);
    }

    @OnClick(R.id.btn_create_task)
    void onCreateTaskClick() {
        Intent intent = new Intent(getApplicationContext(), CreateTaskActivity.class);
        intent.putExtra("mode", CreateTaskModel.MODE_CREATE);
        startActivityForResult(intent, Constants.REQUEST_CREATE_TASK);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_backup:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;

            case R.id.action_create_task:
                onCreateTaskClick();
                return true;

            case R.id.action_fill_list:
                mTasksPresenter.generateDummyData();
                return true;

            case R.id.action_clear_all:
                mTasksPresenter.clearAllTasks();
                return true;

            case R.id.action_exit:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
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

        if (resultCode == RESULT_OK && requestCode == Constants.REQUEST_CREATE_TASK) {
            Task task = data.getParcelableExtra(Constants.TASK);
            mTasksPresenter.displayTask(task);

        }
    }

    @Override
    public <T extends ArrayAdapter> void setAdapter(T adapter) {
        listTasks.setAdapter(adapter);
    }
}
