package com.nandy.taskmanager.mvp.view;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;

/**
 * Created by yana on 16.01.18.
 */

public interface TasksListView {

    <T extends BaseSwipeAdapter>void setAdapter(T adapter);
}
