package com.nandy.taskmanager.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.nandy.taskmanager.R;
import com.nandy.taskmanager.db.AppDatabase;
import com.nandy.taskmanager.db.dao.TasksDao;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.enums.TaskStatus;
import com.nandy.taskmanager.mvp.model.LocationModel;
import com.nandy.taskmanager.mvp.model.TaskRemindersModel;

import java.util.List;
import java.util.Locale;

public class LocationService extends Service implements LocationModel.LocationListener {

    private static final int NOTIFICATION_LOCATION_REQUEST = 4;
    private static final int RADIUS = 500;

    private LocationModel mLocationModel;
    private TasksDao mTasksDao;
    private TaskRemindersModel mScheduleMode;
    private List<Task> mTasks;

    @Override
    public void onCreate() {
        super.onCreate();
        mLocationModel = new LocationModel(getApplicationContext());
        mTasksDao = AppDatabase.getInstance(getApplicationContext()).tasksDao();
        mScheduleMode = new TaskRemindersModel(getApplicationContext());
        mLocationModel.setLocationListener(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        mTasks = selectTasks();

        if (mTasks.size() > 0) {
            mLocationModel.startLocationUpdates();

            startForeground(NOTIFICATION_LOCATION_REQUEST, new NotificationCompat.Builder(getApplicationContext(), "location")
                    .setSmallIcon(R.drawable.ic_gps)
                    .setContentTitle(getString(R.string.location_tracking_is_enabled))
                    .setContentText(String.format(Locale.getDefault(),
                            "%s %d %s",
                            getString(R.string.you_have),
                            mTasks.size(),
                            getString(R.string.pending_tasks)))
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .build());

        } else {
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        mLocationModel.stopLocationUpdates();
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {

        for (Task task : mTasks) {
            float distance =
                    getDistanceBetween(location.getLatitude(), location.getLongitude(),
                            task.getLocation().latitude, task.getLocation().longitude);

            if (distance < RADIUS) {
                toggleStatus(task);
            }
        }
    }


    private List<Task> selectTasks() {
        return mTasksDao.selectAllWithLocation();
    }

    private void toggleStatus(Task task) {

        switch (task.getStatus()) {

            case NEW:
                task.setStatus(TaskStatus.ACTIVE);
                mScheduleMode.scheduleEndReminder(task.getId(), task.getScheduledDuration());
                break;

            case ACTIVE:
                task.setStatus(TaskStatus.COMPLETED);
                break;
        }

        mTasksDao.update(task);
    }

    private float getDistanceBetween(double originLat, double originLng,
                                     double destLat, double destLng) {

        float res[] = new float[1];
        Location.distanceBetween(originLat, originLng, destLat, destLng, res);

        return res[0];
    }

}
