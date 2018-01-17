package com.nandy.taskmanager.mvp.model;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;

import static android.app.Activity.RESULT_OK;

/**
 * Created by yana on 17.01.18.
 */

public class CropImageModel {

    private Context mContext;

    public CropImageModel(Context context) {
        mContext = context;
    }

    public CropImage.ActivityBuilder buildImageCropper(Intent data) {

        Uri fileUri;

        if (data == null || data.getData() == null) {
            fileUri = getOutputFromCamera();
        } else if (data.getData().getScheme().equals("file")) {
            fileUri = data.getData();
        } else {
            fileUri = getImageFromGallery(data.getData());
        }

        return CropImage.activity(fileUri);
    }

    public File getCroppedImage(Intent data, int resultCode) throws Exception{

        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (resultCode == RESULT_OK) {
            Uri resultUri = result.getUri();
            removeTempFile();
            return new File(resultUri.getPath());
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    throw new Exception( result.getError());
        }

        return null;
    }

    private void removeTempFile(){
        File file = new File(mContext.getFilesDir(), "temp_cover.jpg");
        if (file.exists()) {
            file.delete();
        }

    }

    private Uri getOutputFromCamera() {

        return Uri.fromFile(new File(mContext.getFilesDir(), "temp_cover.jpg"));
    }


    private Uri getImageFromGallery(Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        String pathToImage = "";
        Cursor cursor = mContext.getContentResolver().query(uri,
                filePathColumn, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            pathToImage = cursor.getString(columnIndex);
            cursor.close();
        }
        String path = Uri.parse(pathToImage).getPath();
        return Uri.fromFile(new File(path));
    }

}
