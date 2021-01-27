package com.example.detectionapp.db;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class DetectionViewModel extends AndroidViewModel {

    private AppRepository detectionRepository;

    private LiveData<List<Photo>> detections;

    public DetectionViewModel(@NonNull Application application)
    {
        super(application);
        detectionRepository = new AppRepository(application);
    }



    public void insert(Detection detection)
    {
        detectionRepository.insert(detection);
    }

    public void update(Detection detection)
    {
        detectionRepository.update(detection);
    }
    public void delete(Detection detection)
    {
        detectionRepository.delete(detection);
    }
    public void findDetectionsForPhoto( int photoId)
    {
        detectionRepository.findDetectionsForPhoto(photoId);
    }
}
