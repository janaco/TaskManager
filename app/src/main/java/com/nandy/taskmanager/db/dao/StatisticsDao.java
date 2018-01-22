package com.nandy.taskmanager.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.nandy.taskmanager.model.Statistics;
import com.nandy.taskmanager.model.StatisticsResult;

import java.util.List;

/**
 * Created by razomer on 22.01.18.
 */

@Dao
public interface StatisticsDao{

    @Insert
    void insert(Statistics statistics);

    @Delete
    void  delete(Statistics statistics);

    @Query("SELECT t.id AS mTaskId, t.title AS mTaskTitle, sum(s.spent_time) AS mSpentTime FROM statistics s, tasks t WHERE s.id_task == t.id AND s.start_date BETWEEN :dateStart AND :dateEnd")
    List<StatisticsResult> select(long dateStart, long dateEnd);
}
