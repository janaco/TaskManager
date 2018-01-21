package com.nandy.taskmanager.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nandy.taskmanager.R;

public class StatisticsFragment extends Fragment {

    public StatisticsFragment() {
        // Required empty public constructor
    }

    public static StatisticsFragment newInstance(Context context) {
        StatisticsFragment fragment = new StatisticsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

}
