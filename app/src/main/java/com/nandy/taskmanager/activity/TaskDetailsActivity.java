package com.nandy.taskmanager.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestBuilder;
import com.nandy.taskmanager.R;
import com.nandy.taskmanager.image.ImageLoader;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.mvp.model.CreateTaskModel;
import com.nandy.taskmanager.mvp.model.DateFormatModel;
import com.nandy.taskmanager.mvp.model.TaskModel;
import com.nandy.taskmanager.mvp.model.TaskRemindersModel;
import com.nandy.taskmanager.mvp.presenter.TaskItemPresenter;
import com.nandy.taskmanager.mvp.view.TaskDetailsView;

import java.util.Locale;

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
    @BindView(R.id.txt_planned_start_time)
    TextView mPlannedStartTimeTextView;
    @BindView(R.id.txt_actual_start_time)
    TextView mActualStartTimeTextView;
    @BindView(R.id.txt_location)
    TextView mLocationTextView;
    @BindView(R.id.hint_location)
    TextView mLocationHintTextView;
    @BindView(R.id.txt_status)
    TextView mStatusTextView;
    @BindView(R.id.txt_scheduled_duration)
    TextView mScheduledDurationTextView;
    @BindView(R.id.txt_time_spent)
    TextView mTimeSpentTextView;
    @BindView(R.id.txt_period)
    TextView mRepeatPeriodTextView;

    @BindView(R.id.btn_control)
    Button mControlButton;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.layout_actual_start_time)
    View mActualStartTimeLayout;
    @BindView(R.id.layout_time_spent)
    View mTimeSpentLayout;

    private TaskItemPresenter mPresenter;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Task task = getIntent().getParcelableExtra("task");
        mPresenter = new TaskItemPresenter(this);
        mPresenter.setDateFormatModel(new DateFormatModel());
        mPresenter.setTaskReminderModel(new TaskRemindersModel(getApplicationContext()));
        mPresenter.setTaskModel(new TaskModel(getApplicationContext(), task));

        mPresenter.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.menu_task, menu);
        mPresenter.setupMenu();
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
                mPresenter.resetStart();
                break;

            case R.id.action_reset_end:
                mPresenter.resetEnd();
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
    public void setPlannedStartTime(String time) {
        mPlannedStartTimeTextView.setText(time);
    }

    @Override
    public void setActualStartTime(String time) {
        mActualStartTimeTextView.setText(time);
    }

    @Override
    public void setScheduledDuration(int value, int textResId) {
        mScheduledDurationTextView.setText(String.format(Locale.getDefault(), "%d %s", value, getString(textResId)));
    }

    @Override
    public void setTimeSpent(int value, int textResId) {
        mTimeSpentTextView.setText(String.format(Locale.getDefault(), "%d %s", value, getString(textResId)));
    }

    @Override
    public void setRepeatPeriod(int textResId) {
        mRepeatPeriodTextView.setText(textResId);
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

    @Override
    public void setLocationVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mLocationTextView.setVisibility(visibility);
        mLocationHintTextView.setVisibility(visibility);
    }

    @Override
    public void setActualStartDateVisible(boolean visible) {
        mActualStartTimeLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setTimeSpentVisible(boolean visible) {
        mTimeSpentLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setResetEndMenuOptionEnabled(boolean enabled) {
        MenuItem item = mMenu.findItem(R.id.action_reset_end);
        item.setTitle(getMenuItemText(R.string.reset_end, enabled));
        item.setEnabled(enabled);
    }

    @Override
    public void setResetStartMenuOptionEnabled(boolean enabled) {
        MenuItem item = mMenu.findItem(R.id.action_reset_start);
        item.setTitle(getMenuItemText(R.string.reset_start, enabled));
        item.setEnabled(enabled);
    }

    private Spannable getMenuItemText(@StringRes int textResId, boolean enabled) {
        SpannableString spannable = new SpannableString(getString(textResId));
        int colorResId = enabled ? R.color.primaryTextColor : android.R.color.secondary_text_dark_nodisable;
        spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), colorResId)), 0, spannable.length(), 0);
        return spannable;
    }
}
