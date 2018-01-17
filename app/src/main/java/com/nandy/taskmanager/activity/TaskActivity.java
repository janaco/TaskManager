package com.nandy.taskmanager.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.nandy.taskmanager.R;
import com.nandy.taskmanager.mvp.model.CreateTaskModel;
import com.nandy.taskmanager.mvp.model.DateFormatModel;
import com.nandy.taskmanager.mvp.model.ValidationModel;
import com.nandy.taskmanager.mvp.presenter.CreateTaskPresenter;
import com.nandy.taskmanager.mvp.view.CreateTaskView;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TaskActivity extends AppCompatActivity implements CreateTaskView {

    private final static int REQUEST_CODE_LOCATION = 52;

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
        setContentView(R.layout.activity_task);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.create_task);
        }

        mPresener = new CreateTaskPresenter(this);
        mPresener.setCreateTaskMode(new CreateTaskModel(getApplicationContext()));
        mPresener.setValidationModel(new ValidationModel());
        mPresener.setDateFormatModel(new DateFormatModel());
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
        mPresener.enableVoiceInput();
    }

    @OnClick(R.id.btn_clear_start_date)
    void onClearStartDateBtnClick() {
        mPresener.clearStartDate();
    }


    @OnClick(R.id.btn_clear_end_date)
    void onClearEndDateButtonClick() {
        mPresener.clearEndDate();
    }


    @OnClick(R.id.btn_clear_start_date)
    void onClearLocationButtonClick() {
        mPresener.clearLocation();
    }

    @OnClick(R.id.image_task)
    void onTaskImageClick() {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task, menu);
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


    private void onSaveBtnClick() {

        String title = mInputTitle.getText().toString();
        String comment = mInputDescription.getText().toString();

        mPresener.createTask(title, comment);
        finish();

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
        new TimePickerDialog(this,onTimeSetListener, hour, minute, true).show();
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
}
