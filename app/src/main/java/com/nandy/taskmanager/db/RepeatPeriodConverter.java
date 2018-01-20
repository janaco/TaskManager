package com.nandy.taskmanager.db;

import android.arch.persistence.room.TypeConverter;

import com.nandy.taskmanager.model.RepeatPeriod;

/**
 * Created by yana on 20.01.18.
 */

public class RepeatPeriodConverter {

    @TypeConverter
    public static String fromRepeatPeriod(RepeatPeriod period) {
        if (period == null) {
            return null;
        }

        return period.name();
    }

    @TypeConverter
    public static RepeatPeriod toRepeatPeriod(String name) {
        if (name == null) {
            return null;
        }

        return RepeatPeriod.valueOf(name);
    }
}
