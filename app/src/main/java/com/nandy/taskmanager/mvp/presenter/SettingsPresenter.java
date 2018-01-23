package com.nandy.taskmanager.mvp.presenter;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.drive.DriveResourceClient;
import com.nandy.taskmanager.GoogleDriveClientCallback;
import com.nandy.taskmanager.ProgressListener;
import com.nandy.taskmanager.mvp.BasePresenter;
import com.nandy.taskmanager.mvp.model.CreateBackupModel;
import com.nandy.taskmanager.mvp.model.GoogleDriveConnectionModel;
import com.nandy.taskmanager.mvp.model.RestoreFromBackupModel;
import com.nandy.taskmanager.mvp.view.SettingsView;

/**
 * Created by yana on 16.01.18.
 */

public class SettingsPresenter extends BasePresenter implements GoogleDriveClientCallback, ProgressListener {

    private final SettingsView mView;
    private CreateBackupModel mCreateBackupModel;
    private RestoreFromBackupModel mRestoreDataModel;
    private GoogleDriveConnectionModel mGoogleDriveConnectionModel;

    public SettingsPresenter(SettingsView view) {
        mView = view;
    }

    @Override
    public void start() {
        mGoogleDriveConnectionModel.signIn();
    }

    @Override
    public void stop() {

    }

    @Override
    public void onProgressChanged(int progress) {
        mView.setUploadingProgress(progress);
    }

    @Override
    public void onGoogleDriveClientReady(DriveResourceClient driveResourceClient) {
        mCreateBackupModel.setDriveResourceClient(driveResourceClient);
        mRestoreDataModel.setDriveResourceClient(driveResourceClient);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mGoogleDriveConnectionModel.onActivityResult(requestCode, resultCode, data);
    }

    public void backupData() {
        mCreateBackupModel.createBackup();
    }

    public void restoreData() {
        mRestoreDataModel.restoreBackup();
    }

    public void setCreateBackupModel(CreateBackupModel mCreateBackupModel) {
        this.mCreateBackupModel = mCreateBackupModel;
    }

    public void setRestoreDataModel(RestoreFromBackupModel mRestoreDataModel) {
        this.mRestoreDataModel = mRestoreDataModel;
    }

    public void setGoogleDriveConnectionModel(GoogleDriveConnectionModel mGoogleDriveConnectionModel) {
        this.mGoogleDriveConnectionModel = mGoogleDriveConnectionModel;
    }


}
