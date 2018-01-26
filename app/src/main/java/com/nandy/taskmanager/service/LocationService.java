package com.nandy.taskmanager.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.nandy.taskmanager.R;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.mvp.model.LocationModel;
import com.nandy.taskmanager.mvp.model.TaskModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LocationService extends Service implements LocationModel.LocationListener {

    public class LocationBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

    private static final int NOTIFICATION_LOCATION_REQUEST = 4;
    private static final int RADIUS = 500;

    private LocationModel mLocationModel;
    private TaskModel mTaskModel;
    private List<Task> mTasks;
    private Location mLastKnownLocation;

    private final IBinder binder = new LocationBinder();

    private Disposable mTasksSubscription;


    public void setTasks(List<Task> tasks) {
        mTasks = tasks;
        onTaskListChanged();
    }

    public void setTask(Task task) {

        if (mTasks.contains(task)) {
            mTasks.set(mTasks.indexOf(task), task);
            onTaskListChanged();
        } else {
            add(task);
        }
    }

    public void add(Task task) {
        mTasks.add(task);
        onTaskListChanged();
    }

    public void remove(Task task) {
        mTasks.remove(task);
        onTaskListChanged();
    }

    public void clearTasks(){
        mTasks.clear();
        onTaskListChanged();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mTasks = new ArrayList<>();
        Log.d("LOCATION_SERVICE_", "onCreate: " + mLocationModel);
        mTaskModel  = new TaskModel(getApplicationContext());
        mLocationModel = new LocationModel(getApplicationContext());
        mLocationModel.setLocationListener(this);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void onTaskListChanged() {
        Log.d("LOCATION_SERVICE_", "onTaskListChanged: " + mTasks.size());
        if (mTasks.size() > 0) {
            mLocationModel.startLocationUpdates();
            startForeground();

        } else {
            mLocationModel.stopLocationUpdates();
            stopSelf();
        }
    }

    private void startForeground() {
        Log.d("LOCATION_SERVICE_", "startForeground");
        startForeground(NOTIFICATION_LOCATION_REQUEST, new NotificationCompat.Builder(getApplicationContext(), "location")
                .setSmallIcon(R.drawable.ic_gps)
                .setContentTitle(getString(R.string.location_tracking_is_enabled))
                .setContentText(String.format(Locale.getDefault(),
                        "%s %d %s",
                        getString(R.string.you_have),
                        mTasks.size(),
                        getString(R.string.pending_tasks)))
                .build());
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        mLocationModel.stopLocationUpdates();
        stopForeground(true);
        super.onDestroy();
    }



    @Override
    public void onLocationChanged(Location location) {
        mLastKnownLocation = location;
        if (mLastKnownLocation != null &&
                (mTasksSubscription == null || mTasksSubscription.isDisposed())) {
            checkForTasksInRadius();
        }
    }

    private void checkForTasksInRadius() {

        mTasksSubscription = Completable.create(e -> {

            for (Task task : mTasks) {
                LatLng taskLocation
                        = task.getMetadata().getLocation().getPosition();
                float distance =
                        getDistanceBetween(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(),
                                taskLocation.latitude, taskLocation.longitude);

                if (distance < RADIUS) {
                    toggleStatus(task);
                }
            }
            e.onComplete();
        })
                .subscribeOn(Schedulers.newThread())
                .subscribe();
    }

    private void toggleStatus(Task task) {

        switch (task.getStatus()) {

            case NEW:
                mTaskModel.start(task);
                break;

            case ACTIVE:
                mTaskModel.complete(task);
                break;
        }

    }

    private float getDistanceBetween(double originLat, double originLng,
                                     double destLat, double destLng) {

        float res[] = new float[1];
        Location.distanceBetween(originLat, originLng, destLat, destLng, res);

        return res[0];
    }

}
