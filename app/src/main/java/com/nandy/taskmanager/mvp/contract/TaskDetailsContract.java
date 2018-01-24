package com.nandy.taskmanager.mvp.contract;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;

/**
 * Created by yana on 25.01.18.
 */

public interface TaskDetailsContract {

    interface Presenter extends org.kaerdan.presenterretainer.Presenter<View> {

        void onActivityResult(int requestCode, int resultCode, Intent data);

        void setupMenu();

        void toggleStatus();

        void edit();

        void delete();

        void resetStart();

        void resetEnd();

        void pause();

        void resume();
    }

    interface View extends org.kaerdan.presenterretainer.Presenter.View {
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

        void setPauseOptionVisible(boolean visible);

        void setResumeOptionVisible(boolean visible);

        void launchActivityForResult(Bundle args, Class<?> cls, int requestCode);
    }
}
