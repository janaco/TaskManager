package com.nandy.taskmanager.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by razomer on 22.01.18.
 */

public class StatisticsResult implements Parcelable{


    private String mTaskId;
    private String mTaskTitle;
    private long mSpentTime;

    public StatisticsResult(String taskId, String taskTitle, long spentTime) {
        this.mTaskId = taskId;
        this.mTaskTitle = taskTitle;
        this.mSpentTime = spentTime;
    }

    protected StatisticsResult(Parcel in) {
        mTaskId = in.readString();
        mTaskTitle = in.readString();
        mSpentTime = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTaskId);
        dest.writeString(mTaskTitle);
        dest.writeLong(mSpentTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StatisticsResult> CREATOR = new Creator<StatisticsResult>() {
        @Override
        public StatisticsResult createFromParcel(Parcel in) {
            return new StatisticsResult(in);
        }

        @Override
        public StatisticsResult[] newArray(int size) {
            return new StatisticsResult[size];
        }
    };

    public String getTaskId() {
        return mTaskId;
    }

    public void setTaskId(String mTaskId) {
        this.mTaskId = mTaskId;
    }

    public String getTaskTitle() {
        return mTaskTitle;
    }

    public void setTaskTitle(String mTaskTitle) {
        this.mTaskTitle = mTaskTitle;
    }

    public long getSpentTime() {
        return mSpentTime;
    }

    public void setSpentTime(long mSpentTime) {
        this.mSpentTime = mSpentTime;
    }
}
