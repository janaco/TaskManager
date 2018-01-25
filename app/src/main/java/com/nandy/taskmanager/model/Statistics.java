package com.nandy.taskmanager.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by yana on 22.01.18.
 */

@Entity(tableName = "statistics")
public class Statistics implements Parcelable{

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private long mId;
    @ColumnInfo(name = "id_task")
    private long mTaskId;
    @ColumnInfo(name = "spent_time")
    private long mSpentTime;
    @ColumnInfo(name = "start_date")
    private Date mStartDate;

    public Statistics(long taskId, Date startDate, long spentTime) {
        mTaskId = taskId;
        mSpentTime = spentTime;
        mStartDate = startDate;
    }

    protected Statistics(Parcel in) {
        mId = in.readLong();
        mTaskId = in.readLong();
        mSpentTime = in.readLong();
        mStartDate = (Date) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeLong(mTaskId);
        dest.writeLong(mSpentTime);
        dest.writeSerializable(mStartDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Statistics> CREATOR = new Creator<Statistics>() {
        @Override
        public Statistics createFromParcel(Parcel in) {
            return new Statistics(in);
        }

        @Override
        public Statistics[] newArray(int size) {
            return new Statistics[size];
        }
    };

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public long getTaskId() {
        return mTaskId;
    }

    public void setTaskId(long taskId) {
        mTaskId = taskId;
    }

    public long getSpentTime() {
        return mSpentTime;
    }

    public void setSpentTime(long spentTime) {
        mSpentTime = spentTime;
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public void setmStartDate(Date startDate) {
        mStartDate = startDate;
    }
}
