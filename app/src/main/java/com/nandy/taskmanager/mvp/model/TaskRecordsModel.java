package com.nandy.taskmanager.mvp.model;

import android.content.Context;

import com.nandy.taskmanager.db.AppDatabase;
import com.nandy.taskmanager.db.dao.EventsDao;
import com.nandy.taskmanager.db.dao.StatisticsDao;
import com.nandy.taskmanager.db.dao.TasksDao;
import com.nandy.taskmanager.model.Statistics;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.model.TaskEvent;

import java.util.List;

/**
 * Created by yana on 18.01.18.
 */

public class TaskRecordsModel {

    private final EventsDao mEventsDao;
    private final TasksDao mTasksDao;
    private final StatisticsDao mStatisticsDao;

    public TaskRecordsModel(Context context) {

        mTasksDao = AppDatabase.getInstance(context).tasksDao();
        mStatisticsDao = AppDatabase.getInstance(context).statisticsDao();
        mEventsDao = AppDatabase.getInstance(context).taskEventsDao();
    }

    public List<Task> selectAll() {
        return mTasksDao.getAll();
    }


    public List<Task> selectAllWithLocation() {
        return mTasksDao.selectAllWithLocation();
    }

    public void insert(Task task) {
        mTasksDao.insert(task);
    }

    public void update(Task task, TaskEvent event){
        update(task);
        addEvent(event);
    }

    public void update(Task task) {
        mTasksDao.update(task);
    }

    public void delete(Task task){

        mStatisticsDao.delete(task.getId());
        mEventsDao.delete(task.getId());
        mTasksDao.delete(task);
    }

    public void clearAll() {
        mTasksDao.deleteAll();
        mStatisticsDao.deleteAll();
        mEventsDao.deleteAll();
    }

    public void addEvent(TaskEvent event){
        mEventsDao.insert(event);
    }

    public void addStatistics(Statistics statistics){
        mStatisticsDao.insert(statistics);
    }

    public List<Task> getById(long id){
        return mTasksDao.getById(id);
    }


}
