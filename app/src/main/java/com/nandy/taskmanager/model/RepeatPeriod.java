package com.nandy.taskmanager.model;

import android.support.annotation.StringRes;

import com.nandy.taskmanager.R;

import java.util.concurrent.TimeUnit;

/**
 * Created by yana on 20.01.18.
 */

public enum RepeatPeriod {

    NO_REPEAT(R.string.no_repeat, -1),
    ONCE_A_HOUR(R.string.once_a_hour, TimeUnit.HOURS.toMillis(1)),
    ONCE_A_DAY(R.string.once_a_day, TimeUnit.DAYS.toMillis(1)),
    ONCE_A_WEEK(R.string.once_a_week, TimeUnit.DAYS.toMillis(7)),
    ONCE_A_MONTH(R.string.once_a_month, TimeUnit.DAYS.toMillis(30)),
    ONCE_A_YEAR(R.string.once_a_year, TimeUnit.DAYS.toMillis(365));

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
