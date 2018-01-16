package com.nandy.taskmanager.mvp.model;

import android.content.Context;

import com.nandy.taskmanager.db.AppDatabase;
import com.nandy.taskmanager.db.dao.TasksDao;
import com.nandy.taskmanager.model.Task;

/**
 * Created by yana on 16.01.18.
 */

public class CreateTaskModel {

    private TasksDao tasksDao;

    public CreateTaskModel(Context context) {
        tasksDao = AppDatabase.getInstance(context).tasksDao();
    }

    public void save(Task task) {
        tasksDao.insert(task);
    }
}
