package com.nandy.taskmanager.mvp.contract;

import android.content.Intent;
import android.support.annotation.StringRes;

/**
 * Created by yana on 26.01.18.
 */

public interface MainPreferencesContract {

    interface View{
        void setRestoreBackupPreferenceEnabled(boolean enabled);

        void showProgressDialog(@StringRes int textResId);

        void cancelProgressDialog();

        void showMessage(String message);

        void showMessage(@StringRes int textResId);
    }

    interface  Presenter{
        void start();

        void onActivityResult(int requestCode, int resultCode, Intent data) ;

        void createBackup();

        void restoreFromBackup() ;

        void onLocationTrackingPreferenceChanged(boolean enabled);
    }
}
