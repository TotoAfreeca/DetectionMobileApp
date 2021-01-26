package com.example.detectionapp.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = { Detection.class, Photo.class },
        version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract DetectionDao getDetectionDao();
    public abstract PhotoDao getPhotoDao();

    public static volatile AppDatabase INSTANCE;
    public static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriterExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (AppDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app_db").build();
                }
            }
        }
        return INSTANCE;
    }
}