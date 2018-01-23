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

    public static final int MODE_CREATE = 1;
    public static final int MODE_EDIT = 2;

    private final int mMode;
    private long mDuration;

    private Task mTask;
    private Date mStartDate;
    private LatLng mLocation;
    private RepeatPeriod mRepeatPeriod;
    private String mImage;

    public CreateTaskModel(Task task, int mode) {
        mMode = mode;
        mTask = task;

        if (task != null) {
            mStartDate = task.getPlannedStartDate();
            mLocation = task.getLocation();
            mImage = task.getImage();
            mDuration = task.getScheduledDuration();
            mRepeatPeriod = task.getRepeatPeriod();
        }
    }

    public int getMode() {
        return mMode;
    }

    public Task getTask() {
        return mTask;
    }

    public Task createOrUpdate(String title, String description) {

        if (mMode == MODE_CREATE) {
            mTask = new Task(System.currentTimeMillis(), title, description);
            mTask.setStatus(TaskStatus.NEW);

        } else {
            mTask.setTitle(title);
            mTask.setDescription(description);
        }

        mTask.setPlannedStartDate(mStartDate);
        mTask.setLocation(mLocation);
        mTask.setImage(mImage);
        mTask.setScheduledDuration(mDuration);
        mTask.setRepeatPeriod(mRepeatPeriod);

        return mTask;

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
