package com.nandy.taskmanager.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.nandy.taskmanager.db.converters.ActionConverter;
import com.nandy.taskmanager.db.converters.DateTypeConverter;
import com.nandy.taskmanager.db.converters.LocationTypeConverter;
import com.nandy.taskmanager.db.converters.RepeatPeriodConverter;
import com.nandy.taskmanager.db.converters.TaskStatusConverter;
import com.nandy.taskmanager.db.dao.EventsDao;
import com.nandy.taskmanager.db.dao.StatisticsDao;
import com.nandy.taskmanager.db.dao.TasksDao;
import com.nandy.taskmanager.model.Statistics;
import com.nandy.taskmanager.model.TaskEvent;
import com.nandy.taskmanager.model.Task;

/**
 * Created by yana on 16.01.18.
 */

@Database(entities = {Task.class, TaskEvent.class, Statistics.class}, version = 1)
@TypeConverters({
        LocationTypeConverter.class,
        DateTypeConverter.class,
        TaskStatusConverter.class,
        RepeatPeriodConverter.class,
        ActionConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    public static final String DB_NAME = "tasks";
    private static AppDatabase INSTANCE = null;

    public synchronized static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = create(context);
        }

        return INSTANCE;
    }

    private static AppDatabase create(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, DB_NAME)
                .allowMainThreadQueries()
                .build();
    }


    public abstract TasksDao tasksDao();

    public abstract EventsDao taskEventsDao();

    public abstract StatisticsDao statisticsDao();

}
