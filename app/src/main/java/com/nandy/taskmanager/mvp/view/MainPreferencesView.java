package com.nandy.taskmanager.mvp.view;

import android.support.annotation.StringRes;

/**
 * Created by yana on 24.01.18.
 */

public interface MainPreferencesView {

    void setRestoreBackupPreferenceEnabled(boolean enabled);

    void showProgressDialog(@StringRes int textResId);

    void cancelProgressDialog();

    void showMessage(String message);

    void showMessage(@StringRes int textResId);
}
