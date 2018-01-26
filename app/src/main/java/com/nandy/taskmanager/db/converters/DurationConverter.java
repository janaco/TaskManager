package com.nandy.taskmanager.db.converters;

import android.arch.persistence.room.TypeConverter;

import com.nandy.taskmanager.enums.Duration;

/**
 * Created by yana on 26.01.18.
 */

public class DurationConverter {

    @TypeConverter
    public static String fromDuration(Duration duration) {
        if (duration == null) {
            return null;
        }

        return duration.name();
    }

    @TypeConverter
    public static Duration toDuration(String name) {
        if (name == null) {
            return null;
        }

        return Duration.valueOf(name);
    }
}
