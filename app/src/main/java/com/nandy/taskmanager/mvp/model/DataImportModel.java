package com.nandy.taskmanager.mvp.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.nandy.taskmanager.db.AppDatabase;
import com.nandy.taskmanager.db.converters.ActionConverter;
import com.nandy.taskmanager.db.converters.DateTypeConverter;
import com.nandy.taskmanager.db.converters.LocationTypeConverter;
import com.nandy.taskmanager.db.converters.RepeatPeriodConverter;
import com.nandy.taskmanager.db.converters.TaskStatusConverter;
import com.nandy.taskmanager.db.dao.EventsDao;
import com.nandy.taskmanager.db.dao.StatisticsDao;
import com.nandy.taskmanager.db.dao.TasksDao;
import com.nandy.taskmanager.model.Metadata;
import com.nandy.taskmanager.model.Statistics;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.model.TaskEvent;

import java.io.File;
import java.io.IOException;

/**
 * Created by yana on 24.01.18.
 */

public class DataImportModel {

    private AppDatabase mAppDatabase;

    public DataImportModel(Context context){
        mAppDatabase = AppDatabase.getInstance(context);
    }

    public void importData(File backupDbFile) throws IOException {

        SQLiteDatabase backupDb = SQLiteDatabase.openDatabase(backupDbFile.getPath(), null, 0);

        importTasks(backupDb);
        importEvents(backupDb);
        importStatistics(backupDb);
    }

    private void importEvents(SQLiteDatabase backupDb) {
        EventsDao eventsDao = mAppDatabase.taskEventsDao();
        Cursor cursor = backupDb.rawQuery("SELECT * FROM events", null);

        if (cursor.moveToFirst()) {

            do {
                long taskId = cursor.getLong(cursor.getColumnIndex("id_task"));
                long timestamp = cursor.getLong(cursor.getColumnIndex("timestamp"));
                String action = cursor.getString(cursor.getColumnIndex("action"));

                eventsDao.insert(new TaskEvent(taskId, timestamp, ActionConverter.toAction(action)));

            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    private void importStatistics(SQLiteDatabase backupDb) {
        StatisticsDao statisticsDao = mAppDatabase.statisticsDao();
        Cursor cursor = backupDb.rawQuery("SELECT * FROM statistics", null);

        if (cursor.moveToFirst()) {

            do {
                long taskId = cursor.getLong(cursor.getColumnIndex("id_task"));
                long timeSpent = cursor.getLong(cursor.getColumnIndex("spent_time"));
                long startDate = cursor.getLong(cursor.getColumnIndex("start_date"));

                statisticsDao.insert(new Statistics(taskId,
                        DateTypeConverter.fromTimestamp(startDate), timeSpent));

            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    private void importTasks(SQLiteDatabase backupDb) {
        TasksDao tasksDao = mAppDatabase.tasksDao();

        Cursor cursor = backupDb.rawQuery("SELECT * FROM tasks", null);

        if (cursor.moveToFirst()) {

            do {
                long id = cursor.getLong(cursor.getColumnIndex("id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String description = cursor.getString(cursor.getColumnIndex("description"));
                String image = cursor.getString(cursor.getColumnIndex("image"));
                String location = cursor.getString(cursor.getColumnIndex("location"));
                long plannedStartDate = cursor.getLong(cursor.getColumnIndex("planned_start_date"));
                String status = cursor.getString(cursor.getColumnIndex("status"));
                long scheduledDuration = cursor.getLong(cursor.getColumnIndex("scheduled_duration"));
                String repeatPeriod = cursor.getString(cursor.getColumnIndex("repeat_period"));
                long actualStartDate = cursor.getLong(cursor.getColumnIndex("actual_start_date"));
                long timeSpent = cursor.getLong(cursor.getColumnIndex("time_spent"));
                long downtime = cursor.getLong(cursor.getColumnIndex("downtime"));

                Task task = new Task(id);
                task.setTitle(title);
                task.setDescription(description);
                task.setImage(image);
                task.setStatus(TaskStatusConverter.toTaskStatus(status));
                task.setLocation(LocationTypeConverter.toLatLng(location));
                task.setPlannedStartDate(DateTypeConverter.fromTimestamp(plannedStartDate));
                task.setScheduledDuration(scheduledDuration);
                task.setRepeatPeriod(RepeatPeriodConverter.toRepeatPeriod(repeatPeriod));

                Metadata metadata = new Metadata();
                metadata.setActualStartDate(DateTypeConverter.fromTimestamp(actualStartDate));
                metadata.setTimeSpent(timeSpent);
                metadata.setDownTime(downtime);

                task.setMetadata(metadata);

                tasksDao.insert(task);

            } while (cursor.moveToNext());
        }

        cursor.close();
    }

}
