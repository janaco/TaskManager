package com.nandy.taskmanager.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yana on 21.01.18.
 */

@Entity(tableName = "statistics")
public class Statistics implements Parcelable {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private long mId;
    @ColumnInfo(name = "id_task")
    private long mTaskId;
    @ColumnInfo(name = "timestamp")
    private long mTime;
    @ColumnInfo(name = "action")
    private Action mAction;

    public Statistics(long taskId, long time, Action action) {
        mTaskId = taskId;
        mTime = time;
        mAction = action;
    }

    protected Statistics(Parcel in) {
        mId = in.readLong();
        mTaskId = in.readLong();
        mTime = in.readLong();
        mAction = Action.valueOf(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeLong(mTaskId);
        dest.writeLong(mTime);
        dest.writeString(mAction.name());
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

    public void setId(long mId) {
        this.mId = mId;
    }

    public long getTaskId() {
        return mTaskId;
    }

    public void setTaskId(long mTaskId) {
        this.mTaskId = mTaskId;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long mTime) {
        this.mTime = mTime;
    }

    public Action getAction() {
        return mAction;
    }

    public void setAction(Action mAction) {
        this.mAction = mAction;
    }
}
