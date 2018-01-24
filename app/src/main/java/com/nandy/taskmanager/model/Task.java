package com.nandy.taskmanager.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

@Entity(tableName = "tasks")
public class Task implements Parcelable {

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

    @ColumnInfo(name = "planned_start_date")
    private Date mPlannedStartDate;

    @ColumnInfo(name = "status")
    private TaskStatus mStatus;

    @ColumnInfo(name = "scheduled_duration")
    private long mScheduledDuration;

    @ColumnInfo(name = "repeat_period")
    private RepeatPeriod mRepeatPeriod;

    @Embedded
    private Metadata mMetadata;


    public Task(long id) {
        mId = id;
    }

    protected Task(Parcel in) {
        mId = in.readLong();
        mTitle = in.readString();
        mDescription = in.readString();
        mImage = in.readString();
        mLocation = in.readParcelable(LatLng.class.getClassLoader());
        mScheduledDuration = in.readLong();
        mPlannedStartDate = (Date) in.readSerializable();
        mStatus = TaskStatus.valueOf(in.readString());
        mRepeatPeriod = RepeatPeriod.valueOf(in.readString());
        mMetadata = in.readParcelable(Metadata.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mTitle);
        dest.writeString(mDescription);
        dest.writeString(mImage);
        dest.writeParcelable(mLocation, flags);
        dest.writeLong(mScheduledDuration);
        dest.writeSerializable(mPlannedStartDate);
        dest.writeString(mStatus.name());
        dest.writeString(mRepeatPeriod.name());
        dest.writeParcelable(mMetadata, flags);
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
        return mRepeatPeriod != RepeatPeriod.NO_REPEAT;
    }

    public long getScheduledDuration() {
        return mScheduledDuration;
    }

    public void setScheduledDuration(long mMaxDuration) {
        this.mScheduledDuration = mMaxDuration;
    }

    public RepeatPeriod getRepeatPeriod() {
        return mRepeatPeriod;
    }

    public void setRepeatPeriod(RepeatPeriod mPeriod) {
        this.mRepeatPeriod = mPeriod;
    }

    public void setLocation(LatLng mLocation) {
        this.mLocation = mLocation;
    }

    public LatLng getLocation() {
        return mLocation;
    }

    public boolean hasLocation() {
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

    public boolean hasImage() {
        return mImage != null;
    }

    public void setImage(String mImage) {
        this.mImage = mImage;
    }

    public Date getPlannedStartDate() {
        return mPlannedStartDate;
    }

    public void setPlannedStartDate(Date mStartDate) {
        this.mPlannedStartDate = mStartDate;
    }

    public Metadata getMetadata() {
        return mMetadata;
    }


    public void setMetadata(Metadata mMetadata) {
        this.mMetadata = mMetadata;
    }


    @Override
    public String toString() {
        return "Task{" +
                "mId='" + mId + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mImage='" + mImage + '\'' +
                ", mLocation=" + mLocation +
                ", mPlannedStartDate=" + mPlannedStartDate +
                ", mStatus=" + mStatus +
                '}';
    }
}
