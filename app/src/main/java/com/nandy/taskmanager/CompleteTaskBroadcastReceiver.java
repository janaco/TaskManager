package com.nandy.taskmanager;

import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.nandy.taskmanager.db.AppDatabase;
import com.nandy.taskmanager.db.dao.TasksDao;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.model.TaskStatus;

import java.util.List;

/**
 * Created by yana on 20.01.18.
 */

public class CompleteTaskBroadcastReceiver extends BroadcastReceiver {

    public static final String TASK_CHANNEL_ID = "Task";
    private static final int TASK_NOTIFICATION_ID = 425;

    @Override
    public void onReceive(Context context, Intent intent) {


        TasksDao tasksDao = AppDatabase.getInstance(context).tasksDao();
        String taskId = intent.getStringExtra("id");

        Log.d("TASK_RECEIVE_", "id: " + taskId);

        Task task = getTask(tasksDao, taskId);
        Log.d("TASK_RECEIVE_", "task: " + task);

        if (task == null || task.getStatus() != TaskStatus.ACTIVE) {
            return;
        }

        complete(tasksDao, task);
        showTaskCompletedNotification(context, task.getTitle());

    }

    @Nullable
    private Task getTask(TasksDao tasksDao, String taskId) {
        List<Task> tasks = tasksDao.getById(taskId);

        if (tasks.size() > 0) {
            return tasks.get(0);
        } else {
            return null;
        }
    }

    private void complete(TasksDao tasksDao, Task task) {
        task.setStatus(TaskStatus.COMPLETED);
        tasksDao.update(task);
    }

    private void showTaskCompletedNotification(Context context, String title) {

        Intent resultIntent = new Intent(context, ListActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);


        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = buildNotification(context, title, contentIntent);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(TASK_NOTIFICATION_ID, notification);

        }
    }

    private Notification buildNotification(Context context, String title, PendingIntent contentIntent) {
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
}
