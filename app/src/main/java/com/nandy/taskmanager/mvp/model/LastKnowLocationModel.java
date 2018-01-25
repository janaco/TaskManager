package com.nandy.taskmanager.mvp.model;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.nandy.taskmanager.Constants;
import com.nandy.taskmanager.activity.MapActivity;

/**
 * Created by yana on 25.01.18.
 */

public class LastKnowLocationModel {

    private Activity mActivity;
    private FusedLocationProviderClient mLocationProvider;

    public LastKnowLocationModel(Activity activity) {
        mActivity = activity;
        mLocationProvider = LocationServices.getFusedLocationProviderClient(activity);
    }

    public boolean canRequestLocation(){
        return ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void checkResolutionsAndRequestLastKnowLocation(LocationCallback locationCallback) {
        LocationRequest locationRequest = getLocationRequest();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        LocationServices.getSettingsClient(mActivity)
                .checkLocationSettings(builder.build())
                .addOnSuccessListener(mActivity, locationSettingsResponse -> requestLastKnownLocation(locationRequest, locationCallback))
                .addOnFailureListener(mActivity, e -> {
                    e.printStackTrace();
                    if (e instanceof ResolvableApiException) {
                        requestLocationResolution((ResolvableApiException) e);
                    }
                });
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setNumUpdates(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return locationRequest;

    }

    private void requestLocationResolution(ResolvableApiException exception) {
        try {
            exception
                    .startResolutionForResult(mActivity, Constants.REQUEST_LOCATION_RESOLUTION);
        } catch (IntentSender.SendIntentException se) {
            se.printStackTrace();
        }
    }

    private void requestLastKnownLocation(LocationRequest locationRequest, LocationCallback locationCallback) {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mLocationProvider.requestLocationUpdates(locationRequest, locationCallback, null);

        }
    }
}
