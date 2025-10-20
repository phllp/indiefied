package com.phllp.indiefied.model;

public class AlbumItem {
    private String title;
    private String cover;      // nome do arquivo local (ex: ok_computer.webp)
    private String artistName; // artista principal (pode vir da primeira faixa)

    public AlbumItem() {}
    public AlbumItem(String title, String cover, String artistName) {
        this.title = title; this.cover = cover; this.artistName = artistName;
    }

    public String getTitle() { return title; }
    public String getCover() { return cover; }
    public String getArtistName() { return artistName; }

    public void setTitle(String s) { this.title = s; }
    public void setCover(String s) { this.cover = s; }
    public void setArtistName(String s) { this.artistName = s; }
}
