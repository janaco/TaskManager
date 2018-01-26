package com.nandy.taskmanager.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.nandy.taskmanager.model.TaskEvent;

import java.util.List;

/**
 * Created by yana on 21.01.18.
 */

@Dao
public interface EventsDao {

    @Query("SELECT * FROM events")
    List<TaskEvent> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TaskEvent taskEvent);

    @Query("DELETE FROM events")
    void deleteAll();

    @Query("DELETE FROM events WHERE id_task=:taskId")
    void delete(long taskId);

}
