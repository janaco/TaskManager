package com.nandy.taskmanager.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Ignore;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by yana on 25.01.18.
 */

public class Location implements Parcelable {

    @ColumnInfo(name = "address")
    private String mAddress;
    @ColumnInfo(name = "geo_position")
    private LatLng mPosition;

    public Location(LatLng position, String address) {
        mPosition = position;
        mAddress = address;
    }

    @Ignore
    protected Location(Parcel in) {
        mAddress = in.readString();
        mPosition = in.readParcelable(LatLng.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAddress);
        dest.writeParcelable(mPosition, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public void setPosition(LatLng position) {
        mPosition = position;
    }

    public LatLng getPosition(){
        return mPosition;
    }


}
