package com.nandy.taskmanager.mvp.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.events.OpenFileCallback;
import com.nandy.taskmanager.ProgressListener;
import com.nandy.taskmanager.db.AppDatabase;
import com.nandy.taskmanager.db.dao.TasksDao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by yana on 16.01.18.
 */

public class RestoreFromBackupModel extends BackupModel {

    private Context mContext;
    private DriveResourceClient mDriveResourceClient;
    private ProgressListener mProgressListener;

    public RestoreFromBackupModel(Context context) {
        mContext = context;
    }

    public void setDriveResourceClient(DriveResourceClient mDriveResourceClient) {
        this.mDriveResourceClient = mDriveResourceClient;
    }

    public void setProgressListener(ProgressListener mProgressListener) {
        this.mProgressListener = mProgressListener;
    }

    public void restoreBackup() {

        mDriveResourceClient.query(buildBackupFilesQuery())
                .addOnSuccessListener(
                        metadataBuffer -> {

                            if (metadataBuffer.getCount() > 0) {
                                retrieveContents(metadataBuffer.get(0).getDriveId().asDriveFile());
                            } else {
                                //TODO: no backup files
                            }

                        })
                .addOnFailureListener(Throwable::printStackTrace);
    }


    private void retrieveContents(DriveFile file) {
        OpenFileCallback openCallback = new OpenFileCallback() {
            @Override
            public void onProgress(long bytesDownloaded, long bytesExpected) {
                int progress = (int) (bytesDownloaded * 100 / bytesExpected);
                mProgressListener.onProgressChanged(progress);
            }

            @Override
            public void onContents(@NonNull DriveContents driveContents) {
                try {
                    unzipDB(driveContents.getInputStream());
                    File backupFile = getLocalTempBackupFile();
                    importData(backupFile);
                    backupFile.delete();
                    mDriveResourceClient.discardContents(driveContents);
                } catch (IOException e) {
                    onError(e);
                }
            }

            @Override
            public void onError(@NonNull Exception e) {
                e.printStackTrace();
            }
        };

        mDriveResourceClient.openFile(file, DriveFile.MODE_READ_ONLY, openCallback);
    }

    private void importData(File dbFile) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, 0);
        TasksDao tasksDao = AppDatabase.getInstance(mContext).tasksDao();

        Cursor cursor = db.rawQuery("SELECT * FROM tasks", null);

        if (cursor.moveToFirst()) {

            do {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String comment = cursor.getString(cursor.getColumnIndex("comment"));

                tasksDao.insert(new com.nandy.taskmanager.model.Task(id, title, comment));

            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    private void unzipDB(InputStream inputStream) throws IOException {
        OutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(getLocalTempBackupFile());

            int read = 0;
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

    private File getLocalTempBackupFile(){
        return new File(mContext.getFilesDir(), "backup.db");
    }
}