package com.example.detectionapp.Api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DetectionModel {

    @SerializedName("classname")
    @Expose
    private String classname;
    @SerializedName("probability")
    @Expose
    private String probability;
    @SerializedName("image")
    @Expose
    private String image;

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getProbability() {
        return probability;
    }

    public void setProbability(String probability) {
        this.probability = probability;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
