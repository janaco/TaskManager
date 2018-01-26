package com.nandy.taskmanager.mvp.presenter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;

import com.google.android.gms.maps.model.LatLng;
import com.nandy.taskmanager.Constants;
import com.nandy.taskmanager.R;
import com.nandy.taskmanager.SubscriptionUtils;
import com.nandy.taskmanager.enums.Duration;
import com.nandy.taskmanager.enums.RepeatPeriod;
import com.nandy.taskmanager.eventbus.TaskChangedEvent;
import com.nandy.taskmanager.model.Location;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.mvp.contract.CreateTaskContract;
import com.nandy.taskmanager.mvp.model.CreateTaskModel;
import com.nandy.taskmanager.mvp.model.DateFormatModel;
import com.nandy.taskmanager.mvp.model.GeocoderModel;
import com.nandy.taskmanager.mvp.model.TaskCoverModel;
import com.nandy.taskmanager.mvp.model.TaskRecordsModel;
import com.nandy.taskmanager.mvp.model.TaskRemindersModel;
import com.nandy.taskmanager.mvp.model.ValidationModel;
import com.theartofdev.edmodo.cropper.CropImage;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

/**
 * Created by yana on 16.01.18.
 */

public class CreateTaskPresenter implements CreateTaskContract.Presenter {

    private CreateTaskContract.View mView;

    private CreateTaskModel mCreateTaskModel;
    private DateFormatModel mDateFormatModel;
    private TaskCoverModel mCoverModel;
    private TaskRecordsModel mRecordsModel;
    private TaskRemindersModel mScheduleModel;
    private ValidationModel mValidationModel;
    private GeocoderModel mGeocoderModel;

    private Disposable mAddressSubscription;
    private Disposable mCreateTaskSubscription;

    @Override
    public void onAttachView(CreateTaskContract.View view) {
        mView = view;
        displayData();
    }

    @Override
    public void onDetachView() {
    }

    @Override
    public void onDestroy() {
        mView = null;
        SubscriptionUtils.dispose(mAddressSubscription);
        SubscriptionUtils.dispose(mCreateTaskSubscription);
    }

    @Override
    public void chooseTaskCover() {
        mView.launchActivityForResult(
                mCoverModel.createAndGetChooseCoverIntent(), Constants.REQUEST_CODE_CHOOSE_IMAGE);
    }

    @Override
    public void saveChanges() {

        Task task = mCreateTaskModel.getTask();

        if (!isInputValid(task.getTitle(), task.getDescription())) {
            return;
        }

        if (task.getPlannedStartDate() == null) {
            mView.showMessage(R.string.no_planned_start_date);
            return;
        }

        createOrUpdate(task, mCreateTaskModel.getMode() == Constants.MODE_CREATE);
    }

    @Override
    public void onTitleChanged(String title) {
        mCreateTaskModel.setTitle(title);
    }

    @Override
    public void onDescriptionChanged(String description) {
        mCreateTaskModel.setDescription(description);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case Constants.REQUEST_CODE_LOCATION:
                if (resultCode == RESULT_OK) {
                    LatLng latLng = data.getParcelableExtra(Constants.PARAM_LOCATION);
                    onLocationSpecified(latLng);

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

                    if (fileUri != null) {
                        mView.startCropActivity(CropImage.activity(fileUri));
                    } else {
                        mView.showMessage(R.string.image_loading_error);
                    }

                }
                break;

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                try {
                    File imageFile = mCoverModel.getCroppedImage(data, resultCode);
                    mCreateTaskModel.setImage(imageFile.getPath());
                    mView.displayImage(imageFile.getPath());
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showMessage(e.getMessage());
                }

                break;
        }

    }

    @Override
    public void clearLocation() {
        mCreateTaskModel.clearLocation();
        mView.clearLocation();
    }

    @Override
    public void setStartDate() {

        Calendar calendar = Calendar.getInstance();

        mView.showDatePickerDialog(
                (datePicker, year, month, day) ->
                {
                    mCreateTaskModel.setStartDate(year, month, day);
                    mView.setStartTimeVisible(true);
                    mView.displayStartDate(
                            mDateFormatModel.formatDate(mCreateTaskModel.getTask().getPlannedStartDate()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void setStartTime() {
        Calendar calendar = Calendar.getInstance();

        mView.showTimePickerDialog(
                (timePicker, hour, minute) ->
                {
                    mCreateTaskModel.setStartTime(hour, minute);
                    mView.displayStartTime(
                            mDateFormatModel.formatTime(mCreateTaskModel.getTask().getPlannedStartDate()));
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE));
    }

    @Override
    public boolean onDurationSelected(int optionId) {

        switch (optionId) {
            case R.id.option_one_minute:
                setDuration(Duration.ONE_MINUTE);
                return true;

            case R.id.option_five_mins:
                setDuration(Duration.FIVE_MINUTES);
                return true;

            case R.id.option_fifteen_mins:
                setDuration(Duration.FIFTEEN_MINUTES);
                return true;

            case R.id.option_half_of_hour:
                setDuration(Duration.HALF_OF_HOUR);
                return true;

            case R.id.option_one_hour:
                setDuration(Duration.ONE_HOUR);
                return true;

            case R.id.option_two_hours:
                setDuration(Duration.TWO_HOURS);
                return true;

            default:
                return false;
        }
    }

    @Override
    public boolean onRepeatPeriodSelected(int optionId) {

        switch (optionId) {
            case R.id.option_no_repeat:
                setRepeatPeriod(RepeatPeriod.NO_REPEAT);
                return true;

            case R.id.option_five_mins:
                setRepeatPeriod(RepeatPeriod.EVERY_FIVE_MINUTES);
                return true;

            case R.id.option_fifteen_mins:
                setRepeatPeriod(RepeatPeriod.EVERY_FIFTEEN_MINUTES);
                return true;

            case R.id.option_every_hour:
                setRepeatPeriod(RepeatPeriod.EVERY_HOUR);
                return true;

            case R.id.option_every_day:
                setRepeatPeriod(RepeatPeriod.EVERY_DAY);
                return true;

            default:
                return false;
        }
    }

    private void displayData() {

        Task task = mCreateTaskModel.getTask();
        mView.setTitle(task.getTitle());
        mView.setDescription(task.getDescription());

        if (task.getPlannedStartDate() != null) {
            displayPlannedStartTime(task.getPlannedStartDate());
        }

        if (task.getScheduledDuration()  != null) {
            mView.setDuration(task.getScheduledDuration().getTextResId());
        }

        if (task.getRepeatPeriod() != null) {
            mView.setRepeatPeriod(task.getRepeatPeriod().getTextResId());
        }

        if (task.hasMetadata() && task.getMetadata().hasLocation()) {
            mView.displayAddress(task.getMetadata().getLocation().getAddress());
        }

        if (task.hasImage()) {
            mView.displayImage(task.getImage());
        }
    }

    private void displayPlannedStartTime(Date plannedStartTime) {
        mView.displayStartDate(mDateFormatModel.formatDate(plannedStartTime));
        mView.displayStartTime(mDateFormatModel.formatTime(plannedStartTime));
        mView.setStartTimeVisible(true);
    }

    private String reduceImageSize(long taskId, String imagePath) {
        try {
            return mCoverModel.saveImage(taskId, imagePath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void createOrUpdate(Task task, boolean shouldCreateNew) {

        mCreateTaskSubscription = Completable.create(e -> {
            if (task.getImage() != null) {
                String reducedImagePath = reduceImageSize(task.getId(), task.getImage());
                task.setImage(reducedImagePath);
            }

            if (shouldCreateNew) {
                mRecordsModel.insert(task);
            } else {
                mRecordsModel.update(task);
            }

            e.onComplete();
        })
                .doOnComplete(() -> {
                    mScheduleModel.cancelReminders(task.getId());
                    mScheduleModel.scheduleStartReminder(task);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> mView.setProgressViewVisible(true))
                .doFinally(() -> mView.setProgressViewVisible(false))
                .subscribe(() -> {

                    if (task.hasLocation()) {
                        EventBus.getDefault().post(new TaskChangedEvent(task));
                    }

                    Intent intent = new Intent();
                    Bundle args = new Bundle();
                    args.putParcelable(Constants.PARAM_TASK, task);
                    intent.putExtras(args);
                    mView.finishWithResult(RESULT_OK, intent);
                }, Throwable::printStackTrace);
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

    private void onLocationSpecified(LatLng latLng) {

        mAddressSubscription = Single.create((SingleOnSubscribe<Location>) e -> {
            String addressText = mGeocoderModel.getAddressFromLocation(latLng);
            e.onSuccess(new Location(latLng, addressText));
        })
                .doOnSuccess(location -> mCreateTaskModel.setLocation(location))
                .doOnError(throwable -> mCreateTaskModel.setLocation(new Location(latLng, null)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(location -> mView.displayAddress(location.getAddress()), throwable -> mView.displayAddress(String.format(Locale.getDefault(), "%f, %f", latLng.latitude, latLng.longitude)));

    }

    private void setDuration(Duration duration) {
        mCreateTaskModel.setDuration(duration);
        mView.setDuration(duration.getTextResId());

    }

    private void setRepeatPeriod(RepeatPeriod repeatPeriod) {
        mCreateTaskModel.setRepeatPeriod(repeatPeriod);
        mView.setRepeatPeriod(repeatPeriod.getTextResId());
    }

    public void setDateFormatModel(DateFormatModel dateFormatModel) {
        mDateFormatModel = dateFormatModel;
    }

    public void setCreateTaskMode(CreateTaskModel createTaskMode) {
        mCreateTaskModel = createTaskMode;
    }

    public void setValidationModel(ValidationModel validationModel) {
        mValidationModel = validationModel;
    }

    public void setGeocoderModel(GeocoderModel geocoderModel) {
        mGeocoderModel = geocoderModel;
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
