package com.nandy.taskmanager.mvp.model;

import android.content.Context;
import android.support.annotation.StringRes;

import com.nandy.taskmanager.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by yana on 17.01.18.
 */

public class DateFormatModel {

    private static final int SECONDS_IN_MINUTE = 60;
    private static final int MINUTES_IN_HOUR = 60;
    private static final int HOURS_IN_DAY = 24;

    private static final String DATE_FORMAT = "dd.MM.yyyy";
    private static final String TIME_FORMAT = "hh:mm";
    private static final String FULL_DATE_FORMAT = "dd.MM.yyyy hh:mm";

    private DateFormat mDateFormat;
    private DateFormat mTimeFormat;
    private DateFormat mFullDateFormat;
    private Context mContext;

    public DateFormatModel(Context context) {
        mContext = context;
        mDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        mTimeFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
        mFullDateFormat = new SimpleDateFormat(FULL_DATE_FORMAT, Locale.getDefault());
    }

    public String formatDate(Date date) {

        return mDateFormat.format(date);
    }

    public String formatAsFullDate(Date date) {

        return mFullDateFormat.format(date);
    }

    public String formatTime(Date date) {

        return mTimeFormat.format(date);
    }

    public int convertToSeconds(long millis) {

        return (int) TimeUnit.MILLISECONDS.toSeconds(millis);
    }


    public int convertToMinutes(long millis) {

        return (int) TimeUnit.MILLISECONDS.toMinutes(millis);
    }

    public String formatDuration(long duration) {

        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        if (seconds < SECONDS_IN_MINUTE){
            return format(seconds, R.string.s);
        }

        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        if (minutes < MINUTES_IN_HOUR){
            return format(minutes, R.string.min);
        }

        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        if (hours < HOURS_IN_DAY){
            return format(hours, R.string.h);

        }

        long days = TimeUnit.MILLISECONDS.toDays(duration);
        return format(days, R.string.days);

    }

    private String format(long value, @StringRes int unitsTextResId){

        return String.format(Locale.getDefault(), "%d %s", value, mContext.getString(unitsTextResId));
    }
}
