package com.nandy.taskmanager.mvp.model;

import android.content.Context;
import android.support.annotation.Nullable;

import com.nandy.taskmanager.db.AppDatabase;
import com.nandy.taskmanager.db.dao.StatisticsDao;
import com.nandy.taskmanager.db.dao.TasksDao;
import com.nandy.taskmanager.model.Action;
import com.nandy.taskmanager.model.Metadata;
import com.nandy.taskmanager.model.Statistics;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.model.TaskStatus;

import java.util.Date;
import java.util.List;

/**
 * Created by razomer on 22.01.18.
 */

public class TaskModel {

    private StatisticsDao mStatisticsDao;
    private TasksDao mTasksDao;
    private Task mTask;

    public TaskModel(Context context, Task task){
        mTask = task;
        mStatisticsDao = AppDatabase.getInstance(context).statisticsDao();
        mTasksDao = AppDatabase.getInstance(context).tasksDao();
    }

    public TaskModel(Context context){
        mStatisticsDao = AppDatabase.getInstance(context).statisticsDao();
        mTasksDao = AppDatabase.getInstance(context).tasksDao();
    }


    public void resetStart(Task task) {
        Metadata metadata = task.getMetadata();
        metadata.setTimeSpent(0);
        metadata.setActualStartDate(null);
        task.setMetadata(metadata);

        task.setStatus(TaskStatus.NEW);
        mTasksDao.update(task);
    }

    public void resetEnd(Task task) {
        Metadata metadata = task.getMetadata();
        metadata.setTimeSpent(0);
        task.setMetadata(metadata);

        task.setStatus(TaskStatus.ACTIVE);
        mTasksDao.update(task);
    }

    public void resetStart() {
      resetStart(mTask);
    }

    public void resetEnd() {
      resetEnd(mTask);
    }

    public void start(){
        start(mTask);
    }


    public void start(Task task){

        task.setStatus(TaskStatus.ACTIVE);
        Metadata metadata  = task.getMetadata();
        if (metadata == null){
            metadata = new Metadata();
        }
        metadata.setActualStartDate(new Date());
        task.setMetadata(metadata);

        mTasksDao.update(task);
        mStatisticsDao.insert(new Statistics(task.getId(), System.currentTimeMillis(), Action.START));
    }

    public void complete(){
        complete(mTask);
    }

    public void complete(Task task){

        task.setStatus(TaskStatus.COMPLETED);

        Metadata metadata = task.getMetadata();
        Date actualStartDate = metadata.getActualStartDate();
        long spentTime = System.currentTimeMillis() - actualStartDate.getTime();
        metadata.setTimeSpent(spentTime);

        task.setMetadata(metadata);

        mTasksDao.update(task);
        mStatisticsDao.insert(new Statistics(task.getId(), System.currentTimeMillis(), Action.END));
    }

    public void delete(){
        delete(mTask);
    }

    public void delete(Task task){
        mTasksDao.delete(task);
    }

    public Task getTask() {
        return mTask;
    }

    public void setTask(Task mTask) {
        this.mTask = mTask;
    }

    @Nullable
    public Task getTask(long taskId) {
        List<Task> tasks = mTasksDao.getById(taskId);

        if (tasks.size() > 0) {
            return tasks.get(0);
        } else {
            return null;
        }
    }
}
