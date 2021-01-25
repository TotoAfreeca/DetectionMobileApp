package com.example.detectionapp.db;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

import static androidx.room.ForeignKey.CASCADE;


@Entity(foreignKeys = @ForeignKey(entity = Photo.class,
        parentColumns = "id",
        childColumns = "photoId",
        onDelete = CASCADE))
public class Detection {
    @PrimaryKey
    public final int id;
    private final String className;
    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;
    private final int color1;
    private final int color2;
    private final int color3;

    private final int photoId;

    public Detection(int id, String className, int x1, int y1, int x2, int y2,
                     int color1, int color2, int color3, int photoId) {
        this.id = id;
        this.className = className;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color1 = color1;
        this.color2 = color2;
        this.color3 = color3;

        this.photoId = photoId;
    }
}

