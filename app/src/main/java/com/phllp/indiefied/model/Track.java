package com.phllp.indiefied.model;

public class Track {
    private String id;
    private String title;
    private int duration;
    private Album album;
    private Artist artist;
    public Track() {} // Firestore precisa do construtor vazio

    public Track(String id, String title, int duration, Album album, Artist artist) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.album = album;
        this.artist = artist;
    }

    // Getters e Setters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public int getDuration() { return duration; }


    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDuration(int duration) { this.duration = duration; }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }
}
