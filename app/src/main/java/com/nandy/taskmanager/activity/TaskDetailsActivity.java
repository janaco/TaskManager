package com.nandy.taskmanager.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nandy.taskmanager.R;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.mvp.model.DateFormatModel;
import com.nandy.taskmanager.mvp.model.TaskDetailsModel;
import com.nandy.taskmanager.mvp.presenter.TaskDetailsPresenter;
import com.nandy.taskmanager.mvp.view.TaskDetailsView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskDetailsActivity extends AppCompatActivity implements TaskDetailsView {

    @BindView(R.id.task_image)
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

    private TaskDetailsPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        ButterKnife.bind(this);

        Task task = getIntent().getParcelableExtra("task");

        mPresenter = new TaskDetailsPresenter(this);
        mPresenter.setDateFormatModel(new DateFormatModel());
        mPresenter.setDetailsModel(new TaskDetailsModel(task));

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
                break;

            case R.id.action_delete:
                break;

            case R.id.action_reset_start:
                break;

            case R.id.action_reset_end:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(String title) {
        mTitleTextView.setText(title);
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
    public void loadImage(String image) {
        Glide.with(this)
                .load(image)
                .into(mTaskImageView);
    }
}
