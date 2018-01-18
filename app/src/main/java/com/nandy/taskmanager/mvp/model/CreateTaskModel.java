package com.nandy.taskmanager.mvp.model;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.nandy.taskmanager.db.AppDatabase;
import com.nandy.taskmanager.db.dao.TasksDao;
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
    private Date mEndDate;
    private LatLng mLocation;
    private String mImage;

    private Task mTask;

    private int mMode;

    public CreateTaskModel(Task task, int mode) {
        mMode = mode;
        mTask = task;

        if (task != null) {
            mStartDate = task.getStartDate();
            mEndDate = task.getEndDate();
            mLocation = task.getLocation();
            mImage = task.getImage();
        }
    }

    public int getMode() {
        return mMode;
    }

    public Task getTask() {
        return mTask;
    }

    public Task create(String title, String description){

        if (mMode == MODE_CREATE) {
            mTask = new Task(title, description);
            mTask.setStatus(TaskStatus.NEW);
        }else {
            mTask.setTitle(title);
            mTask.setDescription(description);
        }

        mTask.setStartDate(mStartDate);
        mTask.setEndDate(mEndDate);
        mTask.setLocation(mLocation);
        mTask.setImage(mImage);

        return mTask;

    }

    public void setStartDateAndTime(int year, int month, int day, int hour, int minute) {

        if (mStartDate != null){
            mStartDate.setTime(getTime(year, month, day, hour, minute));
        }else {
            mStartDate = new Date(getTime(year, month, day, hour, minute));
        }
    }

    public void setEndDateAndTime(int year, int month, int day, int hour, int minute) {

        if (mEndDate != null){
            mEndDate.setTime(getTime(year, month, day, hour, minute));
        }else {
            mEndDate = new Date(getTime(year, month, day, hour, minute));
        }
    }

    public void clearStartDate(){
        mStartDate = null;
    }

    public void clearEndDate(){
        mEndDate = null;
    }


    public Date getStartDate() {
        return mStartDate;
    }

    public Date getEndDate() {
        return mEndDate;
    }

    public void setLocation(LatLng location) {
        this.mLocation = location;
    }

    public void clearLocation(){
        mLocation = null;
    }

    public void setImage(String mImage) {
        this.mImage = mImage;
    }

    private long getTime(int year, int month, int day, int hour, int minute){
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        return calendar.getTimeInMillis();
    }

}
