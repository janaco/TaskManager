package com.nandy.taskmanager.mvp.model;

import com.nandy.taskmanager.enums.RepeatPeriod;
import com.nandy.taskmanager.model.Location;
import com.nandy.taskmanager.model.Metadata;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.enums.TaskStatus;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by yana on 16.01.18.
 */

public class CreateTaskModel {

    private final int mMode;

    private final Task mTask;

    public CreateTaskModel(Task task, int mode) {
        mMode = mode;

        if (task == null) {
            mTask = new Task(System.currentTimeMillis());
            mTask.setStatus(TaskStatus.NEW);
            mTask.setRepeatPeriod(RepeatPeriod.NO_REPEAT);
        } else {
            mTask = task;
        }
    }

    public int getMode() {
        return mMode;
    }

    public Task getTask() {
        return mTask;
    }

    public void setTitle(String title){
        mTask.setTitle(title);
    }

    public void setDescription(String description){
        mTask.setDescription(description);
    }


    public void setStartDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();

        if (mTask.getPlannedStartDate() != null) {
            calendar.setTime(mTask.getPlannedStartDate());
        }

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        setStartDate(calendar.getTime());
    }


    public void setStartTime(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();

        if (mTask.getPlannedStartDate() != null) {
            calendar.setTime(mTask.getPlannedStartDate());
        }

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        setStartDate(calendar.getTime());
    }

    private void setStartDate(Date date) {
        mTask.setPlannedStartDate(date);
    }

    public void setDuration(long duration) {
        mTask.setScheduledDuration(duration);
    }

    public void setRepeatPeriod(RepeatPeriod repeatPeriod) {
        mTask.setRepeatPeriod(repeatPeriod);
    }

    public void setLocation(Location address) {
        Metadata metadata = mTask.getMetadata();
        if (metadata == null){
            metadata = new Metadata();
        }
        metadata.setLocation(address);
        mTask.setMetadata(metadata);
    }

    public void clearLocation() {
        if (mTask.hasMetadata()){
            Metadata metadata = mTask.getMetadata();
            metadata.setLocation(null);
            mTask.setMetadata(metadata);
        }
    }

    public void setImage(String image) {
        mTask.setImage(image);
    }


}
