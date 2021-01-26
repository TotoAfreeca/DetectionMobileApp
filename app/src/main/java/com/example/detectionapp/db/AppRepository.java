package com.example.detectionapp.db;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class AppRepository {
    private PhotoDao photoDao;
    private DetectionDao detectionDao;
    private LiveData<List<Photo>> photos;

    AppRepository(Application application){
        AppDatabase database = AppDatabase.getDatabase(application);
        photoDao = database.getPhotoDao();
        detectionDao = database.getDetectionDao();
        photos = photoDao.findAll();
    }

    LiveData<List<Photo>> findAllPhotos()
    {
        return photos;
    }

    void insert(Photo photo){
        AppDatabase.databaseWriterExecutor.execute(() ->{
            photoDao.insert(photo);
        });
    }

    void delete(Photo photo){
        AppDatabase.databaseWriterExecutor.execute(() ->{
            photoDao.delete(photo);
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
