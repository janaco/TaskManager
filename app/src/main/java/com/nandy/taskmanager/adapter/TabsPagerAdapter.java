package com.nandy.taskmanager.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by yana on 21.01.18.
 */

public class TabsPagerAdapter extends FragmentPagerAdapter {

    private final Fragment []mFragments;

    public TabsPagerAdapter(FragmentManager fm, Fragment []fragments) {
        super(fm);
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments[position];
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }
}
