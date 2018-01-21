package com.nandy.taskmanager.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.nandy.taskmanager.model.Statistics;

import java.util.List;

/**
 * Created by yana on 21.01.18.
 */

@Dao
public interface StatisticsDao {

    @Query("SELECT * FROM statistics")
    List<Statistics> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Statistics statistics);

    @Update
    void update(Statistics statistics);

    @Delete
    void delete(Statistics statistics);

    @Query("DELETE FROM statistics")
    void deleteAll();

}
