package com.nandy.taskmanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;

import com.nandy.taskmanager.R;
import com.nandy.taskmanager.mvp.model.CreateTaskModel;
import com.nandy.taskmanager.mvp.model.ValidationModel;
import com.nandy.taskmanager.mvp.presenter.CreateTaskPresenter;
import com.nandy.taskmanager.mvp.view.CreateTaskView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TaskActivity extends AppCompatActivity implements CreateTaskView {

    private final static int REQUEST_CODE_LOCATION = 52;

    @BindView(R.id.input_name)
    EditText inputTitle;
    @BindView(R.id.input_comment)
    EditText inputComment;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private CreateTaskPresenter mPresener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.comment_task);
        }

        mPresener = new CreateTaskPresenter(this);
        mPresener.setCreateTaskMode(new CreateTaskModel(getApplicationContext()));
        mPresener.setValidationModel(new ValidationModel());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }


    @OnClick(R.id.btn_save)
    void onSaveBtnClick() {

        String title = inputTitle.getText().toString();
        String comment = inputComment.getText().toString();

        mPresener.createTask(title, comment);
        finish();

    }

    @OnClick(R.id.btn_cancel)
    void onCancelBtnClick() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @OnClick(R.id.btn_set_location)
    void onSetLocationButtonClick(){
        startActivityForResult(new Intent(getApplicationContext(), MapActivity.class), REQUEST_CODE_LOCATION);
    }

    @Override
    public void setTitleError(int textResId) {
        inputTitle.setError(getString(textResId));
    }

    @Override
    public void setCommentError(int textResId) {
        inputComment.setError(getString(textResId));
    }


}
