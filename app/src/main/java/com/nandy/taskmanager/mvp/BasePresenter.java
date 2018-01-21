package com.nandy.taskmanager.mvp;

import android.os.Bundle;

/**
 * Created by yana on 16.01.18.
 */

public abstract class BasePresenter {

    public abstract void start();

    public abstract void stop();

    public void resume(){}

    public void pause(){}

    public void saveInstanceState(Bundle outState){};

    public void restoreInstanceState(Bundle savedInstanceState){};

}
