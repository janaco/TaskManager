package com.nandy.taskmanager.mvp.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.StringRes;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;

/**
 * Created by yana on 16.01.18.
 */

public interface CreateTaskView {

    void setTitleError(@StringRes int textResId);

    void setCommentError(@StringRes int textResId);

    void setResult(int resultCode, Intent intent);

    void showDatePickerDialog(DatePickerDialog.OnDateSetListener onDateSetListener, int year, int month, int day);

    void showTimePickerDialog(TimePickerDialog.OnTimeSetListener onTimeSetListener, int hour, int minute);

    void clearStartDateTime();

    void clearEndDateAndTime();

    void clearLocation();

    void displayStartDate(String date);

    void displayEndDate(String date);

    void displayLocation(String location);

    void displayImage(String imagePath);

    void setDescription(String description);

    void setTitle(String title);

    void startCropActivity(CropImage.ActivityBuilder activityBuilder);
}
