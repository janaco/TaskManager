package com.nandy.taskmanager.mvp.model;

import android.content.Context;

import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.events.OpenFileCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by yana on 16.01.18.
 */

public class RestoreFromBackupModel extends BackupModel {

    public RestoreFromBackupModel(Context context) {
        super(context);
    }


    public void openFile(DriveFile driveFile, OpenFileCallback openFileCallback) {
        mDriveResourceClient.openFile(driveFile, DriveFile.MODE_READ_ONLY, openFileCallback);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void restoreBackup(DriveContents driveContents) throws IOException {

        File backupFile = retrieveContents(driveContents);
        unzip(BACKUP_FILE_NAME);
        backupFile.delete();
    }

    public File getBackupDbFile() {
        return new File(mContext.getFilesDir(), BACKUP_DB_FILE);

    }

    private File retrieveContents(DriveContents driveContents) throws IOException {
        File backupFile = new File(mContext.getFilesDir(), BACKUP_FILE_NAME);
        write(driveContents.getInputStream(), backupFile);
        mDriveResourceClient.discardContents(driveContents);
        unzip(BACKUP_FILE_NAME);

        return backupFile;

    }

    private void write(InputStream inputStream, File file) throws IOException {
        OutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(file);

            int read;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    // outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }

}