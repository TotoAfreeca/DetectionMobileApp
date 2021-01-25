package com.example.detectionapp.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = { Detection.class, Photo.class },
        version = 1)
public abstract class DetectionDatabase extends RoomDatabase {

    public abstract Detection getDetectionDao();
    public abstract Photo getPhotoDao();
}