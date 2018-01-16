package com.nandy.taskmanager.backup;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by yana on 16.01.18.
 */

public interface Backup {

    void init(@NonNull final Activity activity);

    void start();

    void stop();

    GoogleApiClient getClient();
}
