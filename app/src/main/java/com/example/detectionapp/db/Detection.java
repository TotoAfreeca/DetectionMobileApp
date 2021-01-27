package com.example.detectionapp.db;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

import static androidx.room.ForeignKey.CASCADE;


@Entity(foreignKeys = @ForeignKey(entity = Photo.class,
        parentColumns = "uid",
        childColumns = "photoId",
        onDelete = CASCADE))
public class Detection {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "classname")
    public String classname;

    @ColumnInfo(name = "probability")
    public float probability;

    @ColumnInfo(name = "filepath")
    public String filepath;

    @ColumnInfo(name = "photoId")
    public int photoId;

}

