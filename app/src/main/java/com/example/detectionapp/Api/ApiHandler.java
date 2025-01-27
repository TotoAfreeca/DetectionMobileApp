package com.example.detectionapp.Api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.detectionapp.db.Detection;
import com.example.detectionapp.db.DetectionViewModel;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.detectionapp.Utilities.getBatchDirectoryName;


public class ApiHandler {

    OkHttpClient client;
    DetectionViewModel detectionViewModel;
    public ApiHandler(Context context){
        client = new OkHttpClient.Builder()
                .connectTimeout(0, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.SECONDS)
                .writeTimeout(0, TimeUnit.SECONDS)
                .build();

        detectionViewModel = ViewModelProviders.of((FragmentActivity) context).get(DetectionViewModel.class);
    }

    public void getDetectionsFromServer(int photoId, String selectedImagePath){


        String postUrl= "http://"+"192.168.1.100:5000/api/boxes";

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        // Read BitMap by file path

        Uri uri=Uri.parse(selectedImagePath);


        Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath(), options);

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        RequestBody postBodyImage = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "androidToFlask.jpg", RequestBody.create(MediaType.parse("image/jpeg"), byteArray))
                .build();

        postRequest(postUrl, postBodyImage, photoId);

    }


    void postRequest(String postUrl, RequestBody postBody, int photoId) {



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
            }




            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()){
                    // Start processing the JSON object
                    DetectionListModel detectionList = new Gson().fromJson(response.body().string(), DetectionListModel.class);
                    List<DetectionModel> list = detectionList.getDetections();


                    for(DetectionModel model: list ){
                        Detection detection = new Detection();
                        detection.classname = model.getClassname();
                        detection.probability = Float.parseFloat(model.getProbability());
                        detection.photoId = photoId;

                        String base64imgtrimmed = model.getImage().substring(1, model.getImage().length() -1 );
                        byte[] decodedString = Base64.decode(base64imgtrimmed, Base64.DEFAULT);
                        // Bitmap Image
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        String photoName = String.valueOf(System.currentTimeMillis());
                        File file = new File(getBatchDirectoryName(), photoName + "_detection.jpg");
                        Uri savedUri = Uri.fromFile(file);
                        String filePath = savedUri.toString();
                        try (FileOutputStream out = new FileOutputStream(file)) {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
                            detection.filepath = filePath;
                            detectionViewModel.insert(detection);
                            // PNG is a lossless format, the compression factor (100) is ignored
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }else {
                    Log.d("Api", "Api error occurred");
                }
            }
        });
    }
}
