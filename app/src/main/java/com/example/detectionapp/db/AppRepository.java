package com.example.detectionapp.db;

import android.app.Application;

public class AppRepository {
    private PhotoDao photoDao;
    private DetectionDao detectionDao;

    AppRepository(Application application){
        AppDatabase database = AppDatabase.getDatabase(application);
        photoDao = database.getPhotoDao();
        detectionDao = database.getDetectionDao();
    }

    void insert(Photo photo){
        AppDatabase.databaseWriterExecutor.execute(() ->{
            photoDao.insert(photo);
        });
    }

    void update(Photo photo){
        AppDatabase.databaseWriterExecutor.execute(() ->{
            photoDao.update(photo);
        });
    }



    void insert(Detection detection){
        AppDatabase.databaseWriterExecutor.execute(() ->{
            detectionDao.insert(detection);
        });
    }
}
