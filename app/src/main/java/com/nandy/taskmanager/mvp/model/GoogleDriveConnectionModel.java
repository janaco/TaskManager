package com.nandy.taskmanager.mvp.model;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.tasks.Task;
import com.nandy.taskmanager.Constants;
import com.nandy.taskmanager.GoogleDriveClientCallback;
import com.nandy.taskmanager.activity.SettingsActivity;

import java.util.HashSet;
import java.util.Set;

import static android.app.Activity.RESULT_OK;

/**
 * Created by yana on 16.01.18.
 */

public class GoogleDriveConnectionModel {

    private Activity mActivity;
    private GoogleDriveClientCallback mGoogleDriveClientCallback;

    public GoogleDriveConnectionModel(Activity activity){
        mActivity = activity;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_SIGN_IN:
                if (resultCode != RESULT_OK) {
                    //TODO: failed to sign in
                    return;
                }

                Task<GoogleSignInAccount> getAccountTask =
                        GoogleSignIn.getSignedInAccountFromIntent(data);
                if (getAccountTask.isSuccessful()) {
                    mGoogleDriveClientCallback.onGoogleDriveClientReady(Drive.getDriveResourceClient(mActivity, getAccountTask.getResult()));
                } else {
                    //TODO: failed to sign in
                }
                break;

        }
    }

    public void signIn(GoogleDriveClientCallback googleDriveClientCallback) {
        mGoogleDriveClientCallback = googleDriveClientCallback;

        Set<Scope> requiredScopes = new HashSet<>(2);
        requiredScopes.add(Drive.SCOPE_FILE);
        requiredScopes.add(Drive.SCOPE_APPFOLDER);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(mActivity);
        if (signInAccount != null && signInAccount.getGrantedScopes().containsAll(requiredScopes)) {
            mGoogleDriveClientCallback.onGoogleDriveClientReady(Drive.getDriveResourceClient(mActivity, signInAccount));
        } else {
            GoogleSignInOptions signInOptions =
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestScopes(Drive.SCOPE_FILE)
                            .requestScopes(Drive.SCOPE_APPFOLDER)
                            .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(mActivity, signInOptions);
            mActivity.startActivityForResult(googleSignInClient.getSignInIntent(), Constants.REQUEST_CODE_SIGN_IN);
        }
    }



}