package com.example.detectionapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.detectionapp.R;
import com.squareup.picasso.Picasso;

public class FullScreenActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;
    private String fullScreenInd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.photo_filename);
        String filepath = getIntent().getStringExtra("filepath");
        String filename = getIntent().getStringExtra("filename");
        textView.setText(filename);

        Picasso.get()
                .load(filepath)
                .fit()
                .centerInside()
                .into(imageView);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        imageView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        imageView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        imageView.setAdjustViewBounds(false);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();

    }
}