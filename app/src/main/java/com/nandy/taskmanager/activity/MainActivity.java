package com.nandy.taskmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.nandy.taskmanager.R;
import com.nandy.taskmanager.SimpleOnPageChangedListener;
import com.nandy.taskmanager.adapter.TabsPagerAdapter;
import com.nandy.taskmanager.mvp.contract.MainActivityContract;
import com.nandy.taskmanager.mvp.model.DummyDataModel;
import com.nandy.taskmanager.mvp.model.TaskRecordsModel;
import com.nandy.taskmanager.mvp.model.TasksInRadiusTrackingModel;
import com.nandy.taskmanager.mvp.presenter.MainPresenter;
import com.nandy.taskmanager.ui.fragment.StatisticsFragment;
import com.nandy.taskmanager.ui.fragment.TaskListFragment;

import org.kaerdan.presenterretainer.PresenterActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * MainActivity screen contains viewPager or two fragments: TasksListFragment and StatisticsFragment.
 */
public class MainActivity extends PresenterActivity<MainActivityContract.Presenter, MainActivityContract.View>
        implements MainActivityContract.View {

    @BindView(R.id.container)
    ViewPager mViewPager;
    @BindView(R.id.btn_create_task)
    FloatingActionButton mFab;

    private Snackbar mExitSnackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(findViewById(R.id.toolbar));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle(R.string.app_name);
        }

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        mViewPager.setAdapter(createPagerAdapter());
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        mViewPager.addOnPageChangeListener(new SimpleOnPageChangedListener() {
            @Override
            public void onPageChanged(int position) {
                if (position == 0) {
                    mFab.show();
                } else {
                    mFab.hide();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_create_task:
                onCreateTaskClick();
                return true;

            case R.id.action_fill_list:
                getPresenter().onFillListOptionSelected();
                return true;

            case R.id.action_clear_all:
                onClearAllOptionSelected();
                return true;

            case R.id.action_settings:
                getPresenter().onSettingsOptionSelected();
                return true;

            case R.id.action_exit:
                getPresenter().onExitOptionSelected();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {

        if (mExitSnackBar == null) {
            mExitSnackBar = createExitSnackBar();
            mExitSnackBar.show();
        } else if (!mExitSnackBar.isShown()) {
            mExitSnackBar.show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected MainActivityContract.Presenter onCreatePresenter() {
        MainPresenter presenter = new MainPresenter();
        presenter.setDummyDataMode(new DummyDataModel(getApplicationContext()));
        presenter.setRecordsModel(new TaskRecordsModel(getApplicationContext()));
        TasksInRadiusTrackingModel trackingModel = new TasksInRadiusTrackingModel(this);
        trackingModel.setOnServiceConnectedListener(presenter);
        presenter.setTaskInRadiusTrakingModel(trackingModel);

        return presenter;
    }

    @Override
    protected MainActivityContract.View getPresenterView() {
        return this;
    }

    @OnClick(R.id.btn_create_task)
    void onCreateTaskClick() {
        getPresenter().onCreateTaskClick();
    }

    @Override
    public void launchActivity(Bundle args, Class<?> cls) {
        Intent intent = new Intent(getApplicationContext(), cls);
        if (args != null){
            intent.putExtras(args);
        }
        startActivity(intent);
    }

    @Override
    public void launchActivityForResult(Bundle args, Class<?> cls, int requestCode) {
        Intent intent = new Intent(getApplicationContext(), cls);
        intent.putExtras(args);
        startActivityForResult(intent, requestCode);
    }

    private void onClearAllOptionSelected() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.clear_all_data)
                .setPositiveButton(R.string.clear_all, (dialogInterface, i) -> getPresenter().onClearAllOptionSelected())
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }

    private Snackbar createExitSnackBar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.main_content), R.string.exit_app_message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.secondaryColor));
        TextView textView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.secondaryTextColor));

        return snackbar;
    }

    private TabsPagerAdapter createPagerAdapter() {
        return new TabsPagerAdapter(
                getSupportFragmentManager(),
                new Fragment[]{new TaskListFragment(), new StatisticsFragment()});
    }

}
