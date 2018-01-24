package com.nandy.taskmanager.mvp.model;

import com.google.android.gms.maps.model.LatLng;
import com.nandy.taskmanager.model.RepeatPeriod;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.model.TaskStatus;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by yana on 16.01.18.
 */

public class CreateTaskModel {


    private final int mMode;

    private Task mTask;

    public CreateTaskModel(Task task, int mode) {
        mMode = mode;
        mTask = task;

        if (task == null) {
            mTask = new Task(System.currentTimeMillis());
            mTask.setStatus(TaskStatus.NEW);
        }
    }

    public int getMode() {
        return mMode;
    }

    public Task getTask() {
        return mTask;
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

    public void setLocation(LatLng location) {
        mTask.setLocation(location);
    }

    public void clearLocation() {
        mTask.setLocation(null);
    }

    public void setImage(String image) {
        mTask.setImage(image);
    }


}
