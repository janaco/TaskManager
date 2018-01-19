package com.nandy.taskmanager.mvp.view;

import android.support.annotation.StringRes;

/**
 * Created by razomer on 18.01.18.
 */

public interface TaskDetailsView {

    void setTitle(String title);

    void setDescription(String description);

    void setTime(String time);

    void setStatus(String status);

    void setLocation(String location);

    void setControlButtonText(@StringRes int resId);

    void setControlButtonEnabled(boolean enabled);

    void loadImage(String image, boolean drawMapPin);

    void loadImage(int imageResId, boolean drawMapPin);

}
