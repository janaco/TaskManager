package com.nandy.taskmanager.mvp.contract;

import android.os.Bundle;
import android.os.Parcelable;
import android.widget.ExpandableListAdapter;

/**
 * Created by yana on 24.01.18.
 */

public interface StatisticsContract {

    interface Presenter extends org.kaerdan.presenterretainer.Presenter<View> {

        void saveInstanceState(Bundle outState, Parcelable listViewState);

        void restoreInstanceState(Bundle savedInstanceState);
    }

    interface View extends org.kaerdan.presenterretainer.Presenter.View {

        <T extends ExpandableListAdapter> void setAdapter(T adapter);

        void restoreListState(Parcelable state);

    }
}
