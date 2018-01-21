package com.nandy.taskmanager.mvp.model;

import android.content.Context;
import android.os.Bundle;

import com.nandy.taskmanager.adapter.TasksAdapter;
import com.nandy.taskmanager.db.AppDatabase;
import com.nandy.taskmanager.db.dao.TasksDao;
import com.nandy.taskmanager.model.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by yana on 16.01.18.
 */

public class TasksListModel {

    private TasksAdapter mAdapter;
    private ArrayList<Task> mTasks;

    public TasksListModel(Context context){
        mTasks = new ArrayList<>();
        mAdapter = new TasksAdapter(context, mTasks);
    }


    public TasksAdapter getAdapter() {
        return mAdapter;
    }

    public void clearAll(){
        mTasks.clear();
        mAdapter.clear();
    }

    public Task get(int position){
        return mTasks.get(position);
    }

    public void display(Task task){
        mAdapter.add(task);
    }

    public void displayAll(Collection<Task> tasks){
        mTasks.addAll(tasks);
        mAdapter.notifyDataSetChanged();
    }

    public void refreshList(Collection<Task> tasks){
        mTasks.clear();
        mAdapter.clear();
        displayAll(tasks);
    }

    public void saveInstanceState(Bundle outState){
        outState.putParcelableArrayList("tasks", mTasks);
    }

    public void restoreInstanceState(Bundle savedInstanceState){
        ArrayList<Task> tasksList = savedInstanceState.getParcelableArrayList("tasks");
        mTasks.addAll(tasksList);
    }

    public int getTasksCount(){
        return mTasks.size();
    }
}
