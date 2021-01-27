package com.example.detectionapp.db;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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

    LiveData<List<Detection>> findAllDetections(int photoId)
    {
        return detectionDao.findAll(photoId);
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

    void update(Detection detection){
        AppDatabase.databaseWriterExecutor.execute(() ->{
            detectionDao.update(detection);
        });
    }

    void delete(Detection detection){
        AppDatabase.databaseWriterExecutor.execute(() ->{
            detectionDao.delete(detection);
        });
    }

    List<Detection> findDetectionsForPhoto(int photoId){
        AtomicReference<List<Detection>> detectionsForPhoto = new AtomicReference<>();
        AppDatabase.databaseWriterExecutor.execute(() ->{
            detectionsForPhoto.set(detectionDao.findDetectionsForPhoto(photoId));

        });
        return detectionsForPhoto.get();
    }
}
