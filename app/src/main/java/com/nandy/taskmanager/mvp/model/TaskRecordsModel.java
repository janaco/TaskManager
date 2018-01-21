package com.nandy.taskmanager.mvp.model;

import android.content.Context;

import com.nandy.taskmanager.db.AppDatabase;
import com.nandy.taskmanager.db.dao.StatisticsDao;
import com.nandy.taskmanager.db.dao.TasksDao;
import com.nandy.taskmanager.model.Action;
import com.nandy.taskmanager.model.Statistics;
import com.nandy.taskmanager.model.Task;

/**
 * Created by yana on 18.01.18.
 */

public class TaskRecordsModel {

    private TasksDao mTasksDao;
    private StatisticsDao mStatisticsDao;

    public TaskRecordsModel(Context context){

        mTasksDao = AppDatabase.getInstance(context).tasksDao();
        mStatisticsDao = AppDatabase.getInstance(context).statisticsDao();
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

    public void start(long taskIs, long timestamp){
        mStatisticsDao.insert(new Statistics(taskIs, timestamp, Action.START));
    }

    public void end(long taskId, long timestamp){
        mStatisticsDao.insert(new Statistics(taskId, timestamp, Action.END));
    }
}
