package com.example.detectionapp.db;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.io.File;
import java.util.List;

public class PhotoViewModel extends AndroidViewModel {

    private AppRepository photoRepository;

    private LiveData<List<Photo>> photos;

    public PhotoViewModel(@NonNull Application application)
    {
        super(application);
        photoRepository = new AppRepository(application);
        photos = photoRepository.findAllPhotos();
    }

    public LiveData<List<Photo>> findAll()
    {
        return photos;
    }

    public void insert(Photo photo)
    {
        photoRepository.insert(photo);
    }

    public void update(Photo photo)
    {
        photoRepository.update(photo);
    }
    public void delete(Photo photo)
    {
        photoRepository.delete(photo);

        File file = new File(photo.filepath);
        boolean deleted = file.delete();
    }
}