package com.nandy.taskmanager.mvp.model;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

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
 * Created by razomer on 23.01.18.
 */

public class ZipModel {
    private static final int BUFFER_SIZE = 2048;
    private static final String BACKUP_FILE_NAME = "backup.zip";
    private static final String BACKUP_DB_FILE = "backup.db";
    private static final String IMAGE_EXTENSION = ".png";
    private static final String DB_EXTENSION = ".db";
    private final Context mContext;

    public ZipModel(Context context) {
        mContext = context;
    }

    public File compress(File []files) {

        File zipFile = createBackupZipFile(mContext.getFilesDir(), BACKUP_FILE_NAME);
        byte buffer[] = new byte[BUFFER_SIZE];

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
            ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(fileOutputStream));

            for (File file : files) {

                if (file.isDirectory()) {
                    checkDir(zipOutputStream, file, buffer);
                } else if (file.getPath().endsWith(AppDatabase.DB_NAME)) {
                    String fileName = getFileName(file).concat(DB_EXTENSION);
                    compress(zipOutputStream, file, fileName, buffer);
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

    public void unzip() {
        FileInputStream inputStream = null;
        ZipInputStream zipInputStream = null;

        try {
            inputStream = mContext.openFileInput(BACKUP_FILE_NAME);
            zipInputStream = new ZipInputStream(inputStream);
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {

                if (zipEntry.getName().contains(DB_EXTENSION)) {
                    unzip(zipInputStream, BACKUP_DB_FILE);
                } else if (!zipEntry.isDirectory()) {
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