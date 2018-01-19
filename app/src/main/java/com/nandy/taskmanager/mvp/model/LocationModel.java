//package com.nandy.taskmanager.mvp.model;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v4.app.ActivityCompat;
//import android.util.Log;
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GoogleApiAvailability;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.ResultCallback;
//import com.google.android.gms.common.api.Status;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.LocationSettingsRequest;
//import com.google.android.gms.location.LocationSettingsResult;
//import com.google.android.gms.location.LocationSettingsStatusCodes;
//
///**
// * Created by razomer on 19.01.18.
// */
//
//public class LocationModel implements GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult> {
//
//
//    public final static int REQUEST_CHECK_SETTINGS = 2000;
//    public final static int REQUEST_LOCATION_PERMISSIONS = 2001;
//
//    private GoogleApiClient googleApiClient;
//    private Context context;
//
//    public LocationModel(Activity activity) {
//        this.context = activity;
//        if (checkPlayServices(activity)) {
//            buildGoogleApiClient(activity);
//        }
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult result) {
//        Log.i("LOCATION_", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
//    }
//
//    @Override
//    public void onConnected(Bundle arg0) {
//        getLocation();
//    }
//
//    @Override
//    public void onConnectionSuspended(int arg0) {
//        Log.i("LOCATION_", "onConnectionSuspended");
//
//        googleApiClient.connect();
//    }
//
//
//    @Override
//    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
//        final Status status = locationSettingsResult.getStatus();
//
//        switch (status.getStatusCode()) {
//            case LocationSettingsStatusCodes.SUCCESS:
//                getLocation();
//                break;
//            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                EventBus.getDefault().post(new CurrentLocationEvent(status));
//                break;
//        }
//    }
//
//    public void requestLocationUpdate() {
//
//        LocationRequest locationRequest = new LocationRequest();
//        locationRequest.setInterval(10000);
//        locationRequest.setFastestInterval(5000);
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        LocationServices.SettingsApi
//                .checkLocationSettings(googleApiClient, new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build())
//                .setResultCallback(this);
//    }
//
//    public void onActivityResult(int resultCode) {
//
//        switch (resultCode) {
//            case Activity.RESULT_OK:
//                requestLocationUpdate();
//                break;
//        }
//    }
//
//    private void getLocation() {
//
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            EventBus.getDefault().post(new PermissionRequestEvent(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSIONS));
//            return;
//        }
//
//
//        Location lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//        EventBus.getDefault().post(new CurrentLocationEvent(lastKnownLocation));
//    }
//
//    private synchronized void buildGoogleApiClient(Activity activity) {
//        googleApiClient = new GoogleApiClient.Builder(activity)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API).build();
//
//        googleApiClient.connect();
//    }
//
//
//    private boolean checkPlayServices(Context context) {
//
//        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
//    }
//
//}