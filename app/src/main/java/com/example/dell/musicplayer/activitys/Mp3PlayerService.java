package com.example.dell.musicplayer.activitys;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.dell.musicplayer.R;
import com.example.dell.musicplayer.app.AppController;
import com.example.dell.musicplayer.utils.Utilities;


/**
 * Created by Dell on 4/24/2018.
 */

public class Mp3PlayerService extends Service implements AudioManager.OnAudioFocusChangeListener {
    Handler mHandler = new Handler();
    String message = "";
    private MediaPlayer mp;
    private Notification notification;
    private NotificationManager notificationMgr;
    private RemoteViews notificationView;
    int notificatoinID = 12893565;
    String path = "";
    boolean test = false;
    static int currentSongIndex = 0;
    Uri uri;
    AudioManager mAudioManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private class AudioPlayerBroadcastReceiver extends BroadcastReceiver {
        private AudioPlayerBroadcastReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equalsIgnoreCase("ACTION_PLAY")) {
                try {
                    if (Mp3PlayerService.this.mp == null) {
                        return;
                    }
                    if (Mp3PlayerService.this.mp.isPlaying()) {
                        Mp3PlayerService.this.mp.pause();
                        SongPlayerActivity.imvPlayrPause.setImageResource(R.drawable.ic_play_circle_filled_white_24dp);
                        Mp3PlayerService.this.displayNotificationMessage();
                        return;
                    }
                    Mp3PlayerService.this.mp.start();
                    SongPlayerActivity.imvPlayrPause.setImageResource(R.drawable.ic_pause_circle_filled_white_24dp);
                    Mp3PlayerService.this.displayNotificationMessage();
                } catch (Exception e) {
                }
            } else if (action.equalsIgnoreCase("ACTION_NEXT")) {
                if (Mp3PlayerService.this.mp != null) {
                    currentSongIndex = SongPlayerActivity.currentSongIndex;
                    if (currentSongIndex < SongPlayerActivity.SongsList.size() - 1) {
                        currentSongIndex++;
                        Mp3PlayerService.this.playSong(currentSongIndex);
                        SongPlayerActivity.currentSongIndex = currentSongIndex;
                        return;
                    }
                    Mp3PlayerService.this.playSong(0);
                    SongPlayerActivity.currentSongIndex = 0;
                }
            } else if (action.equalsIgnoreCase("ACTION_PREVIOUS") && Mp3PlayerService.this.mp != null) {
                currentSongIndex = SongPlayerActivity.currentSongIndex;
                int size = SongPlayerActivity.SongsList.size();
                if (currentSongIndex > 0) {
                    currentSongIndex--;
                    Mp3PlayerService.this.playSong(currentSongIndex);
                    SongPlayerActivity.currentSongIndex = currentSongIndex;
                    return;
                }
                currentSongIndex = size - 1;
                Mp3PlayerService.this.playSong(currentSongIndex);
                SongPlayerActivity.currentSongIndex = currentSongIndex;
            }
        }


    }


    class ServiceThread extends Thread {
        ServiceThread() {
        }

        public void run() {
        }
    }

    public void onCreate() {
        super.onCreate();
    }

    @SuppressLint("WrongConstant")
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.notificationMgr = (NotificationManager) getSystemService("notification");
        this.mp = SongPlayerActivity.mediaPlayer;
        this.message = intent.getExtras().getString("songTitle");
        this.path = intent.getExtras().getString("songPath");
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        displayNotificationMessage();
        return 2;
