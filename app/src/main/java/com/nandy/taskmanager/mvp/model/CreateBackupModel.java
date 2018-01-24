package com.nandy.taskmanager.mvp.model;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.nandy.taskmanager.db.AppDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by yana on 16.01.18.
 */

public class CreateBackupModel extends BackupModel {

    public CreateBackupModel(Context context) {
        super(context);
    }

    public void removeExistingBackupFiles(MetadataBuffer metadataBuffer) {
        for (int i = 0; i < metadataBuffer.getCount(); i++) {
            deleteFile(metadataBuffer.get(i).getDriveId().asDriveFile());
        }
    }

    public File[] getFilesToBackup() {
        return new File[]{
                mContext.getFilesDir(),
                mContext.getDatabasePath(AppDatabase.DB_NAME)
        };
    }

    public Task<DriveFile> createAndUploadBackup() {
        final Task<DriveFolder> appFolderTask = mDriveResourceClient.getRootFolder();
        final Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();

        return Tasks.whenAll(appFolderTask, createContentsTask)
                .continueWithTask(task -> {
                    DriveFolder parent = appFolderTask.getResult();
                    DriveContents contents = createContentsTask.getResult();
                    OutputStream outputStream = contents.getOutputStream();

                    File backupFile = compress(getFilesToBackup(), BACKUP_FILE_NAME, BACKUP_DB_FILE);
                    write(outputStream, backupFile);

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(BACKUP_FILE_NAME)
                            .setMimeType(BACKUP_MIME_TYPE)
                            .build();


                    return mDriveResourceClient.createFile(parent, changeSet, contents);
                });
    }


    private void deleteFile(DriveFile file) {
        Log.d("BACKUP_", "deleteFile: " + file);
        mDriveResourceClient.delete(file);
    }


    private void write(OutputStream outputStream, File file) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        byte[] buf = new byte[1024];
        int bytesRead;
        try {
            if (inputStream != null) {
                while ((bytesRead = inputStream.read(buf)) > 0) {
                    outputStream.write(buf, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
