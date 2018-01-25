package com.nandy.taskmanager.mvp.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.nandy.taskmanager.Constants;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.receiver.TaskStatusReceiver;
import com.nandy.taskmanager.service.LocationService;

import java.util.Calendar;

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

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(task.getPlannedStartDate());

        Intent data = new Intent(mContext, TaskStatusReceiver.class);
        data.setAction(TaskStatusReceiver.ACTION_START);
        data.putExtra(Constants.PARAM_ID, task.getId());

        int requestCode = (int) task.getId();
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(mContext, requestCode, data, PendingIntent.FLAG_UPDATE_CURRENT);

        if (task.isPeriodical()) {
            getAlarmManager()
                    .setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                            task.getRepeatPeriod().getValue(), pendingIntent);
        } else {
            getAlarmManager()
                    .set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    public void scheduleEndReminder(long taskId, long duration) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MILLISECOND, (int) duration);

        Intent data = new Intent(mContext, TaskStatusReceiver.class);
        data.setAction(TaskStatusReceiver.ACTION_COMPLETE);
        data.putExtra(Constants.PARAM_ID, taskId);

        int requestCode = (int) taskId;
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(mContext, requestCode, data, PendingIntent.FLAG_UPDATE_CURRENT);

        getAlarmManager()
                .set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public void cancelReminders(long requestCode) {

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
}
