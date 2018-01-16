package com.nandy.taskmanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.android.gms.drive.events.OpenFileCallback;
import com.google.android.gms.drive.query.Filter;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.nandy.taskmanager.R;
import com.nandy.taskmanager.db.AppDatabase;
import com.nandy.taskmanager.db.dao.TasksDao;
import com.nandy.taskmanager.mvp.model.CreateBackupModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackupActivity extends DRiveActivity {

    private static final String TAG = "CreateFileInAppFolder";

    private ProgressBar mProgressBar;

    private ExecutorService mExecutorService;

    private CreateBackupModel mCreateBackupModel;

    @Override
    protected void onDriveClientReady() {
        mCreateBackupModel.setDriveResourceClient(getDriveResourceClient());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setMax(100);
        mExecutorService = Executors.newSingleThreadExecutor();

        mCreateBackupModel = new CreateBackupModel(getApplicationContext());

    }

    public void onClickCreateFile(View view) {
        mCreateBackupModel.createBackup();
    }

    public void onClickOpenFile(View view) {

        Query query = new Query.Builder()
                .addFilter(Filters.and(Filters.eq(SearchableField.TITLE, "tasks.db"), Filters.eq(SearchableField.MIME_TYPE, "application/x-sqlite3")))
                .build();
        Task<MetadataBuffer> queryTask = getDriveResourceClient().query(query);
        queryTask
                .addOnSuccessListener(this,
                        new OnSuccessListener<MetadataBuffer>() {
                            @Override
                            public void onSuccess(MetadataBuffer metadataBuffer) {
                                // Handle results...
                                // [START_EXCLUDE]
                                // [END_EXCLUDE]

                                Log.d("CONTENT_", "read.count " + metadataBuffer.getCount());
                                if (metadataBuffer.getCount() > 0) {

                                    Metadata metadata = metadataBuffer.get(0);
                                    DriveFile driveFile = metadata.getDriveId().asDriveFile();
                                    Log.d("CONTENT_", "read " + metadata.getTitle() + ", " + metadata.getMimeType() + ", " + metadata.getDriveId().encodeToString());
                                    retrieveContents(driveFile);

                                }

                            }
                        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mExecutorService.shutdown();
    }

    private void retrieveContents(DriveFile file) {
        OpenFileCallback openCallback = new OpenFileCallback() {
            @Override
            public void onProgress(long bytesDownloaded, long bytesExpected) {
                int progress = (int) (bytesDownloaded * 100 / bytesExpected);
                Log.d(TAG, String.format("Loading progress: %d percent", progress));
                mProgressBar.setProgress(progress);
            }

            @Override
            public void onContents(@NonNull DriveContents driveContents) {
                mProgressBar.setProgress(100);
                try {
                    unzipDB(driveContents.getInputStream());
                    importData(new File(getFilesDir(), "backup.db"));
                    showMessage("Loaded");
                    getDriveResourceClient().discardContents(driveContents);
                } catch (IOException e) {
                    onError(e);
                }
            }

            @Override
            public void onError(@NonNull Exception e) {
                e.printStackTrace();
                showMessage("Read failed");
            }
        };

        getDriveResourceClient().openFile(file, DriveFile.MODE_READ_ONLY, openCallback);
    }

    private void importData(File dbFile) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, 0);
        TasksDao tasksDao = AppDatabase.getInstance(getApplicationContext()).tasksDao();

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

        dbFile.delete();

    }

    private void unzipDB(InputStream inputStream) throws IOException {
        OutputStream outputStream = null;

        try {
            outputStream =
                    new FileOutputStream(new File(getFilesDir(), "backup.db"));

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            Log.d("CONTENT_", "write.DONE");

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