package com.nandy.taskmanager.enums;

import android.support.annotation.StringRes;

import com.nandy.taskmanager.R;

import java.util.concurrent.TimeUnit;

/**
 * Created by yana on 20.01.18.
 */

public enum RepeatPeriod {

    NO_REPEAT(R.string.no_repeat, -1),
    EVERY_FIVE_MINUTES(R.string.every_five_mins, TimeUnit.MINUTES.toMillis(5)),
    EVERY_FIFTEEN_MINUTES(R.string.every_fifteen_mins, TimeUnit.MINUTES.toMillis(15)),
    EVERY_HOUR(R.string.every_hour, TimeUnit.HOURS.toMillis(1)),
    EVERY_DAY(R.string.every_day, TimeUnit.DAYS.toMillis(1));

    @StringRes
    private int mTextResId;
    private long mValue;

    RepeatPeriod(@StringRes int textResId, long value) {
        mTextResId = textResId;
        mValue = value;
    }

    public long getValue() {
        return mValue;
    }

    public int getTextResId() {
        return mTextResId;
    }
}
