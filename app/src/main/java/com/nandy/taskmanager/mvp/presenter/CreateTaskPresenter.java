package com.nandy.taskmanager.mvp.presenter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;

import com.google.android.gms.maps.model.LatLng;
import com.nandy.taskmanager.Constants;
import com.nandy.taskmanager.R;
import com.nandy.taskmanager.enums.RepeatPeriod;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.mvp.contract.CreateTaskContract;
import com.nandy.taskmanager.mvp.model.CreateTaskModel;
import com.nandy.taskmanager.mvp.model.DateFormatModel;
import com.nandy.taskmanager.mvp.model.TaskCoverModel;
import com.nandy.taskmanager.mvp.model.TaskRecordsModel;
import com.nandy.taskmanager.mvp.model.TaskRemindersModel;
import com.nandy.taskmanager.mvp.model.ValidationModel;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_OK;

/**
 * Created by yana on 16.01.18.
 */

public class CreateTaskPresenter implements CreateTaskContract.Presenter {

    private CreateTaskContract.View mView;

    private CreateTaskModel mCreateTaskMode;
    private DateFormatModel mDateFormatModel;
    private TaskCoverModel mCoverModel;
    private TaskRecordsModel mRecordsModel;
    private TaskRemindersModel mScheduleModel;
    private ValidationModel mValidationModel;

    private Bundle mSavedInstanceState;

    @Override
    public void onAttachView(CreateTaskContract.View view) {
        mView = view;
        displayData();

        if (mSavedInstanceState != null) {
            restoreViewState();
        }else {
            setDuration(15, TimeUnit.MINUTES);
            setRepeatPeriod(RepeatPeriod.NO_REPEAT);
        }
    }

    private void restoreViewState() {

        mView.setTitle(mSavedInstanceState.getString(Constants.PARAM_TITLE));
        mView.setDescription(mSavedInstanceState.getString(Constants.PARAM_DESCRIPTION));
    }

    @Override
    public void onDetachView() {
    }

    @Override
    public void onDestroy() {
        mView = null;
    }

    private void displayData() {

        Task task = mCreateTaskMode.getTask();
        mView.setTitle(task.getTitle());
        mView.setDescription(task.getDescription());

        if (task.getPlannedStartDate() != null) {
            displayPlannedStartTime(task.getPlannedStartDate());
        }

        if (task.getScheduledDuration() > 0) {
            displayScheduledDuration(task.getScheduledDuration());
        }

        if (task.getRepeatPeriod() != null) {
            mView.setRepeatPeriod(task.getRepeatPeriod().getTextResId());
        }

        if (task.hasLocation()) {
            mView.displayLocation(String.format(Locale.getDefault(), "%f, %f",
                    task.getLocation().latitude,
                    task.getLocation().longitude));
        }

        if (task.hasImage()) {
            mView.displayImage(task.getImage());
        }
    }

    private void displayScheduledDuration(long scheduledDuration) {
        int duration = mDateFormatModel.convertToMinutes(scheduledDuration);
        int textResId = R.string.minutes;
        if (duration > 30) {
            duration = mDateFormatModel.convertToSeconds(scheduledDuration);
            textResId = R.string.hour;
        }
        mView.setDuration(duration, textResId);
    }

    private void displayPlannedStartTime(Date plannedStartTime) {
        mView.displayStartDate(mDateFormatModel.formatDate(plannedStartTime));
        mView.displayStartTime(mDateFormatModel.formatTime(plannedStartTime));
        mView.setStartTimeVisible(true);
    }

    public void saveChanges(String title, String description) {

        if (!isInputValid(title, description)) {
            return;
        }

        Task task = mCreateTaskMode.getTask();

        if (task.getPlannedStartDate() == null){
            mView.showMessage(R.string.no_planned_start_date);
            return;
        }
        task.setTitle(title);
        task.setDescription(description);

        if (task.getImage() != null) {
            try {
                String reducedImagePath = mCoverModel.saveImage(task.getId(), task.getImage());
                task.setImage(reducedImagePath);
            } catch (IOException e) {
                e.printStackTrace();
                mView.showMessage(R.string.image_saving_error);
            }
        }

        if (mCreateTaskMode.getMode() == Constants.MODE_CREATE) {
            mRecordsModel.insert(task);
        } else {
            mRecordsModel.update(task);

        }

        mScheduleModel.scheduleStartReminder(task);
        if (task.hasLocation()) {
            mScheduleModel.scheduleLocationUpdates();
        }

        Intent intent = new Intent();
        Bundle args = new Bundle();
        args.putParcelable("task", task);
        intent.putExtras(args);
        mView.finishWithResult(RESULT_OK, intent);
    }

