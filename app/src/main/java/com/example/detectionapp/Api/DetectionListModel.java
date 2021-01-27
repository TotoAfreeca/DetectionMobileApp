package com.example.detectionapp.Api;


import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DetectionListModel {

    @SerializedName("detections")
    @Expose
    private List<DetectionModel> detections = null;

    public List<DetectionModel> getDetections() {
        return detections;
    }

    public void setDetections(List<DetectionModel> detections) {
        this.detections = detections;
    }

}