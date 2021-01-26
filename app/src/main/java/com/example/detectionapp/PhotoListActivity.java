package com.example.detectionapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Movie;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;

import com.example.detectionapp.R;
import com.example.detectionapp.Recycler.PhotoAdapter;
import com.example.detectionapp.db.AppDatabase;
import com.example.detectionapp.db.Photo;
import com.example.detectionapp.db.PhotoDao;
import com.example.detectionapp.db.PhotoViewModel;

import java.util.ArrayList;
import java.util.List;

public class PhotoListActivity extends AppCompatActivity {

    Spinner spinnerList;
    Button makePhoto;
    Button buttonRemoveList;
    RecyclerView recyclerView;
    List<Photo> photoList = new ArrayList<Photo>();;
    ArrayList<String> listNames;
    AppDatabase db;
    String m_Text;

    private PhotoViewModel photoViewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);


        recyclerView = findViewById(R.id.recyclerView);

        PhotoAdapter myAdapter = new PhotoAdapter(PhotoListActivity.this, photoList );

        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        photoViewModel = ViewModelProviders.of(this).get(PhotoViewModel.class);
        photoViewModel.findAll().observe(this, new Observer<List<Photo>>() {
            @Override
            public void onChanged(List<Photo> photos) {
                myAdapter.setPhotos(photos);
            }
        });

        makePhoto = findViewById(R.id.button_take_photo);
        makePhoto.setOnClickListener(v -> {
            this.finish();
        });

//        db = AppDatabase.getDatabase(this);



    }



//    class GetAllPhotos extends AsyncTask<Void, Void, List<Photo>> {
//
//        protected List<Photo> doInBackground(Void... voids) {
//            PhotoDao photoDao = db.getPhotoDao();
//            photoList = photoDao.getAllAsc();
//
//            return photoList;
//        }
//
//        @Override
//        protected void onPostExecute(List<Photo> result) {
//
//
//
//            recyclerView.setAdapter(myAdapter);
//            recyclerView.setLayoutManager(new LinearLayoutManager(PhotoListActivity.this));
//        }
//    }

//    @Override
//    public void onResume()
//    {
//        super.onResume();
//        new GetAllPhotos().execute();
//    }




}