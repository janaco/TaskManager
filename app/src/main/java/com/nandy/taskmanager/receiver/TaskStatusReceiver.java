package com.nandy.taskmanager.receiver;

import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.nandy.taskmanager.Constants;
import com.nandy.taskmanager.R;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.enums.TaskStatus;
import com.nandy.taskmanager.mvp.model.TaskModel;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yana on 20.01.18.
 */

public class TaskStatusReceiver extends BroadcastReceiver {

    public static final String ACTION_START = "action_start";
    public static final String ACTION_COMPLETE = "action_compete";

    public static final String GROUP_ACTIVE = "group_active";
    public static final String GROUP_COMPLETED = "group_completed";

    private static final int NOTIFICATION_TASK_STARTED = 425;
    private static final int NOTIFICATION_TASK_COMPLETED = 426;

    @Override
    public void onReceive(Context context, Intent intent) {

        Completable.create(e -> {
            long taskId = intent.getLongExtra(Constants.PARAM_ID, -1);

            TaskModel taskModel = new TaskModel(context);

            Task task = taskModel.getTask(taskId);

            if (task == null || intent.getAction() == null) {
                return;
            }


            switch (intent.getAction()) {

                case ACTION_START:
                    if (task.getStatus() == TaskStatus.NEW
                            || (task.isPeriodical() && task.getStatus() == TaskStatus.COMPLETED)) {
                        Log.d("TASK_", "start:" + task.getTitle());
                        taskModel.start(task);
                        showTaskStartedNotification(context, task.getTitle());
                    }
                    break;

                case ACTION_COMPLETE:
                    if (task.getStatus() == TaskStatus.ACTIVE) {
                        Log.d("TASK_", "complete:" + task.getTitle());
                        taskModel.complete(task);
                        showTaskCompletedNotification(context, task.getTitle());
                    }
                    break;
            }
            e.onComplete();
        }).subscribeOn(Schedulers.io())
        .subscribe(()->{},Throwable::printStackTrace);

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


        return new NotificationCompat.Builder(context, GROUP_COMPLETED)
                .setSmallIcon(R.drawable.ic_clock_alert)
                .setContentTitle(context.getString(R.string.task_completed))
                .setContentText(String.format("%s '%s' %s",
                        context.getString(R.string.task),
                        title,
                        context.getString(R.string.completed_automatically)))
                .setContentIntent(contentIntent)
                .build();
    }


    private static Notification buildTaskStartedNotification(Context context, String title, PendingIntent contentIntent) {


        return new NotificationCompat.Builder(context, GROUP_ACTIVE)
                .setSmallIcon(R.drawable.ic_check_outline_24)
                .setContentTitle(context.getString(R.string.active_task))
                .setContentText(String.format("%s '%s' %s",
                        context.getString(R.string.task),
                        title,
                        context.getString(R.string.started_automatically)))
                .setContentIntent(contentIntent)
                .build();
    }
}
