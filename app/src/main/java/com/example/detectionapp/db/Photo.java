package com.example.detectionapp.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "photo")
public class Photo {
    public Photo(String filepath, Date creationDate)
    {
        this.filepath = filepath;
        this.creationDate = creationDate;
    }
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String filepath;
    private Date creationDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}