package com.nandy.taskmanager.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.nandy.taskmanager.R;
import com.nandy.taskmanager.mvp.contract.StatisticsContract;
import com.nandy.taskmanager.mvp.model.StatisticsModel;
import com.nandy.taskmanager.mvp.presenter.StatisticsPresenter;

import org.kaerdan.presenterretainer.PresenterFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StatisticsFragment
        extends PresenterFragment<StatisticsPresenter, StatisticsContract.View>
        implements StatisticsContract.View {


    @BindView(R.id.list_view)
    ExpandableListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

    }

    @Override
    public void onResume() {
        super.onResume();
        getPresenter().resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        getPresenter().pause();
    }

    @Override
    protected StatisticsPresenter onCreatePresenter() {
        StatisticsPresenter presenter = new StatisticsPresenter();
        presenter.setStatisticsModel(new StatisticsModel(getContext()));

        return presenter;
    }

    @Override
    protected StatisticsFragment getPresenterView() {
        return this;
    }

    @Override
    public <T extends ExpandableListAdapter> void setAdapter(T adapter) {

    }
}
