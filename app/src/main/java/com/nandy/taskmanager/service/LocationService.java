package com.nandy.taskmanager.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.nandy.taskmanager.R;
import com.nandy.taskmanager.db.AppDatabase;
import com.nandy.taskmanager.db.dao.TasksDao;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.model.TaskStatus;
import com.nandy.taskmanager.mvp.model.LocationModel;
import com.nandy.taskmanager.mvp.model.TaskScheduleModel;

import java.util.List;
import java.util.Locale;

public class LocationService extends Service {

    private static final int RADIUS = 500;

    private LocationModel locationModel;
    private LocationTrackingTask mTrackingTask;

    private static final int NOTIFICATION_LOCATION_REQUEST = 4;


    @Override
    public void onCreate() {
        super.onCreate();
        locationModel = new LocationModel(getApplicationContext());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        Log.d("LOCATION_SERVICE_", "mTrackingTask: " + mTrackingTask);

        if (mTrackingTask == null) {
            mTrackingTask = new LocationTrackingTask(getApplicationContext(), locationModel);
            mTrackingTask.execute();
        } else {
            mTrackingTask.refreshTasks();
        }


        int tasksCount = mTrackingTask.getNumberOfPendingTasksWithLocation();

        Log.d("LOCATION_SERVICE_", "tasksCount: " + tasksCount);
        if (tasksCount > 0) {

            startForeground(NOTIFICATION_LOCATION_REQUEST, new NotificationCompat.Builder(getApplicationContext(), "location")
                    .setSmallIcon(R.drawable.ic_gps)
                    .setContentTitle(getString(R.string.location_tracking_is_enabled))
                    .setContentText(String.format(Locale.getDefault(),
                            "%s %d %s",
                            getString(R.string.you_have),
                            tasksCount,
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
        Log.d("LOCATION_SERVICE_", "onDestroy");
        locationModel.stopLocationUpdates();
        super.onDestroy();
    }

    private static class LocationTrackingTask extends AsyncTask<Void, Void, Void>
            implements LocationModel.LocationListener {

        private LocationModel mLocationModel;
        private TasksDao mTasksDao;
        private TaskScheduleModel mScheduleMode;
        private List<Task> mTasks;

        LocationTrackingTask(Context context, LocationModel locationModel) {
            mTasksDao = AppDatabase.getInstance(context).tasksDao();
            mScheduleMode = new TaskScheduleModel(context);
            mLocationModel = locationModel;
            mLocationModel.setLocationListener(this);
        }


        @Override
        protected Void doInBackground(Void... voids) {
            mTasks = selectTasks();
            Looper.prepare();
            Log.d("LOCATION_SERVICE_TASK", "startLocationUpdates: " + Thread.currentThread());
            mLocationModel.startLocationUpdates();
            Looper.loop();


            return null;
        }

        @Override
        public void onLocationChanged(Location location) {

            Log.d("LOCATION_SERVICE_TASK", "onLocationChanged: " + location + ", " + Thread.currentThread());

            for (Task task : mTasks) {
                float distance =
                        getDistanceBetween(location.getLatitude(), location.getLongitude(),
                                task.getLocation().latitude, task.getLocation().longitude);

                Log.d("LOCATION_SERVICE_TASK", "distance: " + distance + ", to: " + task.getLocation());

                if (distance < RADIUS) {
                    toggleStatus(task);
                }


            }
        }

        public void refreshTasks() {
            mTasks = selectTasks();
        }

        private List<Task> selectTasks() {
            return mTasksDao.selectAllWithLocation();
        }

        private void toggleStatus(Task task) {

            Log.d("LOCATION_SERVICE_TASK", "toggleStatus: " + task.getStatus());

            switch (task.getStatus()) {

                case NEW:
                    task.setStatus(TaskStatus.ACTIVE);
                    mScheduleMode.scheduleTaskAutoComplete(task.getId(), task.getMaxDuration());
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

        int getNumberOfPendingTasksWithLocation() {
            return mTasksDao.getNumberOfTasksWithLocation();
        }
    }
}
