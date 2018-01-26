package com.nandy.taskmanager.mvp.presenter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.events.OpenFileCallback;
import com.nandy.taskmanager.R;
import com.nandy.taskmanager.eventbus.LocationTrackingEnabledStateChangedEvent;
import com.nandy.taskmanager.mvp.contract.MainPreferencesContract;
import com.nandy.taskmanager.mvp.model.CreateBackupModel;
import com.nandy.taskmanager.mvp.model.DataImportModel;
import com.nandy.taskmanager.mvp.model.GoogleDriveConnectionModel;
import com.nandy.taskmanager.mvp.model.RestoreFromBackupModel;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;

/**
 * Created by yana on 24.01.18.
 */

public class MainPreferencesPresenter  implements MainPreferencesContract.Presenter{

    private final MainPreferencesContract.View mView;
    private CreateBackupModel mCreateBackupModel;
    private RestoreFromBackupModel mRestoreDataModel;
    private GoogleDriveConnectionModel mGoogleDriveConnectionModel;
    private DataImportModel mDataImportModel;

    public MainPreferencesPresenter(MainPreferencesContract.View view) {
        mView = view;
    }

    public void start() {
        if (mCreateBackupModel.isGoogleClient()) {
            mCreateBackupModel.requestExistingBackupFiles()
                    .addOnSuccessListener(metadataBuffer -> mView.setRestoreBackupPreferenceEnabled(metadataBuffer.getCount() > 0));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            mGoogleDriveConnectionModel.onActivityResult(requestCode, resultCode, data);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            mView.showMessage(throwable.getMessage());
        }
    }

    @Override
    public void createBackup() {

        mGoogleDriveConnectionModel.signIn(driveResourceClient -> {
            mCreateBackupModel.setDriveResourceClient(driveResourceClient);
            mRestoreDataModel.setDriveResourceClient(driveResourceClient);

            mView.showProgressDialog(R.string.creating_backup);

            mCreateBackupModel.
                    requestExistingBackupFiles()
                    .addOnSuccessListener(metadataBuffer -> {

                        if (metadataBuffer.getCount() > 0) {
                            mCreateBackupModel.removeExistingBackupFiles(metadataBuffer);
                        }
                        updateOrCreateBackup();

                    })
                    .addOnFailureListener(this::onError);
        });

    }


    private void onError(Throwable t) {
        t.printStackTrace();
        mView.cancelProgressDialog();
        mView.showMessage(t.getMessage());
    }

    private void updateOrCreateBackup() {
        mCreateBackupModel.createAndUploadBackup()
                .addOnSuccessListener(driveFile -> {
                    mView.cancelProgressDialog();
                    mView.showMessage(R.string.backup_created_successfully);
                    mView.setRestoreBackupPreferenceEnabled(true);
                })
                .addOnFailureListener(this::onError);

    }


    @Override
    public void restoreFromBackup() {

        mGoogleDriveConnectionModel.signIn(driveResourceClient -> {
            mCreateBackupModel.setDriveResourceClient(driveResourceClient);
            mRestoreDataModel.setDriveResourceClient(driveResourceClient);

            mView.showProgressDialog(R.string.restoring_data);
            mRestoreDataModel.requestExistingBackupFiles()
                    .addOnSuccessListener(metadataBuffer -> {

                        Log.d("BACKUP_", "restore: " + metadataBuffer.getCount());
                        if (metadataBuffer.getCount() > 0) {
                            downloadAndRestoreBackup(metadataBuffer.get(0).getDriveId().asDriveFile());
                        } else {
                            mView.cancelProgressDialog();
                            mView.showMessage(R.string.no_backups_found);
                        }
                    })
                    .addOnFailureListener(this::onError);
        });

    }

    private void downloadAndRestoreBackup(DriveFile driveFile) {

        mRestoreDataModel.openFile(driveFile, new OpenFileCallback() {
            @Override
            public void onProgress(long l, long l1) {
                //nothing to do
            }

            @SuppressWarnings("ResultOfMethodCallIgnored")
            @Override
            public void onContents(@NonNull DriveContents driveContents) {
                try {
                    mRestoreDataModel.restoreBackup(driveContents);
                    File dbBackupFile = mRestoreDataModel.getBackupDbFile();
                    mDataImportModel.importData(dbBackupFile)
                            .subscribe(() -> {
                                dbBackupFile.delete();
                                mView.cancelProgressDialog();
                                mView.showMessage(R.string.data_restored);
                            }, MainPreferencesPresenter.this::onError);

                } catch (IOException e) {
                    onError(e);
                }
            }

            @Override
            public void onError(@NonNull Exception e) {
                MainPreferencesPresenter.this.onError(e);
            }
        });
    }

    @Override
    public void onLocationTrackingPreferenceChanged(boolean enabled){
        EventBus.getDefault().post(new LocationTrackingEnabledStateChangedEvent(enabled));
    }

    public void setCreateBackupModel(CreateBackupModel createBackupModel) {
        mCreateBackupModel = createBackupModel;
    }

    public void setRestoreDataModel(RestoreFromBackupModel restoreDataModel) {
        mRestoreDataModel = restoreDataModel;
    }

    public void setGoogleDriveConnectionModel(GoogleDriveConnectionModel googleDriveConnectionModel) {
        mGoogleDriveConnectionModel = googleDriveConnectionModel;
    }

    public void setDataImportModel(DataImportModel dataImportModel) {
        mDataImportModel = dataImportModel;
    }
}