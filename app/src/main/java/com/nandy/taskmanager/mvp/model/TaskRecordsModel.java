package com.nandy.taskmanager.mvp.model;

import android.content.Context;

import com.nandy.taskmanager.db.AppDatabase;
import com.nandy.taskmanager.db.dao.StatisticsDao;
import com.nandy.taskmanager.db.dao.TasksDao;
import com.nandy.taskmanager.model.Task;

import java.util.List;

/**
 * Created by yana on 18.01.18.
 */

public class TaskRecordsModel {

    private final TasksDao mTasksDao;
    private final StatisticsDao mStatisticsDao;

    public TaskRecordsModel(Context context){

        mTasksDao = AppDatabase.getInstance(context).tasksDao();
        mStatisticsDao = AppDatabase.getInstance(context).statisticsDao();
    }

    public List<Task> selectAll(){
        return mTasksDao.getAll();
    }

    public void insert(Task task){
        mTasksDao.insert(task);
    }

    public void update(Task task){
        mTasksDao.update(task);
    }

    public void clearAll(){
        mTasksDao.deleteAll();
        mStatisticsDao.deleteAll();
    }


}
