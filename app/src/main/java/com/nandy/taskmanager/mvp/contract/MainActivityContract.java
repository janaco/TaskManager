package com.nandy.taskmanager.mvp.contract;

import android.os.Bundle;

/**
 * Created by yana on 24.01.18.
 */

public interface MainActivityContract {

    interface Presenter extends org.kaerdan.presenterretainer.Presenter<View> {

        void onCreateTaskClick();

        void onFillListOptionSelected();

        void onClearAllOptionSelected();

        void onSettingsOptionSelected();

        void onExitOptionSelected();
    }

    interface View extends org.kaerdan.presenterretainer.Presenter.View {
        void launchActivity(Bundle args, Class<?> cls);

        void launchActivityForResult(Bundle args, Class<?> cls, int requestCode);

        void finish();
    }

}
