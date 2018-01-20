package com.nandy.taskmanager.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestBuilder;
import com.nandy.taskmanager.ImageLoader;
import com.nandy.taskmanager.R;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.mvp.model.CreateTaskModel;
import com.nandy.taskmanager.mvp.model.DateFormatModel;
import com.nandy.taskmanager.mvp.model.TaskDetailsModel;
import com.nandy.taskmanager.mvp.model.TaskRecordsModel;
import com.nandy.taskmanager.mvp.model.TaskScheduleModel;
import com.nandy.taskmanager.mvp.presenter.TaskDetailsPresenter;
import com.nandy.taskmanager.mvp.view.TaskDetailsView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TaskDetailsActivity extends AppCompatActivity implements TaskDetailsView {

    public static final int REQUEST_CODE_EDIT = 61;

    @BindView(R.id.image_task)
    ImageView mTaskImageView;
    @BindView(R.id.txt_title)
    TextView mTitleTextView;
    @BindView(R.id.txt_description)
    TextView mDescriptionTextView;
    @BindView(R.id.txt_time)
    TextView mTimeTextView;
    @BindView(R.id.txt_location)
    TextView mLocationTextView;
    @BindView(R.id.txt_status)
    TextView mStatusTextView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.btn_control)
    Button mControlButton;

    private TaskDetailsPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        Task task = getIntent().getParcelableExtra("task");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mPresenter = new TaskDetailsPresenter(this);
        mPresenter.setDateFormatModel(new DateFormatModel());
        mPresenter.setDetailsModel(new TaskDetailsModel(task));
        mPresenter.setRecordsModel(new TaskRecordsModel(getApplicationContext()));
        mPresenter.setShceduleModel(new TaskScheduleModel(getApplicationContext()));

        mPresenter.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_edit:
                Intent intent = new Intent(this, CreateTaskActivity.class);
                intent.putExtra("task", mPresenter.getTask());
                intent.putExtra("mode", CreateTaskModel.MODE_EDIT);
                startActivityForResult(intent, REQUEST_CODE_EDIT);

                break;

            case R.id.action_delete:
                mPresenter.delete();
                finish();
                break;

            case R.id.action_reset_start:
                break;

            case R.id.action_reset_end:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.btn_control)
    void onControlButtonClick() {
        mPresenter.toggleStatus();
    }

    @Override
    public void setTitle(String title) {
        mTitleTextView.setText(title);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }


    @Override
    public void setDescription(String description) {
        mDescriptionTextView.setText(description);
    }

    @Override
    public void setTime(String time) {
        mTimeTextView.setText(time);
    }

    @Override
    public void setStatus(String status) {
        mStatusTextView.setText(status);
    }

    @Override
    public void setLocation(String location) {
        mLocationTextView.setText(location);
    }

    @Override
    public void setControlButtonEnabled(boolean enabled) {
        mControlButton.setEnabled(enabled);
    }

    @Override
    public void setControlButtonText(int resId) {
        mControlButton.setText(resId);
    }

    @Override
    public void loadImage(String image, boolean drawMapPin) {

        RequestBuilder<Drawable> requestBuilder =
                drawMapPin ?
                        ImageLoader.load(getApplicationContext(), image, R.mipmap.ic_map_marker)
                        : ImageLoader.load(getApplicationContext(), image);
        requestBuilder.into(mTaskImageView);
    }

    @Override
    public void loadImage(int imageResId, boolean drawMapPin) {
        RequestBuilder<Drawable> requestBuilder =
                drawMapPin ?
                        ImageLoader.load(getApplicationContext(), imageResId, R.mipmap.ic_map_marker)
                        : ImageLoader.load(getApplicationContext(), imageResId);
        requestBuilder.into(mTaskImageView);
    }
}
