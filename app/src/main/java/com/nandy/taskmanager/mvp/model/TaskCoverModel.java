package com.nandy.taskmanager.mvp.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * Created by razomer on 23.01.18.
 */

public class TaskCoverModel {

    private static final int DEFAULT_COVER_SIZE_PX = 50;

    private static final String COVER_PREFIX = "tcover_";
    private static final String COVER_EXTENSION = ".png";
    private static final String DEFAULT_CAMERA_OUTPUT_NAME = "temp_cover.jpg";
    private static final String DEFAULT_TEMP_COVER_NAME = "ctemp";

    private final Context mContext;

    public TaskCoverModel(Context context) {
        mContext = context;
    }

    public Uri getCoverFromFiles(Uri imageUri) throws IOException {
        final Bitmap bitmap = decodeUri(imageUri);

        File tempFile = File.createTempFile(DEFAULT_TEMP_COVER_NAME, COVER_EXTENSION);
        saveToFile(bitmap, tempFile);
        return Uri.fromFile(tempFile);

    }


    public Uri getOutputFromCamera() {

        return Uri.fromFile(new File(mContext.getFilesDir(), DEFAULT_CAMERA_OUTPUT_NAME));
    }


    public File getCroppedImage(Intent data, int resultCode) throws Exception {

        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (resultCode == RESULT_OK) {
            removeCameraOutputFile();

            return new File(result.getUri().getPath());

        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            throw new Exception(result.getError());
        }

        return null;
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void removeCameraOutputFile() {
        File file = new File(mContext.getFilesDir(), DEFAULT_CAMERA_OUTPUT_NAME);
        if (file.exists()) {
            file.delete();
        }

    }

    @Nullable
    private Bitmap decodeUri(Uri imageUri) {
        try {
            return BitmapFactory.decodeStream(
                    mContext.getContentResolver().openInputStream(imageUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }


    public String saveImage(long taskId, String pathToImage) throws IOException {
        Bitmap bitmap = resize(BitmapFactory.decodeFile(pathToImage), DEFAULT_COVER_SIZE_PX);
        File outputFile = new File(mContext.getFilesDir(), generateFileName(taskId));

        saveToFile(bitmap, outputFile);

        return outputFile.getPath();
    }

    private String generateFileName(long taskId) {

        return COVER_PREFIX.concat(String.valueOf(taskId)).concat(COVER_EXTENSION);
    }

    private void saveToFile(Bitmap bitmap, File file) throws IOException {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private Bitmap resize(Bitmap bitmap, int sizeInPizels) {

        int maxSize = (int) (sizeInPizels * mContext.getResources().getDisplayMetrics().scaledDensity);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }
}
