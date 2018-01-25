package com.nandy.taskmanager.ui.fragment;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
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
        extends PresenterFragment<StatisticsPresenter, StatisticsContract.View>
        implements StatisticsContract.View {

    public static final String PARAM_KEYS = "keys";
    public static final String PARAM_LIST_STATE = "list_state";

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

        if (savedInstanceState != null && getPresenter() != null) {
            getPresenter().restoreInstanceState(savedInstanceState);
        }

    }

    @Override
    protected StatisticsPresenter onCreatePresenter() {
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
}
