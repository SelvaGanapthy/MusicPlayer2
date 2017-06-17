package com.example.dell.musicplayer;

import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    String[] title;
    ArrayList<File> mySongs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},1);

            }
            else {

                ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
        }
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        mySongs = findSongs(Environment.getExternalStorageDirectory());
        title = new String[mySongs.size()];
        for (int i = 0; i < mySongs.size(); i++) {

            title[i] = mySongs.get(i).getName().toString().replace(".mp3", "").replace(".wav", "").replace("-StarMusiQ.Com", "").replace("- TamilTunes.com", "").replace("[Starmusqic.cc]", "").replace("- VmusiQ.Com", "").replace("-VmusiQ.Com", "");
        }
        recyclerView.setAdapter(new MyAdapter(title, this, mySongs));

    }

    public ArrayList<File> findSongs(File root) {
        ArrayList<File> a1 = new ArrayList<File>();
        File[] files = root.listFiles();


        for (File singleFile : files) {
            if (singleFile.isDirectory() && !singleFile.isHidden()) {
                a1.addAll(findSongs(singleFile));
            } else {
                if (singleFile.getName().endsWith(".mp3") || (singleFile.getName().endsWith(".MP3"))||(singleFile.getName().endsWith(".wav"))) {
                    a1.add(singleFile);
                }
            }


        }
        return a1;
    }
}
