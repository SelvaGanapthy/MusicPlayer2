package com.example.dell.musicplayer.activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Interpolator;
import android.widget.Toast;

import com.example.dell.musicplayer.R;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.RandomTransitionGenerator;
import com.flaviofaria.kenburnsview.Transition;


public class SplashActivity extends AppCompatActivity {

    public static final int requestcode_permisson = 1;
    KenBurnsView kbvspalsh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        kbvspalsh = (KenBurnsView) findViewById(R.id.kbvspalsh);
        kbvspalsh.setTransitionListener(new KenBurnsView.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {

            }
        });
        if (checkPermisson())
            splash();
        else
            requestPermission();
    }


    @Override
    protected void onStart() {
        super.onStart();
        checkPermisson();
    }

    public boolean checkPermisson() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        return (result == PackageManager.PERMISSION_GRANTED);
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestcode_permisson);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case requestcode_permisson:
                if (grantResults.length > 0) {
                    boolean ExStroagePermisson = grantResults[0] == PackageManager.PERMISSION_DENIED;
                    if (ExStroagePermisson) {
                        Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                        requestPermission();
                    } else
                        splash();
                }
                break;
        }
    }


    public void splash() {

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, 4000);

    }


}
