package com.nandy.taskmanager.mvp.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.nandy.taskmanager.db.AppDatabase;

import java.io.File;
import java.io.IOException;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yana on 24.01.18.
 */

public class DataImportModel {

    private Context mContext;

    public DataImportModel(Context context) {
        mContext = context;
    }

    public Completable importData(File backupDbFile) throws IOException {

        return Completable.create(e -> {
            AppDatabase.getInstance(mContext);

            SQLiteDatabase database = SQLiteDatabase.openDatabase(
                    mContext.getDatabasePath(AppDatabase.DB_NAME).getPath(), null, 0);

            try {
                database.execSQL("ATTACH DATABASE ? AS backup ", new String[]{backupDbFile.getPath()});
                database.beginTransaction();

                importTasks(database);
                importEvents(database);
                importStatistics(database);

                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
                database.execSQL("DETACH backup");
            }


            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    private void importEvents(SQLiteDatabase database) {

        database.execSQL("INSERT OR IGNORE INTO events( id_task, timestamp, action) " +
                "SELECT  backup.events.id_task, backup.events.timestamp, backup.events.action FROM backup.events");

    }

    private void importStatistics(SQLiteDatabase database) {
        database.execSQL("INSERT OR IGNORE INTO statistics( id_task, spent_time, start_date) " +
                "SELECT  backup.statistics.id_task, backup.statistics.spent_time, backup.statistics.start_date " +
                "FROM backup.statistics");
    }

    private void importTasks(SQLiteDatabase database) {

        database.execSQL("INSERT OR IGNORE INTO tasks( id, title, description, image, geo_position, " +
                "address, planned_start_date, status, scheduled_duration, repeat_period, " +
                "actual_start_date, time_spent, downtime) " +
                "SELECT  backup.tasks.id, backup.tasks.title, backup.tasks.description, backup.tasks.image, " +
                "backup.tasks.geo_position, backup.tasks.address, backup.tasks.planned_start_date, " +
                "backup.tasks.status, backup.tasks.scheduled_duration, backup.tasks.repeat_period, " +
                "backup.tasks.actual_start_date, backup.tasks.time_spent, backup.tasks.downtime " +
                "FROM backup.tasks");

    }

}
