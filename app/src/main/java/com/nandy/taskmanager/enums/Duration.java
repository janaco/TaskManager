package com.nandy.taskmanager.enums;

import android.support.annotation.StringRes;

import com.nandy.taskmanager.R;

import java.util.concurrent.TimeUnit;

/**
 * Created by yana on 26.01.18.
 */

public enum  Duration {

    ONE_MINUTE(TimeUnit.MINUTES.toMillis(1), R.string.one_minute),
    FIVE_MINUTES(TimeUnit.MINUTES.toMillis(5), R.string.five_minutes),
    FIFTEEN_MINUTES(TimeUnit.MINUTES.toMillis(15), R.string.fifteen_minutes),
    HALF_OF_HOUR(TimeUnit.MINUTES.toMillis(30), R.string.half_of_hour),
    ONE_HOUR(TimeUnit.HOURS.toMillis(1), R.string.one_hour),
    TWO_HOURS(TimeUnit.HOURS.toMillis(2), R.string.two_hours);

    @StringRes
    private int mTextResId;
    private long mDuration;

    Duration(long duration, int textResId){
        mDuration = duration;
        mTextResId = textResId;
    }

    public int getTextResId() {
        return mTextResId;
    }

    public long getDuration() {
        return mDuration;
    }
}
