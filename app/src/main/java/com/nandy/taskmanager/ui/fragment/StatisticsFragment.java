package com.nandy.taskmanager.ui.fragment;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.nandy.taskmanager.R;
import com.nandy.taskmanager.mvp.contract.StatisticsContract;
import com.nandy.taskmanager.mvp.model.DateFormatModel;
import com.nandy.taskmanager.mvp.model.StatisticsModel;
import com.nandy.taskmanager.mvp.presenter.StatisticsPresenter;

import org.kaerdan.presenterretainer.PresenterFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StatisticsFragment
        extends PresenterFragment<StatisticsContract.Presenter, StatisticsContract.View>
        implements StatisticsContract.View {


    @BindView(R.id.list_view)
    ExpandableListView mListView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mSwipeRefreshLayout.setOnRefreshListener(() -> getPresenter().refresh());

        if (savedInstanceState != null && getPresenter() != null) {
            getPresenter().restoreInstanceState(savedInstanceState);
        }

    }

    @Override
    protected StatisticsContract.Presenter onCreatePresenter() {
        StatisticsPresenter presenter = new StatisticsPresenter();
        presenter.setStatisticsModel(new StatisticsModel(getContext()));
        presenter.setDateFormatModel(new DateFormatModel(getContext()));

        return presenter;
    }

    @Override
    protected StatisticsFragment getPresenterView() {
        return this;
    }

    @Override
    public <T extends ExpandableListAdapter> void setAdapter(T adapter) {
        mListView.setAdapter(adapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        getPresenter().saveInstanceState(outState, mListView.onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void restoreListState(Parcelable state) {
        mListView.onRestoreInstanceState(state);
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        mSwipeRefreshLayout.setRefreshing(refreshing);
    }
}
