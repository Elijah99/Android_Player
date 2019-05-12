package com.example.android_player;

import java.io.Serializable;
import java.util.Comparator;

public class Audio implements Serializable,
        Comparable<Audio>

{
    private String data;
    private String title;
    private String album;
    private String artist;
    private String added_date;


    public Audio(String data, String title, String album, String artist, String added_date) {
        this.data = data;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.added_date = added_date;

    }
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAddedDate(){return added_date;}

    public void setAdded_date(String added_date){this.added_date = added_date;}

    @Override
    public int compareTo(Audio audio) {
        return title.compareTo(audio.getTitle());
    }

}
