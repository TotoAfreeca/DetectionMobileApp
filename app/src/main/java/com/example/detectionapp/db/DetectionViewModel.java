package com.example.detectionapp.db;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.io.File;
import java.util.List;

public class DetectionViewModel extends AndroidViewModel {

    private AppRepository detectionRepository;

    private LiveData<List<Detection>> detections;

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
        File file = new File(detection.filepath);
        boolean deleted = file.delete();
    }

    public List<Detection> findDetectionsForPhoto( int photoId)
    {
        return detectionRepository.findDetectionsForPhoto(photoId);
    }
}
