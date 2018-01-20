package com.nandy.taskmanager.receiver;

import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.nandy.taskmanager.R;
import com.nandy.taskmanager.db.AppDatabase;
import com.nandy.taskmanager.db.dao.TasksDao;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.model.TaskStatus;
import com.nandy.taskmanager.mvp.model.TaskScheduleModel;

import java.util.List;

/**
 * Created by yana on 20.01.18.
 */

public class TaskStatusReceiver extends BroadcastReceiver {

    public static final String ACTION_START = "action_start";
    public static final String ACTION_COMPLETE = "action_compete";

    public static final String TASK_CHANNEL_ID = "Task";

    private static final int NOTIFICATION_TASK_STARTED = 425;
    private static final int NOTIFICATION_TASK_COMPLETED = 426;


    @Override
    public void onReceive(Context context, Intent intent) {


        TasksDao tasksDao = AppDatabase.getInstance(context).tasksDao();
        long taskId = intent.getLongExtra("id", -1);

        Log.d("TASK_RECEIVE_", "id: " + taskId + ", " + intent.getAction());

        Task task = getTask(tasksDao, taskId);
        Log.d("TASK_RECEIVE_", "task: " + task);

        if (task == null || intent.getAction() == null) {
            return;
        }

        switch (intent.getAction()) {

            case ACTION_START:
                if (task.getStatus() == TaskStatus.NEW
                        || (task.isPeriodical() && task.getStatus() == TaskStatus.COMPLETED)) {
                    task.setStatus(TaskStatus.ACTIVE);
                    showTaskStartedNotification(context, task.getTitle());
                    new TaskScheduleModel(context).scheduleTaskAutoComplete(taskId, task.getMaxDuration());
                }
                break;

            case ACTION_COMPLETE:
                if (task.getStatus() == TaskStatus.ACTIVE) {
                    task.setStatus(TaskStatus.COMPLETED);
                    showTaskCompletedNotification(context, task.getTitle());
                }
                break;
        }

        tasksDao.update(task);


    }

    @Nullable
    private Task getTask(TasksDao tasksDao, long taskId) {
        List<Task> tasks = tasksDao.getById(taskId);

        if (tasks.size() > 0) {
            return tasks.get(0);
        } else {
            return null;
        }
    }


    private void showTaskCompletedNotification(Context context, String title) {

        showNotification(context,
                buildTaskCompetedNotification(context, title,
                        getContentIntent(context)),
                NOTIFICATION_TASK_COMPLETED);
    }

    private void showTaskStartedNotification(Context context, String title) {

        showNotification(context,
                buildTaskStartedNotification(context, title,
                        getContentIntent(context)),
                NOTIFICATION_TASK_STARTED);
    }

    private PendingIntent getContentIntent(Context context) {
        Intent resultIntent = new Intent(context, ListActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);


        return PendingIntent.getActivity(context, 0, resultIntent, 0);
    }

    private void showNotification(Context context, Notification notification, int id) {

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(id, notification);

        }
    }


    private Notification buildTaskCompetedNotification(Context context, String title, PendingIntent contentIntent) {
        return new NotificationCompat.Builder(context, TASK_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_clock_alert)
                .setContentTitle(context.getString(R.string.task_completed))
                .setContentText(String.format("%s '%s' %s",
                        context.getString(R.string.task),
                        title,
                        context.getString(R.string.completed_automatically)))
                .setContentIntent(contentIntent)
                .build();
    }


    private Notification buildTaskStartedNotification(Context context, String title, PendingIntent contentIntent) {
        return new NotificationCompat.Builder(context, TASK_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_check_outline)
                .setContentTitle(context.getString(R.string.active_task))
                .setContentText(String.format("%s '%s' %s",
                        context.getString(R.string.task),
                        title,
                        context.getString(R.string.started_automatically)))
                .setContentIntent(contentIntent)
                .build();
    }
}
