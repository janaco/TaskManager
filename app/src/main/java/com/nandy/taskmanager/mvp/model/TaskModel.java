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

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by razomer on 22.01.18.
 */

public class TaskModel {

    private TaskRemindersModel mTaskReminderModel;
    private TaskRecordsModel mRecordsModel;
    private Task mTask;

    public TaskModel(Context context, Task task) {
        this(context);
        mTask = task;
    }

    public TaskModel(Context context) {
        mTaskReminderModel = new TaskRemindersModel(context);
        mRecordsModel = new TaskRecordsModel(context);
    }

    public Single<List<Task>> getAll() {

        return Single.create(e -> e.onSuccess(mRecordsModel.selectAll()));
    }

    public Single<Task> resetStart(Task task) {

        return Single.create((SingleOnSubscribe<Task>) e -> {

            Metadata metadata = task.getMetadata();
            metadata.setTimeSpent(0);
            metadata.setActualStartDate(null);
            task.setMetadata(metadata);

            task.setStatus(TaskStatus.NEW);
            mRecordsModel.update(task);
            mTaskReminderModel.cancelReminders(task.getId());

            if (task.getPlannedStartDate().after(new Date())) {
                mTaskReminderModel.scheduleStartReminder(task);
            }

            e.onSuccess(task);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public Single<Task> resetEnd(Task task) {

        return Single.create((SingleOnSubscribe<Task>) e -> {

            Metadata metadata = task.getMetadata();
            metadata.setTimeSpent(0);
            task.setMetadata(metadata);

            task.setStatus(TaskStatus.ACTIVE);
            mRecordsModel.update(task);
            mTaskReminderModel.scheduleEndReminder(task.getId(), task.getScheduledDuration());

            e.onSuccess(task);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Task> resetStart() {
        return resetStart(mTask);
    }

    public Single<Task> resetEnd() {
        return resetEnd(mTask);
    }

    public Single<Task> start() {
        return start(mTask);
    }


    public Single<Task> start(Task task) {

        return Single.create((SingleOnSubscribe<Task>) e -> {

            task.setStatus(TaskStatus.ACTIVE);
            Metadata metadata = task.getMetadata();
            if (metadata == null) {
                metadata = new Metadata();
            }
            metadata.setActualStartDate(new Date());
            task.setMetadata(metadata);

            mRecordsModel.update(task, new TaskEvent(task.getId(), System.currentTimeMillis(), Action.START));
            mTaskReminderModel.scheduleEndReminder(task.getId(), task.getScheduledDuration());

            e.onSuccess(task);
        })  .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Task> complete() {
        return complete(mTask);
    }

    public Single<Task> complete(Task task) {

        return Single.create((SingleOnSubscribe<Task>)e -> {

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
            mRecordsModel.update(task, new TaskEvent(task.getId(), System.currentTimeMillis(), Action.END));
            mRecordsModel.addStatistics(new Statistics(task.getId(), actualStartDate, timeSpent));

            e.onSuccess(task);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Task> pause(Task task) {

        return Single.create((SingleOnSubscribe<Task>)e -> {

            task.setStatus(TaskStatus.PAUSED);

            Metadata metadata = task.getMetadata();
            Date actualStartDate = metadata.getActualStartDate();
            long downtime = metadata.getDownTime();
            long duration = System.currentTimeMillis() - actualStartDate.getTime();
            long timeSpent = duration - downtime;

            metadata.setTimeSpent(timeSpent);

            task.setMetadata(metadata);

            mRecordsModel.update(task, new TaskEvent(task.getId(), System.currentTimeMillis(), Action.PAUSE));
            mTaskReminderModel.cancelReminders(task.getId());

            e.onSuccess(task);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Task> pause() {
        return pause(mTask);
    }

    public Single<Task> resume() {
        return resume(mTask);
    }

    public Single<Task> resume(Task task) {

        return Single.create((SingleOnSubscribe<Task>)e -> {

            task.setStatus(TaskStatus.ACTIVE);

            Metadata metadata = task.getMetadata();
            Date actualStartDate = metadata.getActualStartDate();
            long timeSpent = metadata.getTimeSpent();
            long duration = System.currentTimeMillis() - actualStartDate.getTime();
            long downtime = duration - timeSpent;
            metadata.setDownTime(downtime);

            task.setMetadata(metadata);

            mRecordsModel.update(task, new TaskEvent(task.getId(), System.currentTimeMillis(), Action.RESUME));

            long timeToComplete = task.getScheduledDuration() - timeSpent;
            mTaskReminderModel.scheduleEndReminder(task.getId(), timeToComplete);
            e.onSuccess(task);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable delete() {
        return delete(mTask);
    }

    public Completable delete(Task task) {

        return Completable.create(e -> {

            mTaskReminderModel.cancelReminders(task.getId());
            mRecordsModel.delete(task);

            e.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public void setTask(Task task) {
        mTask = task;
    }

    public Task getTask(){
        return mTask;
    }

    @Nullable
    public Task getTask(long taskId) {
        List<Task> tasks = mRecordsModel.getById(taskId);

        if (tasks.size() > 0) {
            return tasks.get(0);
        } else {
            return null;
        }
    }
}
