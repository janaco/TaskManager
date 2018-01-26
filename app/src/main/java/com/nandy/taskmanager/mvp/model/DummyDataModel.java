package com.nandy.taskmanager.mvp.model;

import android.content.Context;

import com.nandy.taskmanager.AppPreferencesStorage;
import com.nandy.taskmanager.db.AppDatabase;
import com.nandy.taskmanager.db.dao.TasksDao;
import com.nandy.taskmanager.enums.Duration;
import com.nandy.taskmanager.enums.RepeatPeriod;
import com.nandy.taskmanager.enums.TaskStatus;
import com.nandy.taskmanager.model.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by razomer on 18.01.18.
 */

public class DummyDataModel {

    private static final int DEFAULT_CAPACITY = 30;

    private static final String TITLE_TEMPLATE = "Task #";
    private static final String DESCRIPTION_TEMPLATE = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.";

    private final Random mRandom;
    private final Context mContext;

    public DummyDataModel(Context context){
        mContext = context;
        mRandom = new Random();
    }

    public List<Task> generateDummyData(){
        return generateDummyData(DEFAULT_CAPACITY);
    }

    public List<Task> generateDummyData(int capacity) {

        List<Task> tasks = new ArrayList<>();
        TasksDao tasksDao = AppDatabase.getInstance(mContext).tasksDao();
        Duration defaultDuration = AppPreferencesStorage.getDefaultDuration(mContext);
        int startIndex = tasksDao.getCount() + 1;

        for (int index = startIndex; index < startIndex + capacity; index++) {

            String title = String.format(Locale.getDefault(), "%s %d", TITLE_TEMPLATE, index);
            Task task = new Task(System.currentTimeMillis());
            task.setTitle(title);
            task.setDescription(DESCRIPTION_TEMPLATE);

            int day = getRandomDayOfTheMonth();
            int month = getRandomMonth();
            int startHour = getRandomHour();
            int startMinute = getRandomMinute();


            Date startDate = generateDate(month, day, startHour, startMinute);

            task.setPlannedStartDate(startDate);
            task.setStatus(TaskStatus.NEW);
            task.setScheduledDuration(defaultDuration);
            task.setRepeatPeriod(getRandomPeriod(RepeatPeriod.values()));

            tasksDao.insert(task);
            tasks.add(task);
        }

        return tasks;
    }

    private RepeatPeriod getRandomPeriod(RepeatPeriod[] periods) {
        return periods[mRandom.nextInt(periods.length - 1)];
    }


    private int getRandomDayOfTheMonth() {
        return mRandom.nextInt(28);
    }

    private int getRandomMonth() {
        return mRandom.nextInt(12);
    }

    private int getRandomHour() {
        return mRandom.nextInt(24);
    }

    private int getRandomMinute() {
        return mRandom.nextInt(60);
    }

    private Date generateDate(int month, int day, int hour, int minute) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        return calendar.getTime();
    }


}
