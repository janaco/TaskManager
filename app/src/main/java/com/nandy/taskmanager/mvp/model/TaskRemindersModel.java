package com.nandy.taskmanager.mvp.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.receiver.TaskStatusReceiver;
import com.nandy.taskmanager.service.LocationService;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by yana on 20.01.18.
 */

public class TaskRemindersModel {

    private final Context mContext;

    public TaskRemindersModel(Context context) {
        mContext = context;
    }

    public void scheduleStartReminder(Task task) {
        Log.d("TASK_", "scheduleStartReminder: " + task.getId());

        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(task.getPlannedStartDate());
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 15);

        Intent data = new Intent(mContext, TaskStatusReceiver.class);
        data.setAction(TaskStatusReceiver.ACTION_START);
        data.putExtra("id", task.getId());

        int requestCode = (int) task.getId();
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(mContext, requestCode, data, PendingIntent.FLAG_UPDATE_CURRENT);

        if (task.isPeriodical()) {
//            getAlarmManager()
//                    .setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                            task.getRepeatPeriod().getValue(), pendingIntent);
            getAlarmManager()
                    .setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                            TimeUnit.SECONDS.toMillis(45), pendingIntent);
        } else {
            getAlarmManager()
                    .set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    public void scheduleEndReminder(long taskId, long duration) {

//        taskId = taskId + 1;

        Log.d("TASK_", "scheduleEndReminder: " + taskId);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.add(Calendar.MILLISECOND, (int) duration);
        calendar.add(Calendar.SECOND, 20);

        Intent data = new Intent(mContext, TaskStatusReceiver.class);
        data.setAction(TaskStatusReceiver.ACTION_COMPLETE);
        data.putExtra("id", taskId);

        int requestCode = (int) taskId;
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(mContext, requestCode, data, PendingIntent.FLAG_UPDATE_CURRENT);

        getAlarmManager()
                .set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public void cancelReminders(long requestCode) {

        Log.d("TASK_", "cancelReminders: " + requestCode);

        Intent intent = new Intent(mContext, TaskStatusReceiver.class);
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(mContext, (int) requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        getAlarmManager().cancel(pendingIntent);
        pendingIntent.cancel();
    }

    private AlarmManager getAlarmManager() {
        return (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
    }


    public void scheduleLocationUpdates() {
        mContext.startService(new Intent(mContext, LocationService.class));
    }


    public void cancelLocationUpdates() {
        mContext.stopService(new Intent(mContext, LocationService.class));
    }
}
