package com.phllp.indiefied.model;

public class Track {
    private final String id;
    private final String title;
    private final String artist;
    private final String coverUrl;

    public Track(String id, String title, String artist, String coverUrl) {
        this.id = id; this.title = title; this.artist = artist; this.coverUrl = coverUrl;
    }
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getCoverUrl() { return coverUrl; }
}
