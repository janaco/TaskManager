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
    private static final String DIRECTORY_COVERS = "covers";
    private final Context mContext;

    public ZipModel(Context context) {
        mContext = context;
    }

    public File zip() {

        File zipFile = new File(mContext.getFilesDir(), BACKUP_FILE_NAME);
        try {
            zipFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte data[] = new byte[BUFFER_SIZE];

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
            ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(fileOutputStream));
            BufferedInputStream bufferedInputStream;

            for (File file : getFilesToZip()) {

                if (file.isDirectory()) {

                    for (File f : file.listFiles()) {

                        if (f.getPath().endsWith(".png")) {

                            FileInputStream fileInputStream = new FileInputStream(f);
                            bufferedInputStream = new BufferedInputStream(fileInputStream, BUFFER_SIZE);

                            String fileName = f.getPath().substring(
                                    f.getPath().lastIndexOf(File.separator) + 1);
                            Log.d("ZIP_MODEL", "zip.entry: " + fileName);

                            ZipEntry entry = new ZipEntry(fileName);
                            zipOutputStream.putNextEntry(entry);
                            int count;
                            while ((count = bufferedInputStream.read(data, 0, BUFFER_SIZE)) != -1) {
                                zipOutputStream.write(data, 0, count);
                            }
                            bufferedInputStream.close();
                        }
                    }
                } else if (file.getPath().contains("tasks")){


                    FileInputStream fileInputStream = new FileInputStream(file);
                    bufferedInputStream = new BufferedInputStream(fileInputStream, BUFFER_SIZE);
                    String fileName = file.getPath().substring(
                            file.getPath().lastIndexOf(File.separator) + 1) + ".db";
                    Log.d("ZIP_MODEL", "zip.entry: " + fileName);

                    ZipEntry entry = new ZipEntry(fileName);
                    zipOutputStream.putNextEntry(entry);
                    int count;
                    while ((count = bufferedInputStream.read(data, 0, BUFFER_SIZE)) != -1) {
                        zipOutputStream.write(data, 0, count);
                    }
                    bufferedInputStream.close();
                }

            }
            zipOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return zipFile;
    }

    private File[] getFilesToZip() {

        return new File[]{
                mContext.getFilesDir(),
                mContext.getDatabasePath(AppDatabase.DB_NAME)};
    }


    public void unzip() {
        FileInputStream inputStream = null;
        ZipInputStream zipInputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = mContext.openFileInput(BACKUP_FILE_NAME);
            zipInputStream = new ZipInputStream(inputStream);
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {

                if (zipEntry.getName().contains(".db")) {
                    if (!zipEntry.isDirectory()) {

                        new File(mContext.getFilesDir(), BACKUP_DB_FILE);

                        try {
                            outputStream = mContext.openFileOutput(BACKUP_DB_FILE, Context.MODE_PRIVATE);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        copyStream(zipInputStream, outputStream);
                        zipInputStream.closeEntry();
                        if (outputStream != null) {
                            outputStream.close();
                        }
                        outputStream = null;


                    }
                } else {

                    unzipMarkers(zipEntry, zipInputStream);
                }
            }
            zipInputStream.close();
            zipInputStream = null;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStreams(inputStream, zipInputStream, outputStream);
        }
    }

    private void unzipMarkers(
            ZipEntry zipEntry,
            ZipInputStream zipInputStream) throws IOException {
        OutputStream outputStream;


        if (!zipEntry.isDirectory()) {
            String name = zipEntry.getName();
            Log.d("ZIP_MODEL", "unzip.image: " + name);
                outputStream = mContext.openFileOutput(name, Context.MODE_PRIVATE);

            copyStream(zipInputStream, outputStream);
            zipInputStream.closeEntry();
            outputStream.close();
            outputStream = null;
        }
    }


    private static void copyStream(InputStream inputStream, OutputStream outputStream) {
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

    private static void closeStreams(FileInputStream fileInputStream,
                                     ZipInputStream zipInputStream,
                                     OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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