package com.phllp.indiefied.model;

public class PlaylistDoc {
    private String name;
    private java.util.List<String> tracks;
    public PlaylistDoc() {}
    public PlaylistDoc(String name) { this.name = name; this.tracks = new java.util.ArrayList<>(); }
    public String getName() { return name; }
    public java.util.List<String> getTracks() { return tracks; }
    public void setName(String name) { this.name = name; }
    public void setTracks(java.util.List<String> tracks) { this.tracks = tracks; }
}