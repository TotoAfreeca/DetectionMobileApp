package com.example.detectionapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;

import com.example.detectionapp.Api.ApiHandler;
import com.example.detectionapp.R;
import com.example.detectionapp.Recycler.DetectionAdapter;
import com.example.detectionapp.db.AppDatabase;
import com.example.detectionapp.db.Detection;
import com.example.detectionapp.db.DetectionDao;
import com.example.detectionapp.db.DetectionViewModel;

import java.util.ArrayList;
import java.util.List;

public class DetectionListActivity extends AppCompatActivity {

    Spinner spinnerList;
    Button detectButton;

    Button buttonRemoveList;
    RecyclerView recyclerView;
    List<Detection> detectionList = new ArrayList<Detection>();;
    ArrayList<String> listNames;
    AppDatabase db;
    String m_Text;
    int photoId;
    String photoFilePath;

    private DetectionViewModel detectionViewModel;
    ApiHandler apiHandler;
    DetectionAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection_list);
        db = AppDatabase.getDatabase(this);



        photoId = getIntent().getIntExtra("photoId", 0);
        photoFilePath = getIntent().getStringExtra("filepath");

        recyclerView = findViewById(R.id.recyclerView);

        DetectionAdapter myAdapter = new DetectionAdapter(DetectionListActivity.this, detectionList );
        apiHandler = new ApiHandler(this);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        detectionViewModel = ViewModelProviders.of(this).get(DetectionViewModel.class);

        detectButton = findViewById(R.id.button_detect);

        detectButton.setOnClickListener(v -> {
            apiHandler.getDetectionsFromServer(photoId, photoFilePath);
            new GetDetectionsById().execute();
        });


    }



    class GetDetectionsById extends AsyncTask<Void, Void, List<Detection>> {

        protected List<Detection> doInBackground(Void... voids) {
            DetectionDao detectionDao = db.getDetectionDao();
            detectionList = detectionDao.findDetectionsForPhoto(photoId);
            return detectionList;
        }

        @Override
        protected void onPostExecute(List<Detection> result) {
            DetectionAdapter myAdapter = new DetectionAdapter(DetectionListActivity.this, detectionList);
            recyclerView.setAdapter(myAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(DetectionListActivity.this));
        }
    }



    @Override
    public void onResume()
    {
        super.onResume();
        //new RefreshList().execute();
        new GetDetectionsById().execute();
    }






}