package com.nandy.taskmanager.mvp.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;

import com.nandy.taskmanager.R;
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
    private static final String CAMERA_OUTPUT_EXTENSION = ".jpg";
    private static final String DEFAULT_TEMP_COVER_NAME = "ctemp";

    private final Context mContext;
    private File mCameraOutputFile;

    public TaskCoverModel(Context context) {
        mContext = context;
    }

    public Uri getCoverFromFiles(Uri imageUri) throws IOException {
        final Bitmap bitmap = decodeUri(imageUri);

        File tempFile = File.createTempFile(DEFAULT_TEMP_COVER_NAME, COVER_EXTENSION);
        saveToFile(bitmap, tempFile);
        return Uri.fromFile(tempFile);

    }


    @Nullable
    public Uri getOutputFromCamera() {

        if (mCameraOutputFile != null){
            return Uri.fromFile(mCameraOutputFile);
        }

        return null;
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    public File getCroppedImage(Intent data, int resultCode) throws Exception {

        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (resultCode == RESULT_OK) {
            if (mCameraOutputFile != null){
                mCameraOutputFile.delete();
            }

            return new File(result.getUri().getPath());

        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            throw new Exception(result.getError());
        }

        return null;
    }


    private File createCameraOutputFile() {
        String imageFileName = "JPEG_" + System.currentTimeMillis();
        File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File cameraOutputFile = null;
        try {
            cameraOutputFile = File.createTempFile(imageFileName, CAMERA_OUTPUT_EXTENSION, storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cameraOutputFile;
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


    public Intent createAndGetChooseCoverIntent() {
        Intent pickIntent = new Intent();
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePhotoIntent.resolveActivity(mContext.getPackageManager()) != null) {
            mCameraOutputFile = createCameraOutputFile();
            if (mCameraOutputFile != null) {
                Uri photoURI = FileProvider
                        .getUriForFile(mContext, "com.nandy.taskmanager.fileprovider", mCameraOutputFile);
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            }
        }

        Intent chooserIntent = Intent.createChooser(pickIntent, mContext.getString(R.string.take_or_select_photo));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});

        return chooserIntent;
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
