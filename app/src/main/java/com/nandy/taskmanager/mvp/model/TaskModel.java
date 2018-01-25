package com.nandy.taskmanager.mvp.model;

import android.content.Context;
import android.support.annotation.Nullable;

import com.nandy.taskmanager.db.AppDatabase;
import com.nandy.taskmanager.db.dao.EventsDao;
import com.nandy.taskmanager.db.dao.StatisticsDao;
import com.nandy.taskmanager.db.dao.TasksDao;
import com.nandy.taskmanager.enums.Action;
import com.nandy.taskmanager.model.Metadata;
import com.nandy.taskmanager.model.Statistics;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.model.TaskEvent;
import com.nandy.taskmanager.enums.TaskStatus;

import java.util.Date;
import java.util.List;

/**
 * Created by razomer on 22.01.18.
 */

public class TaskModel {

    private TaskRemindersModel mTaskReminderModel;

    private final EventsDao mEventsDao;
    private final TasksDao mTasksDao;
    private final StatisticsDao mStatisticsDao;
    private Task mTask;

    public TaskModel(Context context, Task task) {
        this(context);
        mTask = task;
    }

    public TaskModel(Context context) {
        mEventsDao = AppDatabase.getInstance(context).taskEventsDao();
        mTasksDao = AppDatabase.getInstance(context).tasksDao();
        mStatisticsDao = AppDatabase.getInstance(context).statisticsDao();
        mTaskReminderModel = new TaskRemindersModel(context);
    }


    public void resetStart(Task task) {
        Metadata metadata = task.getMetadata();
        metadata.setTimeSpent(0);
        metadata.setActualStartDate(null);
        task.setMetadata(metadata);

        task.setStatus(TaskStatus.NEW);
        mTasksDao.update(task);
        mTaskReminderModel.cancelReminders(task.getId());

        if (task.getPlannedStartDate().after(new Date())){
            mTaskReminderModel.scheduleStartReminder(task);
        }

    }

    public void resetEnd(Task task) {
        Metadata metadata = task.getMetadata();
        metadata.setTimeSpent(0);
        task.setMetadata(metadata);

        task.setStatus(TaskStatus.ACTIVE);
        mTasksDao.update(task);
        mTaskReminderModel.scheduleEndReminder(task.getId(), task.getScheduledDuration());
    }

    public void resetStart() {
        resetStart(mTask);
    }

    public void resetEnd() {
        resetEnd(mTask);
    }

    public void start() {
        start(mTask);
    }


    public void start(Task task) {

        task.setStatus(TaskStatus.ACTIVE);
        Metadata metadata = task.getMetadata();
        if (metadata == null) {
            metadata = new Metadata();
        }
        metadata.setActualStartDate(new Date());
        task.setMetadata(metadata);

        mTasksDao.update(task);
        mEventsDao.insert(new TaskEvent(task.getId(), System.currentTimeMillis(), Action.START));
        mTaskReminderModel.scheduleEndReminder(task.getId(), task.getScheduledDuration());

    }

    public void complete() {
        complete(mTask);
    }

    public void complete(Task task) {

        Metadata metadata = task.getMetadata();
        Date actualStartDate = metadata.getActualStartDate();
        long duration = System.currentTimeMillis() - actualStartDate.getTime();
        long downtime = 0;

        switch (task.getStatus()) {

            case ACTIVE:
                downtime = metadata.getDownTime();
                break;

            case PAUSED:
                downtime = duration - metadata.getTimeSpent();
                break;
        }

        long timeSpent = duration - downtime;

        metadata.setTimeSpent(timeSpent);
        metadata.setDownTime(downtime);
        task.setMetadata(metadata);

        task.setStatus(TaskStatus.COMPLETED);
        mTasksDao.update(task);
        mEventsDao.insert(new TaskEvent(task.getId(), System.currentTimeMillis(), Action.END));
        mStatisticsDao.insert(new Statistics(task.getId(), actualStartDate, timeSpent));
    }

    public void pause(Task task) {
        task.setStatus(TaskStatus.PAUSED);

        Metadata metadata = task.getMetadata();
        Date actualStartDate = metadata.getActualStartDate();
        long downtime = metadata.getDownTime();
        long duration = System.currentTimeMillis() - actualStartDate.getTime();
        long timeSpent = duration - downtime;

        metadata.setTimeSpent(timeSpent);

        task.setMetadata(metadata);

        mTasksDao.update(task);
        mEventsDao.insert(new TaskEvent(task.getId(), System.currentTimeMillis(), Action.PAUSE));
        mTaskReminderModel.cancelReminders(task.getId());
    }

    public void pause(){
        pause(mTask);
    }

    public void resume(){
        resume(mTask);
    }
    public void resume(Task task) {
        task.setStatus(TaskStatus.ACTIVE);

        Metadata metadata = task.getMetadata();
        Date actualStartDate = metadata.getActualStartDate();
        long timeSpent = metadata.getTimeSpent();
        long duration = System.currentTimeMillis() - actualStartDate.getTime();
        long downtime = duration - timeSpent;
        metadata.setDownTime(downtime);

        task.setMetadata(metadata);

        mTasksDao.update(task);
        mEventsDao.insert(new TaskEvent(task.getId(), System.currentTimeMillis(), Action.RESUME));

        long timeToComplete = task.getScheduledDuration() - timeSpent;
        mTaskReminderModel.scheduleEndReminder(task.getId(), timeToComplete);
    }

    public void delete() {
        delete(mTask);
    }

    public void delete(Task task) {
        mTaskReminderModel.cancelReminders(task.getId());
        mTasksDao.delete(task);
        mStatisticsDao.deleteAll(task.getId());
    }

    public Task getTask() {
        return mTask;
    }

    public void setTask(Task task) {
        mTask = task;
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
