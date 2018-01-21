package com.nandy.taskmanager;

import android.support.v4.view.ViewPager;

/**
 * Created by yana on 21.01.18.
 */

public abstract class SimpleOnPageChangedListener implements ViewPager.OnPageChangeListener {

    public abstract void onPageChanged(int position);

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        onPageChanged(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}

