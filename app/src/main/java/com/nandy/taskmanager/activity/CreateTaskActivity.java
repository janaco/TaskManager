package com.nandy.taskmanager.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nandy.taskmanager.ImageLoader;
import com.nandy.taskmanager.R;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.mvp.model.CreateTaskModel;
import com.nandy.taskmanager.mvp.model.CropImageModel;
import com.nandy.taskmanager.mvp.model.DateFormatModel;
import com.nandy.taskmanager.mvp.model.TaskRecordsModel;
import com.nandy.taskmanager.mvp.model.ValidationModel;
import com.nandy.taskmanager.mvp.presenter.CreateTaskPresenter;
import com.nandy.taskmanager.mvp.view.CreateTaskView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateTaskActivity extends AppCompatActivity implements CreateTaskView {

    public final static int REQUEST_CODE_LOCATION = 52;
    public final static int REQUEST_PERMISSIONS_CODE = 53;
    public static final int REQUEST_CODE_CHOOSE_IMAGE = 54;
    public static final int REQUEST_CODE_VIOCE_INPUT = 55;

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
    @BindView(R.id.txt_end_date)
    TextView mEndDateTextView;
    @BindView(R.id.txt_location)
    TextView mLocationTextView;

    private CreateTaskPresenter mPresener;

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

        Task task = getIntent().getParcelableExtra("task");
        int mode = getIntent().getIntExtra("mode", CreateTaskModel.MODE_CREATE);


        mPresener = new CreateTaskPresenter(this);
        mPresener.setCreateTaskMode(new CreateTaskModel(task, mode));
        mPresener.setValidationModel(new ValidationModel());
        mPresener.setDateFormatModel(new DateFormatModel());
        mPresener.setCropImageModel(new CropImageModel(getApplicationContext()));
        mPresener.setRecordsModel(new TaskRecordsModel(getApplicationContext()));

        mPresener.start();
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
                onSaveBtnClick();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresener.onActivityResult(requestCode, resultCode, data);
    }


    @OnClick(R.id.txt_location)
    void onSetLocationButtonClick() {
        startActivityForResult(new Intent(getApplicationContext(), MapActivity.class), REQUEST_CODE_LOCATION);
    }

    @OnClick(R.id.txt_start_date)
    void onSetStartDateButtonClick() {
        mPresener.setStartDate();
    }

    @OnClick(R.id.txt_end_date)
    void onSetEndDateButtonClick() {
        mPresener.setEndDate();
    }

    @OnClick(R.id.btn_microphone)
    void onVoiceInputButtonClick() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.description));
        startActivityForResult(intent, REQUEST_CODE_VIOCE_INPUT);
    }


    @OnClick(R.id.btn_clear_start_date)
    void onClearStartDateBtnClick() {
        mPresener.clearStartDate();
    }


    @OnClick(R.id.btn_clear_end_date)
    void onClearEndDateButtonClick() {
        mPresener.clearEndDate();
    }


    @OnClick(R.id.btn_clear_location)
    void onClearLocationButtonClick() {
        mPresener.clearLocation();
    }


    @OnClick(R.id.image_task)
    void onTaskImageClick() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1 && !isPermissionsGranted()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    REQUEST_PERMISSIONS_CODE);

            return;
        }

        showPickImagePopup();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @android.support.annotation.NonNull String[] permissions, @android.support.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS_CODE && isPermissionsGranted()) {
            showPickImagePopup();
        }
    }


    private boolean isPermissionsGranted() {
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1 || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

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
    public void showDatePickerDialog(DatePickerDialog.OnDateSetListener onDateSetListener, int year, int month, int day) {
        new DatePickerDialog(this, onDateSetListener, year, month, day).show();
    }

    @Override
    public void showTimePickerDialog(TimePickerDialog.OnTimeSetListener onTimeSetListener, int hour, int minute) {
        new TimePickerDialog(this, onTimeSetListener, hour, minute, true).show();
    }


    @Override
    public void clearEndDateAndTime() {
        mEndDateTextView.setText(R.string.set_end_time);
    }

    @Override
    public void clearStartDateTime() {
        mStartDateTextView.setText(R.string.set_start_time);
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
    public void displayEndDate(String date) {
        mEndDateTextView.setText(date);
    }

    @Override
    public void displayLocation(String location) {
        mLocationTextView.setText(location);
    }

    @Override
    public void setTitle(String title) {
        mInputTitle.setText(title);
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
    }

    private void showPickImagePopup() {
        Intent pickIntent = new Intent();
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri outputUri = Uri.fromFile(new File(getFilesDir(), "temp_cover.jpg"));
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);

        Intent chooserIntent = Intent.createChooser(pickIntent, getString(R.string.take_or_select_photo));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});
        startActivityForResult(chooserIntent, REQUEST_CODE_CHOOSE_IMAGE);
    }

    private void onSaveBtnClick() {

        String title = mInputTitle.getText().toString().trim();
        String comment = mInputDescription.getText().toString().trim();

        boolean success = mPresener.createTask(title, comment);
        if (success) {
            finish();
        }

    }


}
