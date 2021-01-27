package com.example.detectionapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.detectionapp.Api.ApiHandler;
import com.example.detectionapp.R;
import com.example.detectionapp.Recycler.DetectionAdapter;
import com.example.detectionapp.db.AppDatabase;
import com.example.detectionapp.db.Detection;
import com.example.detectionapp.db.DetectionDao;
import com.example.detectionapp.db.DetectionViewModel;
import com.example.detectionapp.db.Photo;

import java.util.ArrayList;
import java.util.List;

public class DetectionListActivity extends AppCompatActivity {

    Spinner spinnerList;
    Button detectButton;

    Button buttonRemoveList;
    RecyclerView recyclerView;
    List<Detection> detectionList = new ArrayList<Detection>();;
    ArrayList<String> listNames;
    String m_Text;
    int photoId;
    String photoFilePath;
    TextView detectionsCount;

    private DetectionViewModel detectionViewModel;
    ApiHandler apiHandler;
    DetectionAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection_list);




        photoId = getIntent().getIntExtra("photoId", 0);
        photoFilePath = getIntent().getStringExtra("filepath");

        recyclerView = findViewById(R.id.recyclerView);

        DetectionAdapter myAdapter = new DetectionAdapter(DetectionListActivity.this, detectionList );
        apiHandler = new ApiHandler(this);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        detectionViewModel = ViewModelProviders.of(this).get(DetectionViewModel.class);
        detectionViewModel.findAll(photoId).observe(this, new Observer<List<Detection>>() {
            @Override
            public void onChanged(List<Detection> detections) {
                myAdapter.setDetections(detections);
                detections.size();
                detectionsCount.setText("test");

            }
        });
        detectButton = findViewById(R.id.button_detect);

        detectButton.setOnClickListener(v -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage(R.string.get_detections_dialog);
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    R.string.yes,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            apiHandler.getDetectionsFromServer(photoId, photoFilePath);
                            dialog.cancel();
                        }
                    });

            builder1.setNegativeButton(
                    R.string.no,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();



        });


    }

    @Override
    public void onResume()
    {
        super.onResume();
        //new RefreshList().execute();
        //new GetDetectionsById().execute();
    }






}