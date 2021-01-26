package com.example.detectionapp;

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
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.detectionapp.db.AppDatabase;
import com.example.detectionapp.db.Photo;
import com.example.detectionapp.db.PhotoDao;
import com.example.detectionapp.db.PhotoViewModel;
import com.google.common.util.concurrent.ListenableFuture;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.detectionapp.PathHelper.getPath;


public class MainActivity extends AppCompatActivity {

    private Executor executor = Executors.newSingleThreadExecutor();
    private int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
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

    private PhotoViewModel photoViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photoViewModel = ViewModelProviders.of(this).get(PhotoViewModel.class);


        client = new OkHttpClient.Builder()
                .connectTimeout(0, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.SECONDS)
                .writeTimeout(0, TimeUnit.SECONDS)
                .build();


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
                Log.d("DET", "Rotation degrees: " + String.valueOf(rotationDegrees));

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

                image.close();

            }
        });


        ImageCapture.Builder builder = new ImageCapture.Builder();


        final ImageCapture imageCapture = builder
                .setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation())
                .build();

        preview.setSurfaceProvider(mPreviewView.createSurfaceProvider());

        cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageAnalysis, imageCapture);

        captureImage.setOnClickListener(v -> {

            File file = new File(getBatchDirectoryName(), System.currentTimeMillis() + ".jpg");
            photoName = String.valueOf(System.currentTimeMillis());
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
                            Toast.makeText(MainActivity.this, "Image Saved successfully at: "+ savedUri, Toast.LENGTH_SHORT).show();
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


//    private Bitmap toBitmap(Image image){
//        Image.Plane[] planes = image.getPlanes();
//        ByteBuffer yBuffer = planes[0].getBuffer();
//        ByteBuffer uBuffer = planes[1].getBuffer();
//        ByteBuffer vBuffer = planes[2].getBuffer();
//
//        int ySize = yBuffer.remaining();
//        int uSize = uBuffer.remaining();
//        int vSize = vBuffer.remaining();
//
//        byte[] nv21 = new byte[ySize + uSize + vSize];
//        //U and V are swapped
//        yBuffer.get(nv21, 0, ySize);
//        vBuffer.get(nv21, ySize, vSize);
//        uBuffer.get(nv21, ySize + vSize, uSize);
//
//        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 75, out);
//
//        byte[] imageBytes = out.toByteArray();
//        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//    }


    public String getBatchDirectoryName() {

        String app_folder_path = "";
        app_folder_path = Environment.getExternalStorageDirectory().toString() + "/images";
        File dir = new File(app_folder_path);
        if (!dir.exists() && !dir.mkdirs()) {

        }

        return app_folder_path;
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

            String postUrl= "http://"+"192.168.1.100:5000/api/test";

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            // Read BitMap by file path
            Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath, options);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            RequestBody postBodyImage = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", "androidFlask.jpg", RequestBody.create(MediaType.parse("image/jpeg"), byteArray))
                    .build();

            postRequest(postUrl, postBodyImage);

            Toast.makeText(getApplicationContext(), selectedImagePath, Toast.LENGTH_LONG).show();
        }
    }

    void postRequest(String postUrl, RequestBody postBody) {



        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();

                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Failed to Connect to Server ",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                if (response.isSuccessful()){
                    final Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                    // Remember to set the bitmap in the main thread.
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            captureImage.setImageBitmap(bitmap);
                        }
                    });
                }else {
                    //Handle the error
                }
            }
        });
    }


}