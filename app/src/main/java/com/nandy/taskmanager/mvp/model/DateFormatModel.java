package com.nandy.taskmanager.mvp.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yana on 17.01.18.
 */

public class DateFormatModel {

    private static final String DATE_FORMAT= "dd.MM.yyyy hh:mm";
    private DateFormat mDateFormat;

    public DateFormatModel(){
        mDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    }

    public String format(Date date){

        return mDateFormat.format(date);
    }
}
