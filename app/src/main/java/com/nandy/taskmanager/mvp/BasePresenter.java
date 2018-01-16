package com.nandy.taskmanager.mvp;

import android.os.Bundle;

/**
 * Created by yana on 16.01.18.
 */

public interface BasePresenter {

    void start();

    void saveInstanceState(Bundle outState);

    void restoreInstanceState(Bundle savedInstanceState);

}
