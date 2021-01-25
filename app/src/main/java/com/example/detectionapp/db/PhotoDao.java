package com.example.detectionapp.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Photo photo);

    @Update
    public void update(Photo photo);

    @Delete
    public void delete(Photo photo);

    @Query("DELETE FROM photo")
    public void deleteAll();

    @Query("SELECT * FROM photo ORDER BY creationDate")
    public LiveData<List<Photo>> findAll();

}