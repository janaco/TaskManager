package com.nandy.taskmanager.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nandy.taskmanager.R;
import com.nandy.taskmanager.mvp.contract.MapContract;
import com.nandy.taskmanager.mvp.model.LastKnowLocationModel;
import com.nandy.taskmanager.mvp.presenter.MapPresenter;

import org.kaerdan.presenterretainer.PresenterActivity;

public class MapActivity extends PresenterActivity<MapContract.Presenter, MapContract.View>
        implements MapContract.View, OnMapReadyCallback, GoogleMap.OnMapClickListener {

    public static final int REQUEST_LOCATION_PERMISSIONS_CODE = 4;
    public static final int REQUEST_LOCATION_RESOLUTION = 5;
    public static final int DEFAULT_ZOOM = 12;

    public static final String KEY_MAP_CENTER = "center";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_ZOOM = "zoom";


    private GoogleMap mMap;
    private Marker mMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.set_location);
        }

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    @Override
    protected MapContract.View getPresenterView() {
        return this;
    }

    @Override
    protected MapContract.Presenter onCreatePresenter() {
        MapPresenter presenter = new MapPresenter();
        presenter.setLastKnownLocationModel(
                new LastKnowLocationModel(this));
        return presenter;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        LatLng mapCenterPosition = null;
        LatLng markerPosition = null;
        float zoom = -1;

        if (mMap != null) {
            mapCenterPosition = mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();
            zoom = mMap.getCameraPosition().zoom;

            if (mMarker != null) {
                markerPosition = mMarker.getPosition();
            }
        }
        getPresenter().saveInstanceState(outState, mapCenterPosition, markerPosition, zoom);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        getPresenter().restoreInstanceState(savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        getPresenter().onMapReady();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        getPresenter().onRequestPermissionsResult(requestCode);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        setMarkerPosition(latLng);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            case R.id.action_save:
                if (mMarker != null) {
                    getPresenter().onSaveClick(mMarker.getPosition());
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.location_not_specified), Toast.LENGTH_SHORT).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getPresenter().onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void setMarkerPosition(LatLng position) {
        if (mMarker != null) {
            mMarker.setPosition(position);
        } else {
            mMarker = mMap.addMarker(new MarkerOptions().position(position).draggable(true));
        }
    }

    @Override
    public void moveMapTo(LatLng position, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoom));
    }

    @Override
    public void finishWithResult(int resultCode, Intent data) {
        setResult(resultCode, data);
        finish();
    }

    @Override
    public void requestPermissions(int requestCode, String[] permissions) {
        ActivityCompat.requestPermissions(this, permissions, requestCode);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void setMyLocationEnabled(boolean enabled) {
        if (mMap != null) {
            mMap.setMyLocationEnabled(enabled);
        }
    }
}
