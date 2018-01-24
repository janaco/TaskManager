package com.nandy.taskmanager.mvp.presenter;

import com.nandy.taskmanager.adapter.StatisticsAdapter;
import com.nandy.taskmanager.mvp.contract.StatisticsContract;
import com.nandy.taskmanager.mvp.model.StatisticsModel;

/**
 * Created by yana on 24.01.18.
 */

public class StatisticsPresenter implements StatisticsContract.Presenter {

    private StatisticsContract.View mView;

    private StatisticsModel mStatisticsModel;

    @Override
    public void resume() {
        loadStatistics();
    }

    @Override
    public void pause() {

    }

    @Override
    public void onAttachView(StatisticsContract.View view) {
        mView = view;
    }

    @Override
    public void onDetachView() {
        mView = null;
    }

    @Override
    public void onDestroy() {

    }

    private void loadStatistics() {
        mView.setAdapter(new StatisticsAdapter(mStatisticsModel.getStatistics()));
    }

    public void setStatisticsModel(StatisticsModel mStatisticsModel) {
        this.mStatisticsModel = mStatisticsModel;
    }
}
