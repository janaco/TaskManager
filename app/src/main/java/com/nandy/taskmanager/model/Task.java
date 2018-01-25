package com.nandy.taskmanager.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.nandy.taskmanager.enums.RepeatPeriod;
import com.nandy.taskmanager.enums.TaskStatus;

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

    public void setScheduledDuration(long scheduledDuration) {
        mScheduledDuration = scheduledDuration;
    }

    public RepeatPeriod getRepeatPeriod() {
        return mRepeatPeriod;
    }

    public void setRepeatPeriod(RepeatPeriod period) {
        mRepeatPeriod = period;
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

    public void setDescription(String description) {
        mDescription = description;
    }

    public long getId() {
        return mId;
    }

    public TaskStatus getStatus() {
        return mStatus;
    }

    public void setStatus(TaskStatus status) {
        mStatus = status;
    }

    public void setId(@NonNull long id) {
        mId = id;
    }

    public String getImage() {
        return mImage;
    }

    public boolean hasImage() {
        return mImage != null;
    }

    public void setImage(String image) {
        mImage = image;
    }

    public Date getPlannedStartDate() {
        return mPlannedStartDate;
    }

    public void setPlannedStartDate(Date plannedStartDate) {
        mPlannedStartDate = plannedStartDate;
    }

    public Metadata getMetadata() {
        return mMetadata;
    }


    public void setMetadata(Metadata metadata) {
        mMetadata = metadata;
    }

    public boolean hasMetadata(){
        return mMetadata != null;
    }

    public boolean hasLocation(){
        return hasMetadata() && mMetadata.hasLocation();
    }

    @Override
    public String toString() {
        return "Task{" +
                "mId='" + mId + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mImage='" + mImage + '\'' +
                ", mPlannedStartDate=" + mPlannedStartDate +
                ", mStatus=" + mStatus +
                '}';
    }
}
