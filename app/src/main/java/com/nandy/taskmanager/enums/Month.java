package com.nandy.taskmanager.enums;

import android.support.annotation.StringRes;

import com.nandy.taskmanager.R;

import java.util.Calendar;

/**
 * Created by yana on 25.01.18.
 */

public enum  Month {

    JANUARY(Calendar.JANUARY, R.string.january),
    FEBRUARY(Calendar.FEBRUARY, R.string.february),
    MARCH(Calendar.FEBRUARY, R.string.march),
    APRIL(Calendar.FEBRUARY, R.string.april),
    MAY(Calendar.FEBRUARY, R.string.may),
    JUNE(Calendar.FEBRUARY, R.string.june),
    JULY(Calendar.FEBRUARY, R.string.july),
    AUGUST(Calendar.FEBRUARY, R.string.august),
    SEPTEMBER(Calendar.FEBRUARY, R.string.september),
    OCTOBER(Calendar.FEBRUARY, R.string.october),
    NOVEMBER(Calendar.FEBRUARY, R.string.november),
    DECEMBER(Calendar.FEBRUARY, R.string.december);


    @StringRes
    private int mNameResId;
    private int mMonthOfYear;

    Month(int monthOfYear, @StringRes int nameResId){
        mMonthOfYear = monthOfYear;
        mNameResId = nameResId;
    }

    public int getNameResId() {
        return mNameResId;
    }

    public int getMonthOfYear() {
        return mMonthOfYear;
    }
}
