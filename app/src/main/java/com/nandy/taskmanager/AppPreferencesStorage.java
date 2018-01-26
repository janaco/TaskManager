package com.nandy.taskmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.nandy.taskmanager.enums.Duration;

/**
 * Created by yana on 26.01.18.
 */

public class AppPreferencesStorage {

    public static final String KEY_CREATE_BACKUP = "KEY_CREATE_BACKUP";
    public static final String KEY_RESTORE_BACKUP = "KEY_RESTORE_BACKUP";
    public static final String KEY_LOCATION_TRACKING = "pref_location_tracking";
    public static final String KEY_DURATION = "pref_duration";


    public static boolean isLocationTrackingEnabled(Context context) {
        return getPreferences(context).getBoolean(KEY_LOCATION_TRACKING, true);
    }

    public static Duration getDefaultDuration(Context context) {

        return Duration
                .valueOf(getPreferences(context).getString(KEY_DURATION, Duration.FIVE_MINUTES.name()));
    }

    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
