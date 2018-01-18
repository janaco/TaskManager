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
    private String mId;
    @ColumnInfo(name = "title")
    private String mTitle;
    @ColumnInfo(name = "description")
    private String mDescription;
    @ColumnInfo(name = "image")
    private String mImage;

    private LatLng mLocation;

    @ColumnInfo(name = "date_start")
    private Date mStartDate;
    @ColumnInfo(name = "date_end")
    private Date mEndDate;

    @ColumnInfo(name = "status")
    private TaskStatus mStatus;


    public Task(String title, String description) {
        mTitle = title;
        mDescription = description;
        mId = String.valueOf(title.hashCode()).concat(String.valueOf(description.hashCode()));
    }

    @Ignore
    public Task(@NonNull String id, String title, String description){
        mId = id;
        mTitle = title;
        mDescription = description;
    }

    protected Task(Parcel in) {
        mId = in.readString();
        mTitle = in.readString();
        mDescription = in.readString();
        mImage = in.readString();
        mLocation = in.readParcelable(LatLng.class.getClassLoader());
        mStatus = TaskStatus.valueOf(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeString(mDescription);
        dest.writeString(mImage);
        dest.writeParcelable(mLocation, flags);
        dest.writeString(mStatus.name());
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

    public String getId() {
        return mId;
    }

    public TaskStatus getStatus() {
        return mStatus;
    }

    public void setStatus(TaskStatus mStatus) {
        this.mStatus = mStatus;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getImage() {
        return mImage;
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
                "title='" + mTitle + '\'' +
                ", comment='" + mDescription + '\'' +
                '}';
    }
}
