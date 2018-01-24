package com.nandy.taskmanager.mvp.model;

import android.content.Context;
import android.webkit.MimeTypeMap;

import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Task;
import com.nandy.taskmanager.db.AppDatabase;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by yana on 16.01.18.
 */

public abstract class BackupModel {

    public final static String BACKUP_FILE_NAME = "tasks_backup.zip";
    public static final String BACKUP_DB_FILE = "backup.db";
    private static final int BUFFER_SIZE = 2048;
    private static final String IMAGE_EXTENSION = ".png";
    public static final String BACKUP_MIME_TYPE = "application/zip";


    protected final Context mContext;
    protected DriveResourceClient mDriveResourceClient;


    public BackupModel(Context context) {
        mContext = context;
    }

    public boolean isGoogleClient(){
        return mDriveResourceClient != null;
    }

    public void setDriveResourceClient(DriveResourceClient mDriveResourceClient) {
        this.mDriveResourceClient = mDriveResourceClient;
    }

    public Task<MetadataBuffer> requestExistingBackupFiles() {
        return mDriveResourceClient.query(buildRequestBackupFilesQuery());
    }

    protected Query buildRequestBackupFilesQuery() {
        return new Query.Builder()
                .addFilter(
                        Filters.and(
                                Filters.eq(SearchableField.TITLE, BACKUP_FILE_NAME),
                                Filters.eq(SearchableField.MIME_TYPE, BACKUP_MIME_TYPE)))
                .build();
    }



    protected File compress(File[] files, String zipFileName, String dbFileName) {

        File zipFile = createBackupZipFile(mContext.getFilesDir(), zipFileName);
        byte buffer[] = new byte[BUFFER_SIZE];

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
            ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(fileOutputStream));

            for (File file : files) {

                if (file.isDirectory()) {
                    checkDir(zipOutputStream, file, buffer);
                } else if (file.getPath().endsWith(AppDatabase.DB_NAME)) {
                    compress(zipOutputStream, file, dbFileName, buffer);
                } else if (file.getPath().endsWith(IMAGE_EXTENSION)) {
                    compress(zipOutputStream, file, getFileName(file), buffer);
                }

            }
            zipOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return zipFile;
    }

    protected void unzip(String zipFileName) {
        FileInputStream inputStream = null;
        ZipInputStream zipInputStream = null;

        try {
            inputStream = mContext.openFileInput(zipFileName);
            zipInputStream = new ZipInputStream(inputStream);
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (!zipEntry.isDirectory()) {
                    unzip(zipInputStream, zipEntry.getName());
                }
            }
            zipInputStream.close();
            zipInputStream = null;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStreams(inputStream, zipInputStream);
        }
    }

    private File createBackupZipFile(File parentDir, String fileName) {
        File zipFile = new File(parentDir, fileName);

        if (!zipFile.exists()) {
            try {
                zipFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return zipFile;
    }

    private void checkDir(ZipOutputStream zipOutputStream, File dir, byte[] buffer) throws IOException {

        for (File file : dir.listFiles()) {

            if (file.isDirectory()) {
                checkDir(zipOutputStream, dir, buffer);
            } else if (file.getPath().endsWith(IMAGE_EXTENSION)) {
                compress(zipOutputStream, file, getFileName(file), buffer);
            }
        }
    }

    private void compress(ZipOutputStream zipOutputStream, File file, String fileName, byte[] buffer) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream, BUFFER_SIZE);

        ZipEntry entry = new ZipEntry(fileName);
        zipOutputStream.putNextEntry(entry);
        int count;
        while ((count = bufferedInputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
            zipOutputStream.write(buffer, 0, count);
        }
        bufferedInputStream.close();
    }

    private String getFileName(File file) {
        return file.getPath().substring(
                file.getPath().lastIndexOf(File.separator) + 1);
    }

    private void unzip(ZipInputStream zipInputStream, String fileName) throws IOException {
        OutputStream outputStream = null;
        try {
            outputStream = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);

            copyStream(zipInputStream, outputStream);
            zipInputStream.closeEntry();
            if (outputStream != null) {
                outputStream.close();
            }
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    private void copyStream(InputStream inputStream, OutputStream outputStream) {
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            int read;
            while ((read = inputStream.read(buffer, 0, BUFFER_SIZE)) > -1) {
                outputStream.write(buffer, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeStreams(FileInputStream fileInputStream,
                              ZipInputStream zipInputStream) {
        if (zipInputStream != null) {
            try {
                zipInputStream.closeEntry();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (fileInputStream != null) {
            try {
                fileInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}