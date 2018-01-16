package com.nandy.taskmanager.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;

import com.nandy.taskmanager.ProgressListener;
import com.nandy.taskmanager.R;
import com.nandy.taskmanager.mvp.model.CreateBackupModel;
import com.nandy.taskmanager.mvp.model.RestoreFromBackupModel;

public class BackupActivity extends DRiveActivity implements ProgressListener{

    private ProgressBar mProgressBar;


    private CreateBackupModel mCreateBackupModel;
    private RestoreFromBackupModel mRestoreFromBackupModel;

    @Override
    protected void onDriveClientReady() {
        mCreateBackupModel.setDriveResourceClient(getDriveResourceClient());
        mRestoreFromBackupModel.setDriveResourceClient(getDriveResourceClient());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setMax(100);

        mCreateBackupModel = new CreateBackupModel(getApplicationContext());
        mRestoreFromBackupModel = new RestoreFromBackupModel(getApplicationContext());
        mRestoreFromBackupModel.setProgressListener(this);
    }

    public void onClickCreateFile(View view) {
        mCreateBackupModel.createBackup();
    }

    public void onClickOpenFile(View view) {
        mRestoreFromBackupModel.restoreBackup();
    }


    @Override
    public void onProgressChanged(int progress) {
        mProgressBar.setProgress(progress);
    }
}