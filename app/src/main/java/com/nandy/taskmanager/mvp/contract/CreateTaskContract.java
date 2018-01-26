package com.nandy.taskmanager.mvp.contract;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;

/**
 * Created by yana on 24.01.18.
 */

public interface CreateTaskContract {

    interface Presenter extends org.kaerdan.presenterretainer.Presenter<View> {

        void saveChanges();

        void onActivityResult(int requestCode, int resultCode, Intent data);

        void clearLocation();

        void setStartDate();

        void setStartTime();

        boolean onDurationSelected(int optionId);

        boolean onRepeatPeriodSelected(int optionId);

        void onTitleChanged(String title);

        void onDescriptionChanged(String description);

        void chooseTaskCover();
    }

    interface View extends org.kaerdan.presenterretainer.Presenter.View {

        void setTitleError(@StringRes int textResId);

        void setCommentError(@StringRes int textResId);

        void clearLocation();

        void displayStartDate(String date);

        void displayStartTime(String time);

        void setStartTimeVisible(boolean visible);

        void setDuration(@StringRes int textResId);

        void setRepeatPeriod(@StringRes int textResId);

        void displayAddress(String location);

        void displayImage(String imagePath);

        void setDescription(String description);

        void setTitle(String title);

        void startCropActivity(CropImage.ActivityBuilder activityBuilder);

        void showDatePickerDialog(DatePickerDialog.OnDateSetListener onDateSetListener, int year, int month, int day);

        void showTimePickerDialog(TimePickerDialog.OnTimeSetListener onTimeSetListener, int hour, int minute);

        void showMessage(@StringRes int textResId);

        void showMessage(String message);

        void finishWithResult(int resultCode, Intent data);

        void setProgressViewVisible(boolean visible);

        void launchActivityForResult(Intent intent, int requestCode);
    }
}
