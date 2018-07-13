package com.example.dell.musicplayer.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.example.dell.musicplayer.activitys.SongPlayerActivity;
import com.example.dell.musicplayer.app.AppController;

/**
 * Created by Dell on 6/24/2018.
 */

public class Mp3Receiver extends BroadcastReceiver {
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    public static boolean callPause = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            AppController.getInstance().mp3Receiver = this;
            if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                if (AppController.getInstance().songPlayerActivity.mediaPlayer != null && SongPlayerActivity.mediaPlayer.isPlaying()) {
                    AppController.getInstance().songPlayerActivity.SongPause();
                    callPause = true;
                }

            } else {
                String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
                String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                int state = 0;
                if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    state = TelephonyManager.CALL_STATE_IDLE;
                    if (AppController.getInstance().songPlayerActivity.mediaPlayer != null && callPause == true) {
                        AppController.getInstance().songPlayerActivity.SongPlay();
                        callPause = false;
                    }

                } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    state = TelephonyManager.CALL_STATE_OFFHOOK;
                    if (AppController.getInstance().songPlayerActivity.mediaPlayer != null && SongPlayerActivity.mediaPlayer.isPlaying()) {
                        callPause = true;
                        AppController.getInstance().songPlayerActivity.SongPause();
                    }

                } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

                    if (AppController.getInstance().songPlayerActivity.mediaPlayer != null && SongPlayerActivity.mediaPlayer.isPlaying()) {
                        callPause = true;
                        AppController.getInstance().songPlayerActivity.SongPause();
                    }
                    state = TelephonyManager.CALL_STATE_RINGING;
                }
// else if (intent.getAction().equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
//                    if (AppController.getInstance().songPlayerActivity.mediaPlayer != null && SongPlayerActivity.mediaPlayer.isPlaying()) {
//                        AppController.getInstance().songPlayerActivity.SongPause();
//                    }
//                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
