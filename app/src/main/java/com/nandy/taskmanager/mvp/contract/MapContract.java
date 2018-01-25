package com.nandy.taskmanager.mvp.contract;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by yana on 25.01.18.
 */

public interface MapContract {

    interface View extends org.kaerdan.presenterretainer.Presenter.View{

        void setMarkerPosition(LatLng position);

        void moveMapTo(LatLng position, float zoom);

        void finishWithResult(int resultCode, Intent data);

        void requestPermissions(int requestCode, String []permissions);

        void setMyLocationEnabled(boolean enabled);
    }

    interface Presenter extends org.kaerdan.presenterretainer.Presenter<View>{

        void saveInstanceState(Bundle outState, LatLng mapCenterPosition, LatLng markerPosition, float zoom);

        void restoreInstanceState(Bundle savedInstanceState);

        void onSaveClick(LatLng markerPosition);

        void onMapReady();

        void onActivityResult(int requestCode, int resultCode, Intent data);

        void onRequestPermissionsResult(int requestCode);

    }
}
