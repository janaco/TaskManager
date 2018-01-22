package com.nandy.taskmanager.mvp.view;

import android.support.annotation.StringRes;

/**
 * Created by razomer on 18.01.18.
 */

public interface TaskDetailsView {

    void setTitle(String title);

    void setDescription(String description);

    void setPlannedStartTime(String time);

    void setActualStartTime(String time);

    void setScheduledDuration(int value, @StringRes int textResId);

    void setTimeSpent(int value, @StringRes int textResId);

    void setRepeatPeriod(@StringRes int textResId);

    void setStatus(String status);

    void setLocation(String location);

    void setControlButtonText(@StringRes int resId);

    void setControlButtonEnabled(boolean enabled);

    void loadImage(String image, boolean drawMapPin);

    void loadImage(int imageResId, boolean drawMapPin);

    void setLocationVisible(boolean visible);

    void setActualStartDateVisible(boolean visible);

    void setTimeSpentVisible(boolean visible);

    void setResetStartMenuOptionEnabled(boolean enabled);

    void setResetEndMenuOptionEnabled(boolean enabled);

    void finish();

}
