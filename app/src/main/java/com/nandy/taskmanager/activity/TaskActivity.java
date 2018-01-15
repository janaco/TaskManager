package com.nandy.taskmanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;

import com.nandy.taskmanager.Constants;
import com.nandy.taskmanager.R;
import com.nandy.taskmanager.model.Task;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TaskActivity extends AppCompatActivity {

    @BindView(R.id.input_name)
    EditText inputName;
    @BindView(R.id.input_comment)
    EditText inputComment;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

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

        String name = inputName.getText().toString();
        String comment = inputComment.getText().toString();

        if (TextUtils.isEmpty(name)) {
            inputName.setError(getString(R.string.empty_field));
        }

        if (TextUtils.isEmpty(comment)) {
            inputComment.setError(getString(R.string.empty_field));
        }

        Task task = new Task(name, comment);
        Intent intent = new Intent();
        intent.putExtra(Constants.TASK, task);

        setResult(Activity.RESULT_OK, intent);
        finish();

    }

    @OnClick(R.id.btn_cancel)
    void onCancelBtnClick() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}
