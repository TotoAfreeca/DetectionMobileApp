package com.example.detectionapp.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "photo")
public class Photo {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "filename")
    public String filename;

    @ColumnInfo(name = "filepath")
    public String filepath;

}