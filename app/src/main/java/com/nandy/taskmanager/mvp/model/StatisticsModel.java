package com.nandy.taskmanager.mvp.model;

import android.content.Context;
import android.util.Pair;

import com.nandy.taskmanager.R;
import com.nandy.taskmanager.db.AppDatabase;
import com.nandy.taskmanager.db.dao.StatisticsDao;
import com.nandy.taskmanager.model.StatisticsResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by razomer on 22.01.18.
 */

public class StatisticsModel {

    private final Context mContext;
    private final StatisticsDao mStatisticsDao;

    public StatisticsModel(Context context){
        mContext = context;
        mStatisticsDao = AppDatabase.getInstance(context).statisticsDao();
    }

    public List<Pair<String, List<StatisticsResult>>> getStatistics(){
        List<Pair<String, List<StatisticsResult>>> data = new ArrayList<>();

        data.add(new Pair<>(mContext.getString(R.string.january), getStatistics(Calendar.JANUARY)));
        data.add(new Pair<>(mContext.getString(R.string.february), getStatistics(Calendar.FEBRUARY)));
        data.add(new Pair<>(mContext.getString(R.string.march), getStatistics(Calendar.MARCH)));
        data.add(new Pair<>(mContext.getString(R.string.april), getStatistics(Calendar.APRIL)));
        data.add(new Pair<>(mContext.getString(R.string.may), getStatistics(Calendar.MAY)));
        data.add(new Pair<>(mContext.getString(R.string.june), getStatistics(Calendar.JUNE)));
        data.add(new Pair<>(mContext.getString(R.string.july), getStatistics(Calendar.JULY)));
        data.add(new Pair<>(mContext.getString(R.string.august), getStatistics(Calendar.AUGUST)));
        data.add(new Pair<>(mContext.getString(R.string.september), getStatistics(Calendar.SEPTEMBER)));
        data.add(new Pair<>(mContext.getString(R.string.october), getStatistics(Calendar.OCTOBER)));
        data.add(new Pair<>(mContext.getString(R.string.november), getStatistics(Calendar.NOVEMBER)));
        data.add(new Pair<>(mContext.getString(R.string.december), getStatistics(Calendar.DECEMBER)));
        
        return data;

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
