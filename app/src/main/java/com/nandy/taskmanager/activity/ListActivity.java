package com.nandy.taskmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.nandy.taskmanager.Constants;
import com.nandy.taskmanager.R;
import com.nandy.taskmanager.adapter.TasksAdapter;
import com.nandy.taskmanager.model.Task;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListActivity extends AppCompatActivity {


    @BindView(R.id.list_tasks)
    ListView listTasks;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private TasksAdapter mTasksAdapter;
    private ArrayList<Task> mTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        mTasks = new ArrayList<>();

        mTasksAdapter = new TasksAdapter(getApplicationContext(), mTasks);
        listTasks.setAdapter(mTasksAdapter);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("tasks", mTasks);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<Task> tasksList = savedInstanceState.getParcelableArrayList("tasks");
        mTasks.addAll(tasksList);
    }

    @OnClick(R.id.btn_create_task)
    void onCreateTaskClick() {
        startActivityForResult(new Intent(getApplicationContext(), TaskActivity.class), Constants.REQUEST_CREATE_TASK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == Constants.REQUEST_CREATE_TASK){

            Task task = data.getParcelableExtra(Constants.TASK);
            mTasks.add(task);

        }
    }
}
