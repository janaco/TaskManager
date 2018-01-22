package com.nandy.taskmanager.mvp.model;

import android.content.Context;

import com.nandy.taskmanager.db.AppDatabase;
import com.nandy.taskmanager.db.dao.StatisticsDao;
import com.nandy.taskmanager.db.dao.TasksDao;
import com.nandy.taskmanager.model.Action;
import com.nandy.taskmanager.model.Statistics;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.model.TaskStatus;

/**
 * Created by razomer on 22.01.18.
 */

public class TaskStatusModel {

    private StatisticsDao mStatisticsDao;
    private TasksDao mTasksDao;


    public TaskStatusModel(Context context){
        mStatisticsDao = AppDatabase.getInstance(context).statisticsDao();
    }

    public void start(Task task){
        mStatisticsDao.insert(new Statistics(task.getId(), System.currentTimeMillis(), Action.START));
    }

    public void complete(Task task){
        mStatisticsDao.insert(new Statistics(task.getId(), System.currentTimeMillis(), Action.END));
    }

}
