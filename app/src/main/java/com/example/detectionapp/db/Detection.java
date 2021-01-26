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

    @ColumnInfo(name = "filename")
    public String filename;

    @ColumnInfo(name = "photoId")
    public int photoId;

}

