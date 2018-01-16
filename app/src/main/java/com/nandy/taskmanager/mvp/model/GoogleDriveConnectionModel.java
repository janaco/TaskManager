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
import com.nandy.taskmanager.GoogleDriveClientCallback;

import java.util.HashSet;
import java.util.Set;

import static android.app.Activity.RESULT_OK;

/**
 * Created by yana on 16.01.18.
 */

public class GoogleDriveConnectionModel {


    protected static final int REQUEST_CODE_SIGN_IN = 0;

    private GoogleDriveClientCallback mGoogleDriveClientCallback;
    private Activity activity;

    public GoogleDriveConnectionModel(Activity activity){
        this.activity = activity;
    }

    public void setGoogleDriveClientCallback(GoogleDriveClientCallback mGoogleDriveClientCallback) {
        this.mGoogleDriveClientCallback = mGoogleDriveClientCallback;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode != RESULT_OK) {
                    //TODO: failed to sign in
                    return;
                }

                Task<GoogleSignInAccount> getAccountTask =
                        GoogleSignIn.getSignedInAccountFromIntent(data);
                if (getAccountTask.isSuccessful()) {
                    initializeDriveClient(getAccountTask.getResult());
                } else {
                    //TODO: failed to sign in
                }
                break;

        }
    }

    public void signIn() {
        Set<Scope> requiredScopes = new HashSet<>(2);
        requiredScopes.add(Drive.SCOPE_FILE);
        requiredScopes.add(Drive.SCOPE_APPFOLDER);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (signInAccount != null && signInAccount.getGrantedScopes().containsAll(requiredScopes)) {
            initializeDriveClient(signInAccount);
        } else {
            GoogleSignInOptions signInOptions =
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestScopes(Drive.SCOPE_FILE)
                            .requestScopes(Drive.SCOPE_APPFOLDER)
                            .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(activity, signInOptions);
            activity.startActivityForResult(googleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
        }
    }

    private void initializeDriveClient(GoogleSignInAccount signInAccount) {
        mGoogleDriveClientCallback.onGoogleDriveClientReady(Drive.getDriveResourceClient(activity, signInAccount));
    }


}