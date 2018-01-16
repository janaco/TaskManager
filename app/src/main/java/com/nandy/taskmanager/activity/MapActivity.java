package com.nandy.taskmanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nandy.taskmanager.R;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private Marker mMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        setSupportActionBar(findViewById(R.id.toolbar));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.set_location);
        }

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (mMarker == null) {
            mMarker = mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
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

        switch (item.getItemId()){

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

    private void onSaveLocationActionSelected(){

        if (mMarker != null){
            Intent intent = new Intent();
            intent.putExtra("location", mMarker.getPosition());

            setResult(Activity.RESULT_OK, intent);
            finish();
        }else {
            Toast.makeText(getApplicationContext(), getString(R.string.location_not_specified), Toast.LENGTH_SHORT).show();
        }
    }
}
