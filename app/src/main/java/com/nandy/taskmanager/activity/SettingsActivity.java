package com.nandy.taskmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.nandy.taskmanager.R;
import com.nandy.taskmanager.mvp.model.CreateBackupModel;
import com.nandy.taskmanager.mvp.model.GoogleDriveConnectionModel;
import com.nandy.taskmanager.mvp.model.RestoreFromBackupModel;
import com.nandy.taskmanager.mvp.presenter.SettingsPresenter;
import com.nandy.taskmanager.mvp.view.SettingsView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity implements SettingsView{

    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    private SettingsPresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);
        ButterKnife.bind(this);

        mPresenter = new SettingsPresenter(this);
        mPresenter.setCreateBackupModel(new CreateBackupModel(getApplicationContext()));
        mPresenter.setRestoreDataModel(new RestoreFromBackupModel(getApplicationContext()));
        mPresenter.setGoogleDriveConnectionModel(new GoogleDriveConnectionModel(this));

    }

    @OnClick(R.id.btn_backup)
    void onCreateBackupButtonClick(){
        mPresenter.backupData();
    }

    @OnClick(R.id.btn_restore)
    void onRestoreDataButtonClick(){
        mPresenter.restoreData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.stop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mPresenter.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPresenter.restoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setProgressBarVisible(boolean visible) {
        mProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setUploadingProgress(int progress) {
        mProgressBar.setProgress(progress);
    }
}