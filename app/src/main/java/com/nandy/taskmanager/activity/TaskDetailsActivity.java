package com.nandy.taskmanager.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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
import com.nandy.taskmanager.Constants;
import com.nandy.taskmanager.R;
import com.nandy.taskmanager.image.ImageLoader;
import com.nandy.taskmanager.model.Task;
import com.nandy.taskmanager.mvp.contract.TaskDetailsContract;
import com.nandy.taskmanager.mvp.model.DateFormatModel;
import com.nandy.taskmanager.mvp.model.TaskModel;
import com.nandy.taskmanager.mvp.presenter.TaskItemPresenter;

import org.kaerdan.presenterretainer.PresenterActivity;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Task information is displayed there.
 */
public class TaskDetailsActivity extends PresenterActivity<TaskDetailsContract.Presenter, TaskDetailsContract.View>
        implements TaskDetailsContract.View {

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.menu_task, menu);
        getPresenter().setupMenu();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_edit:
                getPresenter().edit();
                break;

            case R.id.action_delete:
                onDeleteOptionSelected();
                break;

            case R.id.action_reset_start:
                getPresenter().resetStart();
                break;

            case R.id.action_reset_end:
                getPresenter().resetEnd();
                break;

            case R.id.action_pause:
                getPresenter().pause();
                break;

            case R.id.action_resume:
                getPresenter().resume();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onDeleteOptionSelected(){
            new AlertDialog.Builder(this)
                    .setMessage(R.string.delete_item_alert)
                    .setPositiveButton(R.string.delete, (dialogInterface, i) -> getPresenter().delete())
                    .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                    .show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getPresenter().onActivityResult(requestCode, resultCode, data);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected TaskDetailsContract.Presenter onCreatePresenter() {
        Task task = getIntent().getExtras().getParcelable(Constants.PARAM_TASK);

        TaskItemPresenter presenter = new TaskItemPresenter();
        presenter.setDateFormatModel(new DateFormatModel(getApplicationContext()));
        presenter.setTaskModel(new TaskModel(getApplicationContext(), task));

        return presenter;
    }

    @Override
    protected TaskDetailsContract.View getPresenterView() {
        return this;
    }

    @OnClick(R.id.btn_control)
    void onControlButtonClick() {
        getPresenter().toggleStatus();
    }

    @Override
    public void launchActivityForResult(Bundle args, Class<?> cls, int requestCode) {
        Intent intent = new Intent( getApplicationContext(), cls);
        intent.putExtras(args);
        startActivityForResult(intent, requestCode);
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
    public void setScheduledDuration(@StringRes int textResId) {
        mScheduledDurationTextView.setText(textResId);
    }

    @Override
    public void setTimeSpent(String timeSpent) {
        mTimeSpentTextView.setText(timeSpent);
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


    @Override
    public void setPauseOptionVisible(boolean visible) {
        mMenu.findItem(R.id.action_pause).setVisible(visible);
    }

    @Override
    public void setResumeOptionVisible(boolean visible) {
        mMenu.findItem(R.id.action_resume).setVisible(visible);
    }

    private Spannable getMenuItemText(@StringRes int textResId, boolean enabled) {
        SpannableString spannable = new SpannableString(getString(textResId));
        int colorResId = enabled ? R.color.primaryTextColor : android.R.color.secondary_text_dark_nodisable;
        spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), colorResId)), 0, spannable.length(), 0);
        return spannable;
    }
}
