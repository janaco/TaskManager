package com.nandy.taskmanager.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nandy.taskmanager.R;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private static final int REQUEST_LOCATION_PERMISSIONS_CODE = 4;
    private static final int REQUEST_LOCATION_RESOLUTION = 5;
    private static final int DEFAULT_ZOOM = 12;

    private GoogleMap mMap;
    private Marker mMarker;
    private FusedLocationProviderClient mFusedLocationProviderClient;

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

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else {
            mMap.setMyLocationEnabled(true);
            createLocationRequest();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMap != null && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMap != null && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSIONS_CODE) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                createLocationRequest();
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (mMarker == null) {
            mMarker = addMarker(latLng);
        } else {
            mMarker.setPosition(latLng);
        }
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
                onSaveLocationActionSelected();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_LOCATION_RESOLUTION && resultCode == RESULT_OK) {
            createLocationRequest();
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION_PERMISSIONS_CODE);
    }

    private void createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setNumUpdates(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        LocationServices.getSettingsClient(this)
                .checkLocationSettings(builder.build())
                .addOnSuccessListener(this, locationSettingsResponse -> getLastKnownLocation(locationRequest))
                .addOnFailureListener(this, e -> {
                    if (e instanceof ResolvableApiException) {
                        try {
                            ((ResolvableApiException) e)
                                    .startResolutionForResult(MapActivity.this, REQUEST_LOCATION_RESOLUTION);
                        } catch (IntentSender.SendIntentException se) {
                            se.printStackTrace();
                        }
                        e.printStackTrace();
                    }
                });
    }

    private void getLastKnownLocation(LocationRequest locationRequest) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {

                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    Location location = locationResult.getLastLocation();

                    if (location != null) {
                        moveToPosition(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM);
                    }
                }
            }, null);

        }
    }

    private void moveToPosition(LatLng position, int zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoom));

        if (mMarker == null) {
            mMarker = addMarker(position);
        }
    }


    private Marker addMarker(LatLng position) {
        return mMap.addMarker(new MarkerOptions().position(position).draggable(true));
    }

    private void onSaveLocationActionSelected() {

        if (mMarker != null) {
            Intent intent = new Intent();
            intent.putExtra("location", mMarker.getPosition());

            setResult(Activity.RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.location_not_specified), Toast.LENGTH_SHORT).show();
        }
    }
}
