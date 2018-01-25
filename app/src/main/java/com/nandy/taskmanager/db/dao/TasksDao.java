package com.nandy.taskmanager.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.nandy.taskmanager.model.Task;

import java.util.List;

/**
 * Created by yana on 16.01.18.
 */

@Dao
public interface TasksDao {

    @Query("SELECT * FROM tasks")
    List<Task> getAll();

    @Query("SELECT * FROM tasks WHERE id LIKE :id")
    List<Task> getById(long id);

    @Query("SELECT * FROM tasks WHERE geo_position IS NOT NULL AND status LIKE('NEW') ")
    List<Task> selectAllWithLocation();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("DELETE FROM tasks")
    void deleteAll();

    @Query("SELECT count(*) FROM tasks")
    int getCount();

}
