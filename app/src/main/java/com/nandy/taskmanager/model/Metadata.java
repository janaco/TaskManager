package com.nandy.taskmanager.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Ignore;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by razomer on 22.01.18.
 */

public class Metadata implements Parcelable{

    @ColumnInfo(name = "actual_start_date")
    private Date mActualStartDate;
    @ColumnInfo(name = "time_spent")
    private long mTimeSpent;

    public Metadata(){}

    @Ignore
    protected Metadata(Parcel in) {
        mTimeSpent = in.readLong();
        mActualStartDate = (Date) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mTimeSpent);
        dest.writeSerializable(mActualStartDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Metadata> CREATOR = new Creator<Metadata>() {
        @Override
        public Metadata createFromParcel(Parcel in) {
            return new Metadata(in);
        }

        @Override
        public Metadata[] newArray(int size) {
            return new Metadata[size];
        }
    };

    public void setActualStartDate(Date mActualStartDate) {
        this.mActualStartDate = mActualStartDate;
    }

    public void setTimeSpent(long mTimeSpent) {
        this.mTimeSpent = mTimeSpent;
    }

    public Date getActualStartDate() {
        return mActualStartDate;
    }

    public long getTimeSpent() {
        return mTimeSpent;
    }
}
