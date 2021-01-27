package com.example.detectionapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.pm.PackageManager;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.detectionapp.Api.ApiHandler;
import com.example.detectionapp.R;
import com.example.detectionapp.db.AppDatabase;
import com.example.detectionapp.db.Photo;
import com.example.detectionapp.db.PhotoViewModel;
import com.google.common.util.concurrent.ListenableFuture;

import org.opencv.dnn.Net;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;

import static com.example.detectionapp.PathHelper.getPath;
import static com.example.detectionapp.Utilities.getBatchDirectoryName;


public class MainActivity extends AppCompatActivity {

    private Executor executor = Executors.newSingleThreadExecutor();
    private int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE", "android.permission.INTERNET" };
    private Net mobileNet;


    String selectedImagePath;

    PreviewView mPreviewView;
    ImageView captureImage;
    ImageView drawableImageView;
    ImageCapture imageCapture;
    Button loadFromLibrary;
    Button goToPhotos;
    OkHttpClient client;
    AppDatabase db;
    String photoName;
    String filePath;


//    private SensorManager sensorManager;
//    private Sensor lightSensor;
//    private SensorEventListener lightEventListener;
//    private float maxValue;

    private PhotoViewModel photoViewModel;
    private ApiHandler apiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photoViewModel = ViewModelProviders.of(this).get(PhotoViewModel.class);

        apiHandler = new ApiHandler(this);

        mPreviewView = findViewById(R.id.previewView);

        captureImage = findViewById(R.id.captureImg);
        drawableImageView = findViewById(R.id.drawableImageView);
        goToPhotos = findViewById(R.id.goToPhotosButton);

        if(allPermissionsGranted()){
            //FrameDetectorHelper.loadMobileNet(this);
            startCamera(); //start camera if permission has been granted by user

        } else{
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        loadFromLibrary = findViewById(R.id.load_from_library);

        goToPhotos.setOnClickListener(v -> {
            Intent intentList = new Intent(this, PhotoListActivity.class);
            startActivity(intentList);
        });

//        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
//
//        if (lightSensor == null) {
//            Toast.makeText(this, "The device has no light sensor !", Toast.LENGTH_SHORT).show();
//            finish();
//        }
//
//        // max value for light sensor
//        maxValue = lightSensor.getMaximumRange();

    }

    private void startCamera() {

        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {

                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);

                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }




    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        //Size screen = new Size(mPreviewView.get(), mPreviewView.getHeight()); //size of the screen

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()

                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        imageAnalysis.setAnalyzer(executor, new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {
                int rotationDegrees = image.getImageInfo().getRotationDegrees();
                //Log.d("DET", "Rotation degrees: " + String.valueOf(rotationDegrees));

//                @SuppressLint("UnsafeExperimentalUsageError")
//                Image androidImage = image.getImage();
//                Bitmap bitmap = toBitmap(androidImage);
//                Mat mat = FrameDetectorHelper.getDetectedImage(bitmap);
//                Utils.matToBitmap(mat, bitmap);

//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        drawableImageView.setImageBitmap(bitmap);
//                        drawableImageView.setRotation(90);
//                    }
//                });

                //image.close();

            }
        });


        ImageCapture.Builder builder = new ImageCapture.Builder();


        final ImageCapture imageCapture = builder
                .setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation())
                .build();

        preview.setSurfaceProvider(mPreviewView.createSurfaceProvider());

        cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageAnalysis, imageCapture);

        captureImage.setOnClickListener(v -> {

            photoName = String.valueOf(System.currentTimeMillis());
            File file = new File(getBatchDirectoryName(), photoName + ".jpg");

            ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
            imageCapture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback () {

                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                    Uri savedUri = Uri.fromFile(file);
                    filePath = savedUri.toString();

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            new AddPhoto().execute();
                            Toast.makeText(MainActivity.this, R.string.saved_image_to_db, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError(@NonNull ImageCaptureException error) {
                    error.printStackTrace();
                }
            });
        });
    }


    class AddPhoto extends AsyncTask<Void, Void, Photo> {

        protected Photo doInBackground(Void... voids) {


            Photo photo = new Photo();
            photo.filename = photoName;
            photo.filepath = filePath;
            photoViewModel.insert(photo);
            Log.e("added", photo.filename);
            return photo;
        }

        @Override
        protected void onPostExecute(Photo result) {

            Toast.makeText(MainActivity.this, getResources().getString(R.string.photo_created), Toast.LENGTH_LONG).show();
        }
    }


    private boolean allPermissionsGranted(){

        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermissionsGranted()){
                startCamera();
            } else{
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }



    public void selectImage(View v){
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 0);


    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        if (resCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            selectedImagePath = getPath(getApplicationContext(), uri);

//            String postUrl= "http://"+"192.168.1.100:5000/api/test";
//
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inPreferredConfig = Bitmap.Config.RGB_565;
//            // Read BitMap by file path
//            Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath, options);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//            byte[] byteArray = stream.toByteArray();
//
//            RequestBody postBodyImage = new MultipartBody.Builder()
//                    .setType(MultipartBody.FORM)
//                    .addFormDataPart("image", "androidFlask.jpg", RequestBody.create(MediaType.parse("image/jpeg"), byteArray))
//                    .build();
            Photo photo = new Photo();
            photo.filepath = selectedImagePath;
            photo.filename = uri.getLastPathSegment();

            photoViewModel.insert(photo);
            apiHandler.getDetectionsFromServer(1, selectedImagePath);



            Toast.makeText(getApplicationContext(), selectedImagePath, Toast.LENGTH_LONG).show();
        }
    }




}