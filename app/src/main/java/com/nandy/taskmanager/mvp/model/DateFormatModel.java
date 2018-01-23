package com.nandy.taskmanager.mvp.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by yana on 17.01.18.
 */

public class DateFormatModel {

    private static final String DATE_FORMAT= "dd.MM.yyyy";
    private static final String TIME_FORMAT= "hh:mm";
    private static final String FULL_DATE_FORMAT= "dd.MM.yyyy hh:mm";

    private DateFormat mDateFormat;
    private DateFormat mTimeFormat;
    private DateFormat mFullDateFormat;

    public DateFormatModel(){
        mDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        mTimeFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
        mFullDateFormat = new SimpleDateFormat(FULL_DATE_FORMAT, Locale.getDefault());
    }

    public String formatDate(Date date){

        return mDateFormat.format(date);
    }

    public String formatAsFullDate(Date date){

        return mFullDateFormat.format(date);
    }

    public String formatTime(Date date){

        return mTimeFormat.format(date);
    }

    public int convertToSeconds(long millis){

        return (int) TimeUnit.MILLISECONDS.toSeconds(millis);
    }


    public int convertToMinutes(long millis){

        return (int) TimeUnit.MILLISECONDS.toMinutes(millis);
    }
}
