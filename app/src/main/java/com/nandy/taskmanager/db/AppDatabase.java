package com.nandy.taskmanager.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.nandy.taskmanager.db.dao.TasksDao;
import com.nandy.taskmanager.model.Task;

/**
 * Created by yana on 16.01.18.
 */

@Database(entities = {Task.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public static AppDatabase getInstance(Context context){
        return Room.databaseBuilder(context,
                AppDatabase.class, "taskmanager")
                .allowMainThreadQueries().build();
    }

    public abstract TasksDao tasksDao();
}
