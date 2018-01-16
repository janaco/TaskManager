package com.nandy.taskmanager;

import com.google.android.gms.drive.DriveResourceClient;

/**
 * Created by yana on 16.01.18.
 */

public interface GoogleDriveClientCallback {

    void onGoogleDriveClientReady(DriveResourceClient driveResourceClient);
}
