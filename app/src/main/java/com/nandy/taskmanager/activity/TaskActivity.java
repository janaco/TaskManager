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
    }

    @OnClick(R.id.txt_location)
    void onSetLocationButtonClick() {
        startActivityForResult(new Intent(getApplicationContext(), MapActivity.class), REQUEST_CODE_LOCATION);
    }

    @OnClick(R.id.txt_start_date)
    void onSetStartDateButtonClick() {
        showDatePickerDialog();

    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this
                , new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                showTimePickerDialog();
            }
        }, year, month, day);
        datePickerDialog.setTitle(R.string.set_start_date);
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {

            }
        }, hour, minute, true);
        timePickerDialog.setTitle(R.string.set_start_date);
        timePickerDialog.show();
    }

    @OnClick(R.id.txt_end_date)
    void onSetEndDateButtonClick() {

    }

    @OnClick(R.id.btn_microphone)
    void onVoiceInputButtonClick() {
        //TODO: voice input
    }

    @OnClick(R.id.btn_clear_start_date)
    void onClearStartDateBtnClick() {
        mStartDateTextView.setText(R.string.set_start_time);
    }


    @OnClick(R.id.btn_clear_end_date)
    void onClearEndDateButtonClick() {
        mEndDateTextView.setText(R.string.set_end_time);
    }


    @OnClick(R.id.btn_clear_start_date)
    void onClearLocationButtonClick() {
        mLocationTextView.setText(R.string.set_location);
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


}
