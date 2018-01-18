package com.nandy.taskmanager.mvp.presenter;

import android.content.Intent;
import android.speech.RecognizerIntent;

import com.google.android.gms.maps.model.LatLng;
import com.nandy.taskmanager.R;
import com.nandy.taskmanager.activity.CreateTaskActivity;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.mvp.model.CreateTaskModel;
import com.nandy.taskmanager.mvp.model.CropImageModel;
import com.nandy.taskmanager.mvp.model.DateFormatModel;
import com.nandy.taskmanager.mvp.model.ValidationModel;
import com.nandy.taskmanager.mvp.view.CreateTaskView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * Created by yana on 16.01.18.
 */

public class CreateTaskPresenter {

    private CreateTaskView mView;
    private CreateTaskModel mCreateTaskMode;
    private ValidationModel mValidationModel;
    private DateFormatModel mDateFormatModel;
    private CropImageModel mCropImageModel;

    public CreateTaskPresenter(CreateTaskView view) {
        mView = view;
    }

    public boolean createTask(String title, String desctiption) {

        if (mValidationModel.isEmpty(title)) {
            mView.setTitleError(R.string.empty_field);
            return false;
        }

        if (!mValidationModel.isTitleValid(title)) {
            mView.setTitleError(R.string.title_is_too_short);
            return false;
        }

        if (mValidationModel.isEmpty(desctiption)) {
            mView.setCommentError(R.string.empty_field);
            return false;
        }

        Task task = mCreateTaskMode.create(title, desctiption);
        mCreateTaskMode.save(task);

        Intent intent = new Intent();
        intent.putExtra("task", task);
        mView.setResult(RESULT_OK, intent);

        return true;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        switch (requestCode) {

            case CreateTaskActivity.REQUEST_CODE_LOCATION:
                if (resultCode == RESULT_OK) {
                    LatLng latLng = data.getParcelableExtra("location");
                    onLocationSpecified(latLng);
                }
                break;

                case CreateTaskActivity.REQUEST_CODE_VIOCE_INPUT:
                    if (resultCode == RESULT_OK) {
                        ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        mView.setDescription(matches.toString());
                    }
                    break;

            case CreateTaskActivity.REQUEST_CODE_CHOOSE_IMAGE:

                if (resultCode == RESULT_OK) {
                   mView.startCropActivity(mCropImageModel.buildImageCropper(data));
                }
                break;

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                try {
                    File imageFile = mCropImageModel.getCroppedImage(data, resultCode);
                    mCreateTaskMode.setImage(imageFile.getPath());
                    mView.displayImage(imageFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    //TODO: show error
                }

                break;

        }

    }


    public void setDateFormatModel(DateFormatModel mDateFormatModel) {
        this.mDateFormatModel = mDateFormatModel;
    }

    public void setCreateTaskMode(CreateTaskModel mCreateTaskMode) {
        this.mCreateTaskMode = mCreateTaskMode;
    }

    public void setValidationModel(ValidationModel mValidationModel) {
        this.mValidationModel = mValidationModel;
    }

    public void setCropImageModel(CropImageModel mCropImageModel) {
        this.mCropImageModel = mCropImageModel;
    }

    public void onLocationSpecified(LatLng latLng) {
        mCreateTaskMode.setLocation(latLng);
        mView.displayLocation(String.format(Locale.getDefault(), "%f, %f", latLng.latitude, latLng.longitude));
    }

    public void clearLocation() {
        mCreateTaskMode.clearLocation();
        mView.clearLocation();
    }

    public void clearStartDate() {
        mCreateTaskMode.clearStartDate();
        mView.clearStartDateTime();
    }

    public void clearEndDate() {
        mCreateTaskMode.clearEndDate();
        mView.clearEndDateAndTime();
    }

    public void setStartDate() {

        Calendar calendar = Calendar.getInstance();

        mView.showDatePickerDialog(
                (datePicker, year, month, day) ->
                        mView.showTimePickerDialog(
                                (timePicker, hour, minute) ->
                                {
                                    mCreateTaskMode.setStartDateAndTime(year, month, day, hour, minute);
                                    mView.displayStartDate(
                                            mDateFormatModel.format(mCreateTaskMode.getStartDate()));
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE)),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    public void setEndDate() {
        Calendar calendar = Calendar.getInstance();

        mView.showDatePickerDialog(
                (datePicker, year, month, day) ->
                        mView.showTimePickerDialog(
                                (timePicker, hour, minute) ->
                                {
                                    mCreateTaskMode.setEndDateAndTime(year, month, day, hour, minute);
                                    mView.displayEndDate(
                                            mDateFormatModel.format(mCreateTaskMode.getEndDate()));
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE)),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }


}