//        return START_STICKY;
    }


    @SuppressLint({"WrongConstant", "ResourceType"})
    private void displayNotificationMessage() {
        long timeNotification = System.currentTimeMillis() * 2;
        this.notification = new Notification(R.drawable.defaultmusicimg, this.message, System.currentTimeMillis());
        this.notification.priority = Notification.PRIORITY_MAX;
        this.notification.when = timeNotification;
        this.notificationView = new RemoteViews(getPackageName(), R.layout.notification_mediacontroller);
        if (this.mp != null) {
            if (this.mp.isPlaying()) {
                this.notificationView.setImageViewResource(R.id.btnPlay_notification, R.drawable.ic_pause_circle_filled_white_24dp);
            } else {
                this.notificationView.setImageViewResource(R.id.btnPlay_notification, R.drawable.ic_play_arrow_white_24dp);
            }
        }
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(this.path);

        } catch (Exception e) {
        }
        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap = null;
        if (art != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPurgeable = true;
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length, options);
        }
        if (bitmap == null) {
            bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.defaultmusicimg)).getBitmap();
        }
        Resources res = getResources();
        this.notificationView.setImageViewBitmap(R.id.imageView_notification, Bitmap.createScaledBitmap(bitmap, (int) res.getDimension(17104901), (int) res.getDimension(17104902), true));
        this.notificationView.setTextViewText(R.id.songTitle_notification, this.message);
        Utilities utils = new Utilities();
        if (this.mp != null) {
            this.notificationView.setTextViewText(R.id.totalTimes_notification, new StringBuilder(String.valueOf(utils.milliSecondsToTimer((long) this.mp.getDuration()))).toString());
        }
        this.notification.contentView = this.notificationView;
        Intent intent = new Intent(getApplicationContext(), SongPlayerActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
        taskStackBuilder.addParentStack(SongPlayerActivity.class);
        taskStackBuilder.addNextIntent(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        intent.putExtra("filterList", SongPlayerActivity.FilterList);
//        intent.putExtra("songList", SongPlayerActivity.songList);
//        intent.putExtra("position", SongPlayerActivity.currentSongIndex);
//        intent.putExtra("Duration", mp.getDuration());
//        intent.putExtra("CurrentPosition", mp.getCurrentPosition());

//        intent.putExtra("check", true);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.notification.contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = this.notification;
        notification.flags |= 32;

        if (!this.test) {
            AudioPlayerBroadcastReceiver broadcastReceiver = new AudioPlayerBroadcastReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addCategory("android.intent.category.DEFAULT");
            intentFilter.addAction("ACTION_PLAY");
            intentFilter.addAction("ACTION_NEXT");
            intentFilter.addAction("ACTION_PREVIOUS");
            registerReceiver(broadcastReceiver, intentFilter);
            this.test = true;
        }
        this.notificationView.setOnClickPendingIntent(R.id.btnPlay_notification, PendingIntent.getBroadcast(this, 100, new Intent("ACTION_PLAY"), 0));
        this.notificationView.setOnClickPendingIntent(R.id.btnNext_notification, PendingIntent.getBroadcast(this, 100, new Intent("ACTION_NEXT"), 0));
        this.notificationView.setOnClickPendingIntent(R.id.btnPrevious_notification, PendingIntent.getBroadcast(this, 100, new Intent("ACTION_PREVIOUS"), 0));
        this.notificationMgr.notify(this.notificatoinID, this.notification);

    }

    public void onDestroy() {
        super.onDestroy();
        this.notificationMgr.cancel(this.notificatoinID);
        if (this.mp != null) {
            this.mp.release();
        }
    }


    public void playSong(int songIndex) {
        try {
//            this.uri = Uri.parse(SongPlayerActivity.songList.get(songIndex).getPath().toString());
//            this.mp = MediaPlayer.create(getApplicationContext(), uri);
            this.mp.reset();
            this.mp.setDataSource(SongPlayerActivity.SongsList.get(songIndex).getSongPath().toString());
            this.mp.prepare();
            this.mp.start();
            SongPlayerActivity.songDetails(SongPlayerActivity.SongsList.get(songIndex).getSongPath().toString());
            String songTitle = "Song Title";
//            MusicPlayerActivity.songTitleLabel.setText(songTitle);
            this.message = songTitle;
//            this.mp.start();
            SongPlayerActivity.imvPlayrPause.setImageResource(R.drawable.ic_pause_circle_filled_white_24dp);
//            if (!(MusicPlayerActivity.listName == null || MusicPlayerActivity.listName.equals(""))) {
//                MusicPlayerActivity.songListName.setText(MusicPlayerActivity.listName);
//            }
            SongPlayerActivity.songSeekBar.setProgress(0);
            SongPlayerActivity.songSeekBar.setMax(100);
            SongPlayerActivity.updateProgressBar();
            this.path = SongPlayerActivity.SongsList.get(songIndex).getSongPath().toString();
            displayNotificationMessage();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e2) {
            e2.printStackTrace();
        } catch (Exception e4) {
            this.mp.reset();
        }
    }


    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange >= 0) {
            //LOSS -> PAUSE
        } else {
            //GAIN -> PLAY
//            Toast.makeText(getApplicationContext(),""+focusChange,Toast.LENGTH_SHORT).show();
            try {


                if (Mp3PlayerService.this.mp == null) {
                    return;
                }
                if (Mp3PlayerService.this.mp.isPlaying()) {
                    Mp3PlayerService.this.mp.pause();
                    AppController.getInstance().mp3Receiver.callPause = true;
                    SongPlayerActivity.imvPlayrPause.setImageResource(R.drawable.ic_play_circle_filled_white_24dp);
                    Mp3PlayerService.this.displayNotificationMessage();
                    return;
                }
//
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
