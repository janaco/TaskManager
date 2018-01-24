package com.nandy.taskmanager.mvp.contract;

import android.widget.ExpandableListAdapter;

/**
 * Created by yana on 24.01.18.
 */

public interface StatisticsContract {

    interface Presenter extends org.kaerdan.presenterretainer.Presenter<View> {

        void resume();

        void pause();

    }

    interface View extends org.kaerdan.presenterretainer.Presenter.View {

        <T extends ExpandableListAdapter> void setAdapter(T adapter);

    }
}
