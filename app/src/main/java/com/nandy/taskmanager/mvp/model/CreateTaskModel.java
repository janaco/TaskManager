package com.nandy.taskmanager.mvp.model;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.nandy.taskmanager.db.AppDatabase;
import com.nandy.taskmanager.db.dao.TasksDao;
import com.nandy.taskmanager.model.Task;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by yana on 16.01.18.
 */

public class CreateTaskModel {

    private TasksDao mTasksDao;

    private Date mStartDate;
    private Date mEndDate;
    private LatLng mLocation;
    private String mImage;

    public CreateTaskModel(Context context) {
        mTasksDao = AppDatabase.getInstance(context).tasksDao();
    }


    public Task create(String title, String description){

        Task task = new Task(title,description);
        task.setStartDate(mStartDate);
        task.setEndDate(mEndDate);
        task.setLocation(mLocation);
        task.setImage(mImage);

        return task;
    }

    public void save(Task task) {
        mTasksDao.insert(task);
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
