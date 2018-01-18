package com.nandy.taskmanager.mvp.model;

import android.content.Context;

import com.nandy.taskmanager.db.AppDatabase;
import com.nandy.taskmanager.db.dao.TasksDao;
import com.nandy.taskmanager.model.Task;

/**
 * Created by yana on 18.01.18.
 */

public class TaskRecordsModel {

    private TasksDao mTasksDao;

    public TaskRecordsModel(Context context){

        mTasksDao = AppDatabase.getInstance(context).tasksDao();
    }

    public void delete(Task task){
        mTasksDao.delete(task);
    }

    public void insert(Task task){
        mTasksDao.insert(task);
    }

    public void update(Task task){
        mTasksDao.update(task);
    }

    public void clearAll(){
        mTasksDao.deleteAll();
    }
}
