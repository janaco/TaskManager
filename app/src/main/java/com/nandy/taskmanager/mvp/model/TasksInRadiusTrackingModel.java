package com.nandy.taskmanager.mvp.model;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.nandy.taskmanager.OnServiceConnectedListener;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.service.LocationService;

import java.util.List;

/**
 * Created by yana on 26.01.18.
 */

public class TasksInRadiusTrackingModel {

    @Nullable
    private LocationService mLocationService;
    private final Intent mServiceIntent;
    private Context mContext;
    private OnServiceConnectedListener mOnServiceConnectedListener;
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.LocationBinder binder = (LocationService.LocationBinder) service;
            mLocationService = binder.getService();

            if (mOnServiceConnectedListener != null) {
                mOnServiceConnectedListener.onServiceConnected();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mLocationService = null;
        }
    };

    public TasksInRadiusTrackingModel(Context context) {
        mContext = context;
        mServiceIntent = new Intent(context, LocationService.class);
    }

    public void setOnServiceConnectedListener(OnServiceConnectedListener onServiceConnectedListener) {
        mOnServiceConnectedListener = onServiceConnectedListener;
    }

    public void startTracking() {

        if (isLocationServiceRunning()) {
            mContext.bindService(mServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        } else {
            mContext.startService(mServiceIntent);
            mContext.bindService(mServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public void setTasks(List<Task> tasks) {
        if (mLocationService != null) {
            mLocationService.setTasks(tasks);
        } else if (!isLocationServiceRunning()) {
            startTracking();
        }
    }

    public void onTaskChanged(Task task) {
        if (mLocationService != null) {
            mLocationService.setTask(task);
        } else if (!isLocationServiceRunning()) {
            startTracking();
        }
    }

    public void onTaskRemoved(Task task) {
        if (mLocationService != null) {
            mLocationService.remove(task);
        } else if (!isLocationServiceRunning()) {
            startTracking();
        }
    }

    public void onTasksCleaned() {

        if (mLocationService != null) {
            mLocationService.clearTasks();
        } else if (!isLocationServiceRunning()) {
            startTracking();
        }

    }

    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationService.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }


    public void unbind() {
        mContext.unbindService(mServiceConnection);
        mLocationService = null;
    }

    public void destroy(){
        mContext = null;
    }
}

