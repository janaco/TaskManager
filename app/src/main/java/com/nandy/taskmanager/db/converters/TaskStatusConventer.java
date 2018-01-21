package com.nandy.taskmanager.db.converters;

import android.arch.persistence.room.TypeConverter;

import com.nandy.taskmanager.model.TaskStatus;

/**
 * Created by razomer on 18.01.18.
 */

public class TaskStatusConventer {

    @TypeConverter
    public static String fromTaskStatus(TaskStatus status) {
        if (status == null) {
            return null;
        }

        return status.name();
    }

    @TypeConverter
    public static TaskStatus toTaskStatus(String name) {
        if (name == null) {
            return null;
        }

        return TaskStatus.valueOf(name);
    }
}
