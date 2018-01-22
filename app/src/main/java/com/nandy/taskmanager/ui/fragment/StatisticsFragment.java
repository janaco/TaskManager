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

import java.util.ArrayList;
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

        List<Pair<String, ArrayList>> data = new ArrayList<>();
        data.add(new Pair<>(getString(R.string.january), new ArrayList()));
        data.add(new Pair<>(getString(R.string.february), new ArrayList()));
        data.add(new Pair<>(getString(R.string.march), new ArrayList()));
        data.add(new Pair<>(getString(R.string.april), new ArrayList()));
        data.add(new Pair<>(getString(R.string.may), new ArrayList()));
        data.add(new Pair<>(getString(R.string.june), new ArrayList()));
        data.add(new Pair<>(getString(R.string.july), new ArrayList()));
        data.add(new Pair<>(getString(R.string.august), new ArrayList()));
        data.add(new Pair<>(getString(R.string.september), new ArrayList()));
        data.add(new Pair<>(getString(R.string.october), new ArrayList()));
        data.add(new Pair<>(getString(R.string.november), new ArrayList()));
        data.add(new Pair<>(getString(R.string.december), new ArrayList()));

        StatisticsAdapter adapter = new StatisticsAdapter(data);
        mListView.setAdapter(adapter);

    }

    public static StatisticsFragment newInstance(Context context) {
        StatisticsFragment fragment = new StatisticsFragment();
        return fragment;
    }

}
