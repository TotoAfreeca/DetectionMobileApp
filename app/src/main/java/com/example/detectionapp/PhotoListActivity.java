package com.example.detectionapp;

import androidx.appcompat.app.AppCompatActivity;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);

        recyclerView = findViewById(R.id.recyclerView);
        makePhoto = findViewById(R.id.button_take_photo);
        makePhoto.setOnClickListener(v -> {
            this.finish();
        });

        db = AppDatabase.getDatabase(this);
    }



    class GetAllPhotos extends AsyncTask<Void, Void, List<Photo>> {

        protected List<Photo> doInBackground(Void... voids) {
            PhotoDao photoDao = db.getPhotoDao();
            photoList = photoDao.getAllAsc();

            return photoList;
        }

        @Override
        protected void onPostExecute(List<Photo> result) {


            PhotoAdapter myAdapter = new PhotoAdapter(PhotoListActivity.this, photoList );
            recyclerView.setAdapter(myAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(PhotoListActivity.this));
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        new GetAllPhotos().execute();
    }



}