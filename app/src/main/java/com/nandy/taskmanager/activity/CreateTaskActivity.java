package com.nandy.taskmanager.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nandy.taskmanager.Constants;
import com.nandy.taskmanager.R;
import com.nandy.taskmanager.SimpleTextChangedListener;
import com.nandy.taskmanager.image.ImageLoader;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.mvp.contract.CreateTaskContract;
import com.nandy.taskmanager.mvp.model.CreateTaskModel;
import com.nandy.taskmanager.mvp.model.DateFormatModel;
import com.nandy.taskmanager.mvp.model.GeocoderModel;
import com.nandy.taskmanager.mvp.model.TaskCoverModel;
import com.nandy.taskmanager.mvp.model.TaskRecordsModel;
import com.nandy.taskmanager.mvp.model.TaskRemindersModel;
import com.nandy.taskmanager.mvp.model.ValidationModel;
import com.nandy.taskmanager.mvp.presenter.CreateTaskPresenter;
import com.theartofdev.edmodo.cropper.CropImage;

import org.kaerdan.presenterretainer.PresenterActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Create/edit task screen
 */
public class CreateTaskActivity
        extends PresenterActivity<CreateTaskContract.Presenter, CreateTaskContract.View>
        implements CreateTaskContract.View {

    @BindView(R.id.input_title)
    EditText mInputTitle;
    @BindView(R.id.input_description)
    EditText mInputDescription;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.image_task)
    ImageView mTaskImageView;
    @BindView(R.id.txt_start_date)
    TextView mStartDateTextView;
    @BindView(R.id.txt_start_time)
    TextView mStartTimeTextView;
    @BindView(R.id.txt_duration)
    TextView mDurationTextView;
    @BindView(R.id.txt_repeat)
    TextView mRepeatPeriodTextView;
    @BindView(R.id.txt_location)
    TextView mLocationTextView;
    @BindView(R.id.layout_progress)
    View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.create_task);
        }

        registerForContextMenu(mDurationTextView);
        registerForContextMenu(mRepeatPeriodTextView);

       setupTextWatchers();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_task, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_save:
                getPresenter().saveChanges();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        switch (v.getId()) {

            case R.id.txt_duration:
                getMenuInflater().inflate(R.menu.menu_duration, menu);
                break;

            case R.id.txt_repeat:
                getMenuInflater().inflate(R.menu.menu_repeat_period, menu);
                break;
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getGroupId()) {

            case R.id.group_duration:
                return getPresenter().onDurationSelected(item.getItemId());

            case R.id.group_period:
                return getPresenter().onRepeatPeriodSelected(item.getItemId());

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getPresenter().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.REQUEST_CODE_PERMISSIONS && isPermissionsGranted()) {
            getPresenter().chooseTaskCover();
        }
    }

    @Override
    protected CreateTaskContract.Presenter onCreatePresenter() {

        Bundle args = getIntent().getExtras();
        Task task = null;
        int mode = Constants.MODE_CREATE;
        if (args != null) {
            task = args.getParcelable(Constants.PARAM_TASK);
            mode = args.getInt(Constants.PARAM_MODE, Constants.MODE_CREATE);
        }
        CreateTaskPresenter presenter = new CreateTaskPresenter();
        presenter.setCreateTaskMode(new CreateTaskModel(getApplicationContext(), task, mode));
        presenter.setValidationModel(new ValidationModel());
        presenter.setDateFormatModel(new DateFormatModel(getApplicationContext()));
        presenter.setCoverModel(new TaskCoverModel(getApplicationContext()));
        presenter.setRecordsModel(new TaskRecordsModel(getApplicationContext()));
        presenter.setScheduleModel(new TaskRemindersModel(getApplicationContext()));
        presenter.setGeocoderModel(new GeocoderModel(getApplicationContext()));

        return presenter;
    }

    @Override
    protected CreateTaskContract.View getPresenterView() {
        return this;
    }

    @OnClick(R.id.txt_location)
    void onSetLocationButtonClick() {
        startActivityForResult(new Intent(getApplicationContext(), MapActivity.class), Constants.REQUEST_CODE_LOCATION);
    }

    @OnClick(R.id.txt_start_date)
    void onSetStartDateButtonClick() {
        getPresenter().setStartDate();
    }

    @OnClick(R.id.txt_start_time)
    void onSetStartTimeButtonClick() {
        getPresenter().setStartTime();
    }

    @OnClick(R.id.txt_duration)
    void onSetDurationButtonClick(View view) {
        openContextMenu(view);
    }

    @OnClick(R.id.txt_repeat)
    void onSetRepeatPeriodClick(View view) {
        openContextMenu(view);
    }

    @OnClick(R.id.btn_microphone)
    void onVoiceInputButtonClick() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.description));
        startActivityForResult(intent, Constants.REQUEST_CODE_VOICE_INPUT);
    }

    @OnClick(R.id.btn_clear_location)
    void onClearLocationButtonClick() {
        getPresenter().clearLocation();
    }


    @OnClick(R.id.image_task)
    void onTaskImageClick() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1 && !isPermissionsGranted()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    Constants.REQUEST_CODE_PERMISSIONS);

            return;
        }

        getPresenter().chooseTaskCover();
    }

    @Override
    public void setTitleError(int textResId) {
        mInputTitle.setError(getString(textResId));
    }

    @Override
    public void setCommentError(int textResId) {
        mInputDescription.setError(getString(textResId));
    }

    @Override
    public void clearLocation() {
        mLocationTextView.setText(R.string.location);
    }

    @Override
    public void displayStartDate(String date) {
        mStartDateTextView.setText(date);
    }

    @Override
    public void displayStartTime(String time) {
        mStartTimeTextView.setText(time);
    }

    @Override
    public void setStartTimeVisible(boolean visible) {
        mStartTimeTextView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setDuration(@StringRes int textResId) {
        mDurationTextView.setText(textResId);
    }

    @Override
    public void setRepeatPeriod(@StringRes int textResId) {
        mRepeatPeriodTextView.setText(textResId);
    }

    @Override
    public void displayAddress(String location) {
        mLocationTextView.setText(location);
    }

    @Override
    public void setTitle(String title) {
        mInputTitle.setText(title);

        if (!TextUtils.isEmpty(title)) {
            mInputTitle.setSelection(title.length());
        }
    }

    @Override
    public void displayImage(String imagePath) {
        ImageLoader.load(getApplicationContext(), imagePath)
                .into(mTaskImageView);
    }

    @Override
    public void startCropActivity(CropImage.ActivityBuilder activityBuilder) {
        activityBuilder.start(this);
    }

    @Override
    public void setDescription(String description) {
        mInputDescription.setText(description);
        if (!TextUtils.isEmpty(description)) {
            mInputDescription.setSelection(description.length());
        }
    }

    @Override
    public void showDatePickerDialog(DatePickerDialog.OnDateSetListener onDateSetListener, int year, int month, int day) {
        new DatePickerDialog(this, onDateSetListener, year, month, day).show();
    }

    @Override
    public void showTimePickerDialog(TimePickerDialog.OnTimeSetListener onTimeSetListener, int hour, int minute) {
        new TimePickerDialog(this, onTimeSetListener, hour, minute, true).show();
    }

    @Override
    public void showMessage(int textResId) {
        showMessage(getString(textResId));
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void finishWithResult(int resultCode, Intent data) {
        setResult(resultCode, data);
        finish();
    }

    @Override
    public void setProgressViewVisible(boolean visible) {
        mProgressView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void launchActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    private boolean isPermissionsGranted() {
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1 || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

    }


    private void setupTextWatchers(){
        mInputTitle.addTextChangedListener(new SimpleTextChangedListener() {
            @Override
            public void onTextChanged(String text) {
                getPresenter().onTitleChanged(text);
            }
        });

        mInputDescription.addTextChangedListener(new SimpleTextChangedListener() {
            @Override
            public void onTextChanged(String text) {
                getPresenter().onDescriptionChanged(text);
            }
        });
    }


}
