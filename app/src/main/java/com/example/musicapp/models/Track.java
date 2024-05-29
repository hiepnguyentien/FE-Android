package com.example.musicapp.models;

import java.io.Serializable;

public class Track implements Serializable {
    private String trackName;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String author;
    private String artWork;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private String fileName;


    public String getArtWork() {
        return artWork;
    }

    public void setArtWork(String artWork) {
        this.artWork = artWork;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
