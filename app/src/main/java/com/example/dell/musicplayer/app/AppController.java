package com.example.dell.musicplayer.app;

import android.app.Application;
import android.content.Context;

import com.example.dell.musicplayer.activitys.MainActivity;
import com.example.dell.musicplayer.activitys.Mp3PlayerService;
import com.example.dell.musicplayer.activitys.SongPlayerActivity;
import com.example.dell.musicplayer.adapters.SongsAdapter;
import com.example.dell.musicplayer.service.Mp3Receiver;

/**
 * Created by Dell on 6/19/2018.
 */

public class AppController extends Application {
    static Context context;
    static AppController mInstance;
    public MainActivity mainActivity;
    public Mp3PlayerService mp3PlayerService;
    public SongPlayerActivity songPlayerActivity;
    public SongsAdapter songsAdapter;
    public Mp3Receiver mp3Receiver;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        context = this.getApplicationContext();
    }

    public static Context getAppContext() {
        return context;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

}
