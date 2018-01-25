package com.nandy.taskmanager.mvp.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.nandy.taskmanager.Constants;
import com.nandy.taskmanager.activity.MapActivity;
import com.nandy.taskmanager.mvp.contract.MapContract;
import com.nandy.taskmanager.mvp.model.LastKnowLocationModel;

/**
 * Created by yana on 25.01.18.
 */

public class MapPresenter implements MapContract.Presenter {

    private MapContract.View mView;

    private LastKnowLocationModel mLocationModel;
    private Bundle mSavedInstanceState;

    @Override
    public void onAttachView(MapContract.View view) {
        mView = view;
    }

    @Override
    public void onDetachView() {
        mView.setMyLocationEnabled(false);
        mView = null;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onMapReady() {

        if (mSavedInstanceState == null) {

            if (!mLocationModel.canRequestLocation()) {
                mView.requestPermissions(Constants.REQUEST_LOCATION_PERMISSIONS_CODE, new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
            } else {
                mView.setMyLocationEnabled(true);
                requestLastKnownLocation();
            }

        } else {
            restoreViewState();
        }
    }

    private void requestLastKnownLocation() {
        mLocationModel.checkResolutionsAndRequestLastKnowLocation(new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location location = locationResult.getLastLocation();
                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mView.setMarkerPosition(latLng);
                    mView.moveMapTo(latLng, MapActivity.DEFAULT_ZOOM);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode) {
        if (requestCode == Constants.REQUEST_LOCATION_PERMISSIONS_CODE) {
            if (mLocationModel.canRequestLocation()) {
                mView.setMyLocationEnabled(true);
                requestLastKnownLocation();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constants.REQUEST_LOCATION_RESOLUTION && resultCode == Activity.RESULT_OK) {
            requestLastKnownLocation();
        }
    }

    private void restoreViewState() {
        LatLng mapCenterPosition = mSavedInstanceState.getParcelable(Constants.PARAM_MAP_CENTER);
        LatLng markerPosition = mSavedInstanceState.getParcelable(Constants.PARAM_LOCATION);
        float zoom = mSavedInstanceState.getFloat(Constants.PARAM_ZOOM);

        mView.moveMapTo(mapCenterPosition, zoom);
        mView.setMarkerPosition(markerPosition);
        mView.setMyLocationEnabled(mLocationModel.canRequestLocation());
    }

    @Override
    public void saveInstanceState(Bundle outState, LatLng mapCenterPosition, LatLng markerPosition, float zoom) {
        outState.putParcelable(Constants.PARAM_MAP_CENTER, mapCenterPosition);
        outState.putParcelable(Constants.PARAM_LOCATION, markerPosition);
        outState.putFloat(Constants.PARAM_ZOOM, zoom);
    }

    @Override
    public void restoreInstanceState(Bundle savedInstanceState) {
        mSavedInstanceState = savedInstanceState;
    }

    @Override
    public void onSaveClick(LatLng markerPosition) {

        Bundle args = new Bundle();
        args.putParcelable(Constants.PARAM_LOCATION, markerPosition);

        Intent data = new Intent();
        data.putExtras(args);

        mView.finishWithResult(Activity.RESULT_OK, data);
    }

    public void setLastKnownLocationModel(LastKnowLocationModel locationModel) {
        mLocationModel = locationModel;
    }
}
