package com.phllp.indiefied.model;

public class UserDoc {
    private String name;
    private java.util.List<PlaylistDoc> playlists;
    public UserDoc() {}
    public UserDoc(String name) { this.name = name; this.playlists = new java.util.ArrayList<>(); }
    public String getName() { return name; }
    public java.util.List<PlaylistDoc> getPlaylists() { return playlists; }
    public void setName(String name) { this.name = name; }
    public void setPlaylists(java.util.List<PlaylistDoc> playlists) { this.playlists = playlists; }
}