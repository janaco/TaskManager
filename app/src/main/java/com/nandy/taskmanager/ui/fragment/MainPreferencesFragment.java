package com.nandy.taskmanager.ui.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.Toast;

import com.nandy.taskmanager.R;
import com.nandy.taskmanager.mvp.model.CreateBackupModel;
import com.nandy.taskmanager.mvp.model.DataImportModel;
import com.nandy.taskmanager.mvp.model.GoogleDriveConnectionModel;
import com.nandy.taskmanager.mvp.model.RestoreFromBackupModel;
import com.nandy.taskmanager.mvp.presenter.MainPreferencesPresenter;
import com.nandy.taskmanager.mvp.view.MainPreferencesView;

/**
 * Created by yana on 24.01.18.
 */

public class MainPreferencesFragment extends PreferenceFragment
        implements Preference.OnPreferenceClickListener, MainPreferencesView {


    private static final String KEY_CREATE_BACKUP = "KEY_CREATE_BACKUP";
    private static final String KEY_RESTORE_BACKUP = "KEY_RESTORE_BACKUP";

    private static final String KEY_LOCATION_TRACKING = "pref_location_tracking";

    private MainPreferencesPresenter mPresenter;
    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main_preferences);

        findPreference(KEY_CREATE_BACKUP).setOnPreferenceClickListener(this);
        findPreference(KEY_RESTORE_BACKUP).setOnPreferenceClickListener(this);

        CheckBoxPreference prefLocationTracking = (CheckBoxPreference) getPreferenceManager().findPreference(KEY_LOCATION_TRACKING);
        if (prefLocationTracking != null) {
            prefLocationTracking.setOnPreferenceChangeListener((preference, newValue) -> {
                if (newValue instanceof Boolean) {
                    mPresenter.onLocationTrackingPreferenceChanged((Boolean) newValue);
                }
                return true;
            });

        }

        mPresenter = new MainPreferencesPresenter(this);
        mPresenter.setCreateBackupModel(new CreateBackupModel(getActivity().getApplicationContext()));
        mPresenter.setRestoreDataModel(new RestoreFromBackupModel(getActivity().getApplicationContext()));
        mPresenter.setGoogleDriveConnectionModel(new GoogleDriveConnectionModel(getActivity()));
        mPresenter.setDataImportModel(new DataImportModel(getActivity().getApplicationContext()));
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.start();
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {

        switch (preference.getKey()) {

            case KEY_CREATE_BACKUP:
                mPresenter.createBackup();
                return true;

            case KEY_RESTORE_BACKUP:
                mPresenter.restoreFromBackup();
                return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setRestoreBackupPreferenceEnabled(boolean enabled) {
        Preference preference = findPreference(KEY_RESTORE_BACKUP);

        SpannableString spannable = new SpannableString(preference.getTitle());
        int colorResId = enabled ? R.color.primaryTextColor : android.R.color.secondary_text_dark_nodisable;
        spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity().getApplicationContext(), colorResId)), 0, spannable.length(), 0);

        preference.setTitle(spannable);
        preference.setEnabled(enabled);
    }

    @Override
    public void showProgressDialog(int textResId) {
        mProgressDialog = ProgressDialog.show(getActivity(), null, getString(textResId), true, false);
    }

    @Override
    public void cancelProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(int textResId) {
        showMessage(getString(textResId));
    }
}