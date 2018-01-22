package com.nandy.taskmanager.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.nandy.taskmanager.R;
import com.nandy.taskmanager.adapter.StatisticsAdapter;
import com.nandy.taskmanager.model.StatisticsResult;
import com.nandy.taskmanager.mvp.model.StatisticsModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StatisticsFragment extends Fragment {


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

        StatisticsModel statisticsModel = new StatisticsModel(getContext());

        List<Pair<String, List<StatisticsResult>>> data = new ArrayList<>();
        data.add(new Pair<>(getString(R.string.january), statisticsModel.getStatistics(Calendar.JANUARY)));
        data.add(new Pair<>(getString(R.string.february), statisticsModel.getStatistics(Calendar.FEBRUARY)));
        data.add(new Pair<>(getString(R.string.march), statisticsModel.getStatistics(Calendar.MARCH)));
        data.add(new Pair<>(getString(R.string.april), statisticsModel.getStatistics(Calendar.APRIL)));
        data.add(new Pair<>(getString(R.string.may), statisticsModel.getStatistics(Calendar.MAY)));
        data.add(new Pair<>(getString(R.string.june), statisticsModel.getStatistics(Calendar.JUNE)));
        data.add(new Pair<>(getString(R.string.july), statisticsModel.getStatistics(Calendar.JULY)));
        data.add(new Pair<>(getString(R.string.august), statisticsModel.getStatistics(Calendar.AUGUST)));
        data.add(new Pair<>(getString(R.string.september), statisticsModel.getStatistics(Calendar.SEPTEMBER)));
        data.add(new Pair<>(getString(R.string.october), statisticsModel.getStatistics(Calendar.OCTOBER)));
        data.add(new Pair<>(getString(R.string.november), statisticsModel.getStatistics(Calendar.NOVEMBER)));
        data.add(new Pair<>(getString(R.string.december), statisticsModel.getStatistics(Calendar.DECEMBER)));

        StatisticsAdapter adapter = new StatisticsAdapter(data);
        mListView.setAdapter(adapter);

    }

    public static StatisticsFragment newInstance(Context context) {
        StatisticsFragment fragment = new StatisticsFragment();
        return fragment;
    }

}
