package com.nandy.taskmanager.db.converters;

import android.arch.persistence.room.TypeConverter;

import com.nandy.taskmanager.model.Action;

/**
 * Created by yana on 21.01.18.
 */

public class ActionConverter {


    @TypeConverter
    public static String fromAction(Action action) {
        if (action == null) {
            return null;
        }

        return action.name();
    }

    @TypeConverter
    public static Action toAction(String name) {
        if (name == null) {
            return null;
        }

        return Action.valueOf(name);
    }
}
