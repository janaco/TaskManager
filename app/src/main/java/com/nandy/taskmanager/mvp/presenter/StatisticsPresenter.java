package com.nandy.taskmanager.mvp.presenter;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Pair;

import com.nandy.taskmanager.adapter.StatisticsAdapter;
import com.nandy.taskmanager.model.StatisticsResult;
import com.nandy.taskmanager.mvp.contract.StatisticsContract;
import com.nandy.taskmanager.mvp.model.DateFormatModel;
import com.nandy.taskmanager.mvp.model.StatisticsModel;
import com.nandy.taskmanager.ui.fragment.StatisticsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yana on 24.01.18.
 */

public class StatisticsPresenter implements StatisticsContract.Presenter {

    private StatisticsContract.View mView;

    private StatisticsModel mStatisticsModel;
    private DateFormatModel mDateFormatModel;
    private StatisticsAdapter mAdapter;

    private Bundle mSavedInstanceState;

    @Override
    public void onAttachView(StatisticsContract.View view) {
        mView = view;

        if (mAdapter == null) {
            mAdapter = new StatisticsAdapter();
            mAdapter.setDateFormatModel(mDateFormatModel);
        }

        mView.setAdapter(mAdapter);

        if (mSavedInstanceState == null) {
            mAdapter.setData(mStatisticsModel.getStatistics());
        } else {
            restoreViewState();
            mSavedInstanceState = null;
        }
    }

    private void restoreViewState() {
//        ArrayList<String> keys = mSavedInstanceState.getStringArrayList(StatisticsFragment.PARAM_KEYS);
//        List<Pair<String, ArrayList<StatisticsResult>>> data = new ArrayList<>();
//
//        for (String key : keys) {
//            ArrayList<StatisticsResult> values = mSavedInstanceState.getParcelableArrayList(key);
//            data.add(new Pair<>(key, values));
//        }
//
//        mAdapter.setData(data);
        mView.restoreListState(mSavedInstanceState.getParcelable(StatisticsFragment.PARAM_LIST_STATE));
    }

    @Override
    public void onDetachView() {
        mView = null;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void saveInstanceState(Bundle outState, Parcelable listViewState) {
//        List<Pair<String, ArrayList<StatisticsResult>>> data = mAdapter.getData();
//
//        ArrayList<String> keys = new ArrayList<>(data.size());
//
//        for (Pair<String, ArrayList<StatisticsResult>> pair : data) {
//            keys.add(pair.first);
//            outState.putParcelableArrayList(pair.first, pair.second);
//        }
//
//        outState.putStringArrayList(StatisticsFragment.PARAM_KEYS, keys);
        outState.putParcelable(StatisticsFragment.PARAM_LIST_STATE, listViewState);
    }

    @Override
    public void restoreInstanceState(Bundle savedInstanceState) {
        mSavedInstanceState = savedInstanceState;
    }

    public void setStatisticsModel(StatisticsModel mStatisticsModel) {
        this.mStatisticsModel = mStatisticsModel;
    }

    public void setDateFormatModel(DateFormatModel mDateFormatModel) {
        this.mDateFormatModel = mDateFormatModel;
    }
}
