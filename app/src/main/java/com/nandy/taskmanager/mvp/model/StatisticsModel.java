package com.nandy.taskmanager.mvp.model;

import android.content.Context;

import com.nandy.taskmanager.db.AppDatabase;
import com.nandy.taskmanager.db.dao.StatisticsDao;
import com.nandy.taskmanager.model.StatisticsResult;

import java.util.Calendar;
import java.util.List;

/**
 * Created by razomer on 22.01.18.
 */

public class StatisticsModel {

    private StatisticsDao mStatisticsDao;

    public StatisticsModel(Context context){
        mStatisticsDao = AppDatabase.getInstance(context).statisticsDao();
    }

    public List<StatisticsResult> getStatistics(int month) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long dateStart = calendar.getTimeInMillis();

        calendar.add(Calendar.MONTH, 1);
        long dateEnd = calendar.getTimeInMillis();

        return getStatistics(dateStart, dateEnd);

    }

    private List<StatisticsResult> getStatistics(long dateStart, long dateEnd) {
        return mStatisticsDao.select(dateStart, dateEnd);
    }
}
