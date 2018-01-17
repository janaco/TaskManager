package com.nandy.taskmanager.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

@Entity(tableName = "tasks")
public class Task implements Parcelable{

    @PrimaryKey
    @NonNull
    private String mId;
    private String mTitle;
    private String mComment;
    
    private LatLng mLocation;

    public Task(String title, String comment) {
        mTitle = title;
        mComment = comment;
        mId = String.valueOf(title.hashCode()).concat(String.valueOf(comment.hashCode()));
    }

    @Ignore
    public Task(String id, String title, String comment){
        mId = id;
        mTitle = title;
        mComment = comment;
    }


    protected Task(Parcel in) {
        mId = in.readString();
        mTitle = in.readString();
        mComment = in.readString();
        mLocation = in.readParcelable(LatLng.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeString(mComment);
        dest.writeParcelable(mLocation, flags);
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

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + mTitle + '\'' +
                ", comment='" + mComment + '\'' +
                '}';
    }
}
