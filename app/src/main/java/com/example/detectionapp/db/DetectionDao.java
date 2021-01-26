package com.example.detectionapp.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DetectionDao {

    @Insert
    void insert(Detection detection);

    @Update
    void update(Detection... detections);

    @Delete
    void delete(Detection... detections);

    @Query("SELECT * FROM Detection")
    List<Detection> getAllDetections();

    @Query("SELECT * FROM Detection WHERE photoId=:photoId")
    List<Detection> findDetectionsForPhoto(final int photoId);
}