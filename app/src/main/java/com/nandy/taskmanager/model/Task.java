package com.nandy.taskmanager.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

@Entity(tableName = "tasks")
public class Task implements Parcelable{

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private long mId;
    @ColumnInfo(name = "title")
    private String mTitle;
    @ColumnInfo(name = "description")
    private String mDescription;
    @ColumnInfo(name = "image")
    private String mImage;

    @ColumnInfo(name = "location")
    private LatLng mLocation;

    @ColumnInfo(name = "date_start")
    private Date mStartDate;
    @ColumnInfo(name = "date_end")
    private Date mEndDate;

    @ColumnInfo(name = "status")
    private TaskStatus mStatus;

    @ColumnInfo(name = "max_duration")
    private long mMaxDuration;

    @ColumnInfo(name = "repeat_period")
    private RepeatPeriod mPeriod;


    public Task(long id, String title, String description) {
        mId = id;
        mTitle = title;
        mDescription = description;
    }

    protected Task(Parcel in) {
        mId = in.readLong();
        mTitle = in.readString();
        mDescription = in.readString();
        mImage = in.readString();
        mLocation = in.readParcelable(LatLng.class.getClassLoader());
        mMaxDuration = in.readLong();
        mStartDate = (Date) in.readSerializable();
        mEndDate = (Date) in.readSerializable();
        mStatus = TaskStatus.valueOf(in.readString());
        mPeriod = RepeatPeriod.valueOf(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mTitle);
        dest.writeString(mDescription);
        dest.writeString(mImage);
        dest.writeParcelable(mLocation, flags);
        dest.writeLong(mMaxDuration);
        dest.writeSerializable(mStartDate);
        dest.writeSerializable(mEndDate);
        dest.writeString(mStatus.name());
        dest.writeString(mPeriod.name());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public boolean isPeriodical() {
        return mPeriod != RepeatPeriod.NO_REPEAT;
    }

    public long getMaxDuration() {
        return mMaxDuration;
    }

    public void setMaxDuration(long mMaxDuration) {
        this.mMaxDuration = mMaxDuration;
    }

    public RepeatPeriod getPeriod() {
        return mPeriod;
    }

    public void setPeriod(RepeatPeriod mPeriod) {
        this.mPeriod = mPeriod;
    }

    public void setLocation(LatLng mLocation) {
        this.mLocation = mLocation;
    }

    public LatLng getLocation() {
        return mLocation;
    }

    public boolean hasLocation(){
        return mLocation != null;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public long getId() {
        return mId;
    }

    public TaskStatus getStatus() {
        return mStatus;
    }

    public void setStatus(TaskStatus mStatus) {
        this.mStatus = mStatus;
    }

    public void setId(@NonNull long mId) {
        this.mId = mId;
    }

    public String getImage() {
        return mImage;
    }

    public boolean hasImage(){
        return mImage != null;
    }

    public void setImage(String mImage) {
        this.mImage = mImage;
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public void setStartDate(Date mStartDate) {
        this.mStartDate = mStartDate;
    }

    public Date getEndDate() {
        return mEndDate;
    }

    public void setEndDate(Date mEndDate) {
        this.mEndDate = mEndDate;
    }

    @Override
    public String toString() {
        return "Task{" +
                "mId='" + mId + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mImage='" + mImage + '\'' +
                ", mLocation=" + mLocation +
                ", mStartDate=" + mStartDate +
                ", mEndDate=" + mEndDate +
                ", mStatus=" + mStatus +
                '}';
    }
}
