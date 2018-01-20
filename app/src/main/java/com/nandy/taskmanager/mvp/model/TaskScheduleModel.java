package com.nandy.taskmanager.mvp.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.nandy.taskmanager.receiver.TaskStatusReceiver;

import java.util.Calendar;
import java.util.Date;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by yana on 20.01.18.
 */

public class TaskScheduleModel {

    private Context mContext;

    public TaskScheduleModel(Context context) {
        mContext = context;
    }

    public void scheduleAutoTaskStart(String taskId, Date startDate) {
        Log.d("TASK_", "scheduleAutoTaskStart: " + taskId + ", " + startDate);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        Intent data = new Intent(mContext, TaskStatusReceiver.class);
        data.setAction(TaskStatusReceiver.ACTION_START);
        data.putExtra("id", taskId);

        int requestCode = (int) System.currentTimeMillis();
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(mContext, requestCode, data, PendingIntent.FLAG_ONE_SHOT);

        getAlarmManager()
                .set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public void scheduleTaskAutoComplete(String taskId, long duration) {

        Log.d("TASK_", "scheduleTaskAutoComplete: " + taskId + ", " + duration);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.add(Calendar.MILLISECOND, (int) duration);
        calendar.add(Calendar.SECOND, 30);


        enableTasksReceiver();

        Intent data = new Intent(mContext, TaskStatusReceiver.class);
        data.setAction(TaskStatusReceiver.ACTION_COMPLETE);
        data.putExtra("id", taskId);

        int requestCode = (int) System.currentTimeMillis();
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(mContext, requestCode, data, PendingIntent.FLAG_ONE_SHOT);

        getAlarmManager()
                .set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private AlarmManager getAlarmManager() {
        return (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
    }

    private void enableTasksReceiver() {
        mContext.getPackageManager()
                .setComponentEnabledSetting(
                        new ComponentName(mContext, TaskStatusReceiver.class),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);
    }

    private void cancelReminder(Context context) {
        ComponentName componentName = new ComponentName(context, TaskStatusReceiver.class);
        PackageManager packageManager = context.getPackageManager();
        packageManager.setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent = new Intent(context, TaskStatusReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
        pendingIntent.cancel();
    }
}
