package com.example.dell.musicplayer.models;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Dell on 5/16/2018.
 */

public class SongInfoModel implements Serializable {
    int Id;
    long albumId;
    String songName;
    String songArtist;
    String songTime;
    String songComposer;
    String songMoviename;
    String songPath;
    String songImgPath;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public String getSongTime() {
        return songTime;
    }

    public void setSongTime(String songTime) {
        this.songTime = songTime;
    }

    public String getSongComposer() {
        return songComposer;
    }

    public void setSongComposer(String songComposer) {
        this.songComposer = songComposer;
    }

    public String getSongMoviename() {
        return songMoviename;
    }

    public void setSongMoviename(String songMoviename) {
        this.songMoviename = songMoviename;
    }

    public String getSongPath() {
        return songPath;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

    public String getSongImgPath() {
        return songImgPath;
    }

    public void setSongImgPath(String songImgPath) {
        this.songImgPath = songImgPath;
    }


}
