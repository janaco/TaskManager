package com.nandy.taskmanager.mvp.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.nandy.taskmanager.CompleteTaskBroadcastReceiver;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by yana on 20.01.18.
 */

public class TaskScheduleModel {

    private Context mContext;

    public TaskScheduleModel(Context context) {
        mContext = context;
    }

    public void start(String taskId, long duration) {

        Log.d("TASK_", "start: " + taskId + ", " + duration);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MILLISECOND, (int) duration);

        enableTasksReceiver();

        Intent data = new Intent(mContext, CompleteTaskBroadcastReceiver.class);
        data.putExtra("id", taskId);

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(mContext, 2, data, PendingIntent.FLAG_UPDATE_CURRENT);

        getAlarmManager()
                .set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private AlarmManager getAlarmManager() {
        return (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
    }

    private void enableTasksReceiver() {
        mContext.getPackageManager()
                .setComponentEnabledSetting(
                        new ComponentName(mContext, CompleteTaskBroadcastReceiver.class),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);
    }

    private void cancelReminder(Context context) {
        ComponentName componentName = new ComponentName(context, CompleteTaskBroadcastReceiver.class);
        PackageManager packageManager = context.getPackageManager();
        packageManager.setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent = new Intent(context, CompleteTaskBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
        pendingIntent.cancel();
    }
}
