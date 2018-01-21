package com.nandy.taskmanager.db.converters;

import android.arch.persistence.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

/**
 * Created by yana on 17.01.18.
 */

public class LocationTypeConverter {

    @TypeConverter
    public static String fromLatLng(LatLng latLng) {
        if (latLng == null) {
            return (null);
        }

        return (String.format(Locale.US, "%f,%f", latLng.latitude, latLng.longitude));
    }

    @TypeConverter
    public static LatLng toLatLng(String latLng) {
        if (latLng == null) {
            return null;
        }

        String[] pieces = latLng.split(",");

        double latitude = Double.parseDouble(pieces[0]);
        double  longitude = Double.parseDouble(pieces[1]);

        return new LatLng(latitude, longitude);
    }
}