    private boolean isInputValid(String title, String description) {

        boolean isInputValid = true;

        if (mValidationModel.isEmpty(title)) {
            mView.setTitleError(R.string.empty_field);
            isInputValid = false;
        }

        if (!mValidationModel.isTitleValid(title)) {
            mView.setTitleError(R.string.title_is_too_short);
            isInputValid = false;
        }

        if (mValidationModel.isEmpty(description)) {
            mView.setCommentError(R.string.empty_field);
            isInputValid = false;
        }

        return isInputValid;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case Constants.REQUEST_CODE_LOCATION:
                if (resultCode == RESULT_OK) {
                    LatLng latLng = data.getParcelableExtra("location");
                    mCreateTaskMode.setLocation(latLng);
                    mView.displayLocation(String.format(Locale.getDefault(), "%f, %f", latLng.latitude, latLng.longitude));

                }
                break;

            case Constants.REQUEST_CODE_VOICE_INPUT:
                if (resultCode == RESULT_OK) {
                    ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (matches.size() > 0) {
                        mView.setDescription(matches.get(0));
                    }
                }
                break;

            case Constants.REQUEST_CODE_CHOOSE_IMAGE:

                if (resultCode == RESULT_OK) {

                    Uri fileUri;

                    if (data == null || data.getData() == null) {
                        fileUri = mCoverModel.getOutputFromCamera();
                    } else if (data.getData().getScheme().equals("file")) {
                        fileUri = data.getData();
                    } else {
                        try {
                            fileUri = mCoverModel.getCoverFromFiles(data.getData());
                        } catch (IOException e) {
                            e.printStackTrace();
                            mView.showMessage(R.string.image_loading_error);
                            return;
                        }

                    }

                    mView.startCropActivity(CropImage.activity(fileUri));

                }
                break;

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                try {
                    File imageFile = mCoverModel.getCroppedImage(data, resultCode);
                    mCreateTaskMode.setImage(imageFile.getPath());
                    mView.displayImage(imageFile.getPath());
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showMessage(e.getMessage());
                }

                break;
        }

    }

    public void clearLocation() {
        mCreateTaskMode.clearLocation();
        mView.clearLocation();
    }

    public void setStartDate() {

        Calendar calendar = Calendar.getInstance();

        mView.showDatePickerDialog(
                (datePicker, year, month, day) ->
                {
                    mCreateTaskMode.setStartDate(year, month, day);
                    mView.setStartTimeVisible(true);
                    mView.displayStartDate(
                            mDateFormatModel.formatDate(mCreateTaskMode.getTask().getPlannedStartDate()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    public void setStartTime() {
        Calendar calendar = Calendar.getInstance();

        mView.showTimePickerDialog(
                (timePicker, hour, minute) ->
                {
                    mCreateTaskMode.setStartTime(hour, minute);
                    mView.displayStartTime(
                            mDateFormatModel.formatTime(mCreateTaskMode.getTask().getPlannedStartDate()));
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE));
    }

    public boolean onDurationSelected(int optionId) {

        switch (optionId) {
            case R.id.option_fifteen_minutes:
                setDuration(15, TimeUnit.MINUTES);
                return true;

            case R.id.option_half_of_hour:
                setDuration(30, TimeUnit.MINUTES);
                return true;

            case R.id.option_one_hour:
                setDuration(1, TimeUnit.HOURS);
                return true;

            case R.id.option_two_hours:
                setDuration(2, TimeUnit.HOURS);
                return true;

            case R.id.option_three_hours:
                setDuration(3, TimeUnit.HOURS);
                return true;

            case R.id.option_four_hours:
                setDuration(4, TimeUnit.HOURS);
                return true;

            case R.id.option_five_hours:
                setDuration(5, TimeUnit.HOURS);
                return true;

            case R.id.option_six_hours:
                setDuration(6, TimeUnit.HOURS);
                return true;

            default:
                return false;
        }
    }

    private void setDuration(int value, TimeUnit timeUnit) {
        mCreateTaskMode.setDuration(timeUnit.toMillis(value));
        mView.setDuration(value, timeUnit == TimeUnit.MINUTES ? R.string.minutes : R.string.hour);

    }

    @Override
    public void saveInstanceState(Bundle outState, String title, String description) {
        outState.putString(Constants.PARAM_TITLE, title);
        outState.putString(Constants.PARAM_DESCRIPTION, description);
    }

    @Override
    public void restoreInstanceState(Bundle savedInstanceState) {
        mSavedInstanceState = savedInstanceState;
    }

    public boolean onRepeatPeriodSelected(int optionId) {

        switch (optionId) {
            case R.id.option_no_repeat:
                setRepeatPeriod(RepeatPeriod.NO_REPEAT);
                return true;

            case R.id.option_once_a_hour:
                setRepeatPeriod(RepeatPeriod.ONCE_A_HOUR);
                return true;

            case R.id.option_once_a_day:
                setRepeatPeriod(RepeatPeriod.ONCE_A_DAY);
                return true;

            case R.id.option_once_a_week:
                setRepeatPeriod(RepeatPeriod.ONCE_A_WEEK);
                return true;

            case R.id.option_once_a_month:
                setRepeatPeriod(RepeatPeriod.ONCE_A_MONTH);
                return true;

            case R.id.option_once_a_year:
                setRepeatPeriod(RepeatPeriod.ONCE_A_YEAR);
                return true;

            default:
                return false;
        }
    }

    private void setRepeatPeriod(RepeatPeriod repeatPeriod) {
        mCreateTaskMode.setRepeatPeriod(repeatPeriod);
        mView.setRepeatPeriod(repeatPeriod.getTextResId());
    }

    public void setDateFormatModel(DateFormatModel dateFormatModel) {
        mDateFormatModel = dateFormatModel;
    }

    public void setCreateTaskMode(CreateTaskModel createTaskMode) {
        mCreateTaskMode = createTaskMode;
    }

    public void setValidationModel(ValidationModel validationModel) {
        mValidationModel = validationModel;
    }

    public void setCoverModel(TaskCoverModel coverModel) {
        mCoverModel = coverModel;
    }

    public void setRecordsModel(TaskRecordsModel recordsModel) {
        mRecordsModel = recordsModel;
    }

    public void setScheduleModel(TaskRemindersModel scheduleModel) {
        mScheduleModel = scheduleModel;
    }


}
