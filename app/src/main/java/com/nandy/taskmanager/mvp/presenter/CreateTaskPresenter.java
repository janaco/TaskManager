package com.nandy.taskmanager.mvp.presenter;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.maps.model.LatLng;
import com.nandy.taskmanager.R;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.mvp.model.CreateTaskModel;
import com.nandy.taskmanager.mvp.model.DateFormatModel;
import com.nandy.taskmanager.mvp.model.ValidationModel;
import com.nandy.taskmanager.mvp.view.CreateTaskView;

import java.util.Calendar;

/**
 * Created by yana on 16.01.18.
 */

public class CreateTaskPresenter {

    private CreateTaskView mView;
    private CreateTaskModel mCreateTaskMode;
    private ValidationModel mValidationModel;
    private DateFormatModel mDateFormatModel;

    public CreateTaskPresenter(CreateTaskView view) {
        mView = view;
    }

    public void createTask(String title, String desctiption) {

        if (mValidationModel.isEmpty(title)) {
            mView.setTitleError(R.string.empty_field);
            return;
        }

        if (!mValidationModel.isTitleValid(title)){
            mView.setTitleError(R.string.title_is_too_short);
            return;
        }

        if (mValidationModel.isEmpty(desctiption)) {
            mView.setCommentError(R.string.empty_field);
            return;
        }

        Task task = new Task(title, desctiption);
        mCreateTaskMode.save(task);

        Intent intent = new Intent();
        intent.putExtra("task", task);
        mView.setResult(Activity.RESULT_OK, intent);
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

    public void enableVoiceInput() {

    }

    public void onLocationSpecified(LatLng latLng) {
        mCreateTaskMode.setLocation(latLng);
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
