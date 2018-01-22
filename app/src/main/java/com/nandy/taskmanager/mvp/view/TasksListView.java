package com.nandy.taskmanager.mvp.view;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.nandy.taskmanager.model.Task;

/**
 * Created by yana on 16.01.18.
 */

public interface TasksListView {

    <T extends BaseSwipeAdapter>void setAdapter(T adapter);

    void startEditTaskActivity(Task task);
}
