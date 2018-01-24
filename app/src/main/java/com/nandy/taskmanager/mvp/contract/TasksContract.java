package com.nandy.taskmanager.mvp.contract;

import android.os.Bundle;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.nandy.taskmanager.model.Task;

/**
 * Created by yana on 24.01.18.
 */

public interface TasksContract {

    interface Presenter extends org.kaerdan.presenterretainer.Presenter<View> {

        void resume();

        void pause();

        void add(Task task);

        void delete(int position);

        void resetStart(int position);

        void resetEnd(int position);

        void edit(int position);

        void openDetails(int position);

        void openDetails(Task task);

        void saveInstanceState(Bundle outState, int firstVisiblePosition);

        void setSavedViewState(Bundle savedInstanceState);

    }

    interface View extends org.kaerdan.presenterretainer.Presenter.View {

        <T extends BaseSwipeAdapter> void setAdapter(T adapter);

        void launchActivity(Bundle args, Class<?> cls);

        void launchActivityForResult(Bundle args, Class<?> cls, int requestCode);

        void scrollToPosition(int position);

    }
}
