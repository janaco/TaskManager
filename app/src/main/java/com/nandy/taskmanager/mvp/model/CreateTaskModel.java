package com.nandy.taskmanager.mvp.model;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.nandy.taskmanager.db.AppDatabase;
import com.nandy.taskmanager.db.dao.TasksDao;
import com.nandy.taskmanager.model.RepeatPeriod;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.model.TaskStatus;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by yana on 16.01.18.
 */

public class CreateTaskModel {

    public static final int MODE_CREATE = 1;
    public static final int MODE_EDIT = 2;

    private Date mStartDate;
    private LatLng mLocation;
    private String mImage;
    private long mDuration;
    private RepeatPeriod mRepeatPeriod;

    private Task mTask;

    private int mMode;

    public CreateTaskModel(Task task, int mode) {
        mMode = mode;
        mTask = task;

        if (task != null) {
            mStartDate = task.getStartDate();
            mLocation = task.getLocation();
            mImage = task.getImage();
            mDuration = task.getMaxDuration();
            mRepeatPeriod = task.getPeriod();
        }
    }

    public int getMode() {
        return mMode;
    }

    public Task getTask() {
        return mTask;
    }

    public Task create(String title, String description) {

        if (mMode == MODE_CREATE) {
            mTask = new Task(title, description);
            mTask.setStatus(TaskStatus.NEW);
        } else {
            mTask.setTitle(title);
            mTask.setDescription(description);
        }

        mTask.setStartDate(mStartDate);
        mTask.setEndDate(calculateEndDate(mStartDate, mDuration));
        mTask.setLocation(mLocation);
        mTask.setImage(mImage);
        mTask.setMaxDuration(mDuration);
        mTask.setPeriod(mRepeatPeriod);

        return mTask;

    }

    private Date calculateEndDate(Date startDate, long duration){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.MILLISECOND,(int) duration);

        return calendar.getTime();
    }

    public void setStartDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();

        if (mStartDate != null) {
            calendar.setTime(mStartDate);
        }

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        setStartDate(calendar.getTime());
    }


    public void setStartTime(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();

        if (mStartDate != null) {
            calendar.setTime(mStartDate);
        }

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        setStartDate(calendar.getTime());
    }

    private void setStartDate(Date date) {
        if (mStartDate != null) {
            mStartDate.setTime(date.getTime());
        } else {
            mStartDate = date;
        }
    }

    public void setDuration(long duration){
        mDuration = duration;
    }

    public void setRepeatPeriod(RepeatPeriod repeatPeriod){
        mRepeatPeriod = repeatPeriod;

    }

    public Date getStartDate() {
        return mStartDate;
    }


    public void setLocation(LatLng location) {
        this.mLocation = location;
    }

    public void clearLocation() {
        mLocation = null;
    }

    public void setImage(String mImage) {
        this.mImage = mImage;
    }


}
