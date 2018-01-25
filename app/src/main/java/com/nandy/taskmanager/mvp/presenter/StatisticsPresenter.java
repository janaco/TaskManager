package com.nandy.taskmanager.mvp.presenter;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Pair;

import com.nandy.taskmanager.Constants;
import com.nandy.taskmanager.SubscriptionUtils;
import com.nandy.taskmanager.adapter.StatisticsAdapter;
import com.nandy.taskmanager.model.StatisticsResult;
import com.nandy.taskmanager.mvp.contract.StatisticsContract;
import com.nandy.taskmanager.mvp.model.DateFormatModel;
import com.nandy.taskmanager.mvp.model.StatisticsModel;
import com.nandy.taskmanager.ui.fragment.StatisticsFragment;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yana on 24.01.18.
 */

public class StatisticsPresenter implements StatisticsContract.Presenter {

    private StatisticsContract.View mView;

    private StatisticsModel mStatisticsModel;
    private DateFormatModel mDateFormatModel;
    private StatisticsAdapter mAdapter;

    private Bundle mSavedInstanceState;

    private Disposable mStatisticsSubscription;

    @Override
    public void onAttachView(StatisticsContract.View view) {
        mView = view;

        if (mAdapter == null) {
            mAdapter = new StatisticsAdapter();
            mAdapter.setDateFormatModel(mDateFormatModel);
        }

        mView.setAdapter(mAdapter);

        if (mSavedInstanceState == null) {
            loadStatistics(false);
        } else {
            restoreViewState();
            mSavedInstanceState = null;
        }
    }

    @Override
    public void refresh() {
loadStatistics(true);
    }

    private void loadStatistics(boolean byUser) {

        mStatisticsSubscription = mStatisticsModel.getStatistics()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> {
                    if (byUser){
                        mView.setRefreshing(true);
                    }
                })
                .doFinally(() -> mView.setRefreshing(false))
                .subscribe(pairs -> mAdapter.setData(pairs), Throwable::printStackTrace);
    }

    private void restoreViewState() {
        mView.restoreListState(mSavedInstanceState.getParcelable(Constants.PARAM_LIST_STATE));
    }

    @Override
    public void onDetachView() {
        mView = null;
        SubscriptionUtils.dispose(mStatisticsSubscription);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void saveInstanceState(Bundle outState, Parcelable listViewState) {
        outState.putParcelable(Constants.PARAM_LIST_STATE, listViewState);
    }

    @Override
    public void restoreInstanceState(Bundle savedInstanceState) {
        mSavedInstanceState = savedInstanceState;
    }

    public void setStatisticsModel(StatisticsModel statisticsModel) {
        mStatisticsModel = statisticsModel;
    }

    public void setDateFormatModel(DateFormatModel dateFormatModel) {
        mDateFormatModel = dateFormatModel;
    }
}
