package com.nandy.taskmanager.model;

import android.support.annotation.StringRes;

import com.nandy.taskmanager.R;

/**
 * Created by yana on 20.01.18.
 */

public enum RepeatPeriod {

    NO_REPEAT(R.string.no_repeat),
    ONCE_A_HOUR(R.string.once_a_hour),
    ONCE_A_DAY(R.string.once_a_day),
    ONCE_A_WEEK(R.string.once_a_week),
    ONCE_A_MONTH(R.string.once_a_month),
    ONCE_A_YEAR(R.string.once_a_year);

    @StringRes
    private int mTextResId;

    RepeatPeriod(@StringRes int textResId) {
        mTextResId = textResId;
    }

    public int getTextResId() {
        return mTextResId;
    }
}
