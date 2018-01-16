package com.nandy.taskmanager.mvp.model;

import android.content.Context;

import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
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

public class CreateBackupModel extends BackupModel{

    private Context mContext;
    private DriveResourceClient mDriveResourceClient;

    public CreateBackupModel(Context context) {
        mContext = context;
    }

    public void setDriveResourceClient(DriveResourceClient mDriveResourceClient) {
        this.mDriveResourceClient = mDriveResourceClient;
    }

    public void createBackup() {

        mDriveResourceClient.query(buildBackupFilesQuery())
                .addOnSuccessListener(
                        metadataBuffer -> {

                            if (metadataBuffer.getCount() > 0) {
                                for (int i = 0; i < metadataBuffer.getCount(); i++) {
                                    deleteFile(metadataBuffer.get(i).getDriveId().asDriveFile());
                                }
                            }
                            createBackupFile();

                        })
                .addOnFailureListener(Throwable::printStackTrace);
    }

    private void createBackupFile() {
        final Task<DriveFolder> appFolderTask = mDriveResourceClient.getRootFolder();
        final Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();

        Tasks.whenAll(appFolderTask, createContentsTask)
                .continueWithTask(task -> {
                    DriveFolder parent = appFolderTask.getResult();
                    DriveContents contents = createContentsTask.getResult();
                    OutputStream outputStream = contents.getOutputStream();

                    writeDatabaseToFile(outputStream);

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(BACKUP_FILE_NAME)
                            .setMimeType(BACKUP_MIME_TYPE)
                            .build();


                    return mDriveResourceClient.createFile(parent, changeSet, contents);
                })
                .addOnSuccessListener(
                        driveFile -> {
                            //TODO
                        })
                .addOnFailureListener(Throwable::printStackTrace);
    }




    private void deleteFile(DriveFile file) {
        mDriveResourceClient.delete(file);
    }


    private String getPathToDatabase() {
        return mContext.getDatabasePath(AppDatabase.DB_NAME).getAbsolutePath();
    }


    private void writeDatabaseToFile(OutputStream outputStream) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(getPathToDatabase()));
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
