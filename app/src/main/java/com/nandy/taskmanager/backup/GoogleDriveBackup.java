//package com.nandy.taskmanager.backup;
//
//import android.app.Activity;
//import android.content.IntentSender;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.util.Log;
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GoogleApiAvailability;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.ResultCallback;
//import com.google.android.gms.drive.Drive;
//import com.google.android.gms.drive.DriveApi;
//import com.google.android.gms.drive.DriveContents;
//import com.google.android.gms.drive.DriveFile;
//import com.google.android.gms.drive.DriveFolder;
//import com.google.android.gms.drive.DriveId;
//import com.google.android.gms.drive.MetadataChangeSet;
//import com.google.firebase.crash.FirebaseCrash;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.lang.ref.WeakReference;
//
///**
// * Created by yana on 16.01.18.
// */
//
//public class GoogleDriveBackup implements Backup, GoogleApiClient.OnConnectionFailedListener {
//    @Nullable
//    private GoogleApiClient googleApiClient;
//
//    @Nullable
//    private WeakReference<Activity> activityRef;
//
//
//    @Override
//    public void init(@NonNull final Activity activity) {
//        this.activityRef = new WeakReference<>(activity);
//
//        googleApiClient = new GoogleApiClient.Builder(activity)
//                .addApi(Drive.API)
//                .addScope(Drive.SCOPE_FILE)
//                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
//                    @Override
//                    public void onConnected(Bundle bundle) {
//                        // Do nothing
//                    }
//
//                    @Override
//                    public void onConnectionSuspended(int i) {
//
//                    }
//                })
//                .addOnConnectionFailedListener(this)
//                .build();
//
//    }
//
//    @Override
//    public GoogleApiClient getClient() {
//        return googleApiClient;
//    }
//
//    @Override
//    public void start() {
//        if (googleApiClient != null) {
//            googleApiClient.connect();
//        } else {
//            throw new IllegalStateException("You should call init before start");
//        }
//    }
//
//    @Override
//    public void stop() {
//        if (googleApiClient != null) {
//            googleApiClient.disconnect();
//        } else {
//            throw new IllegalStateException("You should call init before start");
//        }
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull final ConnectionResult result) {
//        Log.i("Connection Failed", "GoogleApiClient connection failed: " + result.toString());
//
//        if (result.hasResolution() && activityRef != null && activityRef.get() != null) {
//            Activity a = activityRef.get();
//            // show the localized error dialog.
//            try {
//                result.startResolutionForResult(a, 1);
//            } catch (IntentSender.SendIntentException e) {
//                e.printStackTrace();
//                GoogleApiAvailability.getInstance().getErrorDialog(a, result.getErrorCode(), 0).show();
//            }
//        } else {
//            Log.d("error", "cannot resolve connection issue");
//        }
//    }
//
//
//    private void uploadToDrive(DriveId mFolderDriveId) {
//        if (mFolderDriveId != null) {
//            //Create the file on GDrive
//            final DriveFolder folder = mFolderDriveId.asDriveFolder();
//            Drive.DriveApi.newDriveContents(googleApiClient)
//                    .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
//                        @Override
//                        public void onResult(DriveApi.DriveContentsResult result) {
//                            if (!result.getStatus().isSuccess()) {
//                                Log.e("BACKUP_", "Error while trying to create new file contents");
//                                showErrorDialog();
//                                return;
//                            }
//                            final DriveContents driveContents = result.getDriveContents();
//
//                            // Perform I/O off the UI thread.
//                            new Thread() {
//                                @Override
//                                public void run() {
//                                    // write content to DriveContents
//                                    OutputStream outputStream = driveContents.getOutputStream();
//
//                                    FileInputStream inputStream = null;
//                                    try {
//                                        inputStream = new FileInputStream(new File(realm.getPath()));
//                                    } catch (FileNotFoundException e) {
//                                        showErrorDialog();
//                                        e.printStackTrace();
//                                    }
//
//                                    byte[] buf = new byte[1024];
//                                    int bytesRead;
//                                    try {
//                                        if (inputStream != null) {
//                                            while ((bytesRead = inputStream.read(buf)) > 0) {
//                                                outputStream.write(buf, 0, bytesRead);
//                                            }
//                                        }
//                                    } catch (IOException e) {
//                                        showErrorDialog();
//                                        e.printStackTrace();
//                                    }
//
//
//                                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
//                                            .setTitle("glucosio.realm")
//                                            .setMimeType("text/plain")
//                                            .build();
//
//                                    // create a file in selected folder
//                                    folder.createFile(googleApiClient, changeSet, driveContents)
//                                            .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
//                                                @Override
//                                                public void onResult(DriveFolder.DriveFileResult result) {
//                                                    if (!result.getStatus().isSuccess()) {
//                                                        Log.d(TAG, "Error while trying to create the file");
//                                                        showErrorDialog();
//                                                        finish();
//                                                        return;
//                                                    }
//                                                    showSuccessDialog();
//                                                    finish();
//                                                }
//                                            });
//                                }
//                            }.start();
//                        }
//                    });
//        }
//    }
//
//    private void downloadFromDrive(DriveFile file) {
//        file.open(googleApiClient, DriveFile.MODE_READ_ONLY, null)
//                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
//                    @Override
//                    public void onResult(DriveApi.DriveContentsResult result) {
//                        if (!result.getStatus().isSuccess()) {
//                            showErrorDialog();
//                            return;
//                        }
//
//                        // DriveContents object contains pointers
//                        // to the actual byte stream
//                        DriveContents contents = result.getDriveContents();
//                        InputStream input = contents.getInputStream();
//
//                        try {
//                            File file = new File(realm.getPath());
//                            OutputStream output = new FileOutputStream(file);
//                            try {
//                                try {
//                                    byte[] buffer = new byte[4 * 1024]; // or other buffer size
//                                    int read;
//
//                                    while ((read = input.read(buffer)) != -1) {
//                                        output.write(buffer, 0, read);
//                                    }
//                                    output.flush();
//                                } finally {
//                                    output.close();
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        } finally {
//                            try {
//                                input.close();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                });
//    }
//}