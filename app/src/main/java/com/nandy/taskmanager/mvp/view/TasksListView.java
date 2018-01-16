package com.nandy.taskmanager.mvp.view;

import android.widget.ArrayAdapter;

/**
 * Created by yana on 16.01.18.
 */

public interface TasksListView {

    <T extends ArrayAdapter>void setAdapter(T adapter);
}
