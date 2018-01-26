package com.nandy.taskmanager.mvp.model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.nandy.taskmanager.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by yana on 25.01.18.
 */

public class GeocoderModel {

    private Context mContext;

    public GeocoderModel(Context context) {
        mContext = context;
    }

    public String getAddressFromLocation(LatLng latLng) {

        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses == null || addresses.size() == 0) {
            return mContext.getString(R.string.unknown_address);
        }

        Address address = addresses.get(0);
        ArrayList<String> addressFragments = new ArrayList<>();

        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
            addressFragments.add(address.getAddressLine(i));
        }

        return TextUtils.join(System.getProperty("line.separator"), addressFragments);
    }
}
