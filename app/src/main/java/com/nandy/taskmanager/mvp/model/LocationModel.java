package com.nandy.taskmanager.mvp.model;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.TimeUnit;

/**
 * Created by yana on 20.01.18.
 */

public class LocationModel {

    private static long UPDATE_INTERVAL = TimeUnit.MINUTES.toMillis(5);
    private static long FASTEST_INTERVAL = TimeUnit.MINUTES.toMillis(2);
    private static int DISPLACEMENT = 100;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private Context mContext;
    private LocationListener mLocationListener;
    private LocationRequest mLocationRequest;


    public LocationModel(Context context) {
        mContext = context;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mLocationRequest = createLocationRequest();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                if (locationResult.getLocations().size() > 0) {
                    mLocationListener.onLocationChanged(
                            locationResult.getLocations().get(0));
                }

            }
        };
    }

    public void setLocationListener(LocationListener locationListener) {
        mLocationListener = locationListener;
    }


    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(DISPLACEMENT);

        return locationRequest;
    }


    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }


    public void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }


    public interface LocationListener {
        void onLocationChanged(Location location);
    }
}
