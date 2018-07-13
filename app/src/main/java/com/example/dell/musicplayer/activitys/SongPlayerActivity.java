package com.example.dell.musicplayer.activitys;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.dell.musicplayer.R;
import com.example.dell.musicplayer.app.AppController;
import com.example.dell.musicplayer.models.SongInfoModel;
import com.example.dell.musicplayer.utils.BlurBuilder;
import com.example.dell.musicplayer.utils.Utilities;

import java.util.ArrayList;
import java.util.Random;

public class SongPlayerActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    public static MediaPlayer mediaPlayer;
    static android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
    int position;
    //    Uri songUri;
    static ArrayList<SongInfoModel> SongsList = new ArrayList<>();
    static ImageView imvSongImage, imvBackward, imvForward, imvShuffle, imvRepeat;
    static ImageView imvPlayrPause;
    static String MovieName, movieYear, songName, songComposer;
    static TextView tvMovieName, tvSongYear, tvSongName, tvSongComposer;
    static TextView songCurrentDurationLabel;
    static TextView songTotalDurationLabel;
    static SeekBar songSeekBar;
    static Utilities utils;
    static int currentSongIndex = 0;
    // Handler to update UI timer, progress bar etc,.
    static Handler mHandler = new Handler();
    static boolean isShuffle = false;
    static boolean isRepeat = false;
    static Uri uri;
    final boolean[] expanded = new boolean[1];
    LinearLayout l1;
    static Context context;
    static RelativeLayout layoutSongPlayerActivity;


    IntentFilter filter;
    public BroadcastReceiver mNoisyReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction().equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
                    if (SongPlayerActivity.mediaPlayer != null && SongPlayerActivity.mediaPlayer.isPlaying()) {
                        SongPause();
                    }
                } else if (intent.getAction().equals(String.valueOf(AudioManager.AUDIOFOCUS_LOSS_TRANSIENT))) {

                    Toast.makeText(getApplicationContext(), "headset", Toast.LENGTH_SHORT).show();

                    if (SongPlayerActivity.mediaPlayer != null && SongPlayerActivity.mediaPlayer.isPlaying()) {
                        Toast.makeText(getApplicationContext(), "not", Toast.LENGTH_SHORT).show();
                        SongPlay();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };
//    IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_player);
        AppController.getInstance().songPlayerActivity = this;
        initialize();
        SongsList = (ArrayList<SongInfoModel>) getIntent().getSerializableExtra("filterList");
        position = getIntent().getIntExtra("position", 0);
        try {
            filter = new IntentFilter();
            filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
            filter.addAction(String.valueOf(AudioManager.AUDIOFOCUS_LOSS_TRANSIENT));
            registerReceiver(mNoisyReceive, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (SongPlayerActivity.mediaPlayer != null) {
            try {

                SongPlayerActivity.mediaPlayer.stop();
                SongPlayerActivity.mediaPlayer.release();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        currentSongIndex = position;
        playSong(position);
    }


    public void initialize() {
        l1 = (LinearLayout) findViewById(R.id.l1);
        tvMovieName = (TextView) findViewById(R.id.tvMovieName);
        imvSongImage = (ImageView) findViewById(R.id.imvSongImage);
        imvBackward = (ImageView) findViewById(R.id.imvBackward);
        imvForward = (ImageView) findViewById(R.id.imvForward);
        imvShuffle = (ImageView) findViewById(R.id.imvShuffle);
        imvPlayrPause = (ImageView) findViewById(R.id.imvPlayrPause);
        imvRepeat = (ImageView) findViewById(R.id.imvRepeat);
        tvSongName = (TextView) findViewById(R.id.tvSongName);
        tvSongComposer = (TextView) findViewById(R.id.tvSongArtist);
        tvSongYear = (TextView) findViewById(R.id.tvSongYear);
        songCurrentDurationLabel = (TextView) findViewById(R.id.tvCurrentTime);
        songTotalDurationLabel = (TextView) findViewById(R.id.tvTotalTime);
        songSeekBar = (SeekBar) findViewById(R.id.songSeekBar);
        layoutSongPlayerActivity = (RelativeLayout) findViewById(R.id.layoutSongPlayerActivity);
        imvPlayrPause.setOnClickListener(this);
        imvBackward.setOnClickListener(this);
        imvForward.setOnClickListener(this);
        imvShuffle.setOnClickListener(this);
        imvRepeat.setOnClickListener(this);
        imvSongImage.setOnClickListener(this);
        this.context = this;
        utils = new Utilities();
//         Listeners
        songSeekBar.setOnSeekBarChangeListener(this); // Important
//        mp.setOnCompletionListener(this); // Important

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuSetting) {
//            startActivity(new Intent(MainActivity.this, SettingActivity.class));
            return true;
        }
//        if (id == R.id.menuLogout) {
//            Toast.makeText(getApplicationContext(), "Log Out", Toast.LENGTH_SHORT).show();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


    public static void playSong(final int songIndex) {

        try {

            uri = Uri.parse(SongsList.get(songIndex).getSongPath().toString());
            SongPlayerActivity.mediaPlayer = MediaPlayer.create(context.getApplicationContext(), uri);
            songDetails(SongsList.get(songIndex).getSongPath());
            SongPlayerActivity.mediaPlayer.start();
            // set Progress bar values
            songSeekBar.setProgress(0);
            songSeekBar.setMax(100);
            // Updating progress bar
            updateProgressBar();

//Call Service background play
            Intent intent = new Intent(context.getApplicationContext(), Mp3PlayerService.class);
            intent.putExtra("songTitle", "songName");
            intent.putExtra("songPath", SongsList.get(songIndex).getSongPath());
            context.startService(intent);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e2) {
            e2.printStackTrace();
        } catch (Exception e4) {
//            mp.reset();
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
//
                if (isRepeat) {
                    // repeat is on play same song again
                    playSong(currentSongIndex);
                } else if (isShuffle) {
                    // shuffle is on - play a random song
                    Random rand = new Random();
                    currentSongIndex = rand.nextInt((SongsList.size() - 1) - 0 + 1) + 0;
                    playSong(currentSongIndex);
                } else {
                    // no repeat or shuffle ON - play next song
                    if (currentSongIndex < (SongsList.size() - 1)) {
                        playSong(currentSongIndex + 1);
                        currentSongIndex = currentSongIndex + 1;
                    } else {
                        // play first song
                        playSong(0);
                        currentSongIndex = 0;
                    }
                }
            }
        });


//            imvPlayrPause.setImageResource(R.drawable.ic_pause_circle_filled_white_24dp);


    }


    public static void songDetails(String songFile) {
        try {
            mmr.setDataSource(songFile);
            MovieName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            movieYear = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);
            songName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            songComposer = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER);
            if (MovieName != null)
                tvMovieName.setText("" + MovieName);
            if (songName != null)
                tvSongName.setText("" + songName);
            if (movieYear != null)
                tvSongYear.setText("" + movieYear);
            if (songComposer != null)
                tvSongComposer.setText("" + songComposer);
            byte[] data = mmr.getEmbeddedPicture();
            if (data != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                imvSongImage.setImageBitmap(bitmap);
                imvSongImage.setAdjustViewBounds(true);
                BlurBuilder bler = new BlurBuilder();
                layoutSongPlayerActivity.setBackground(new BitmapDrawable(bler.blur(SongPlayerActivity.context, bitmap)));

            } else {
                imvSongImage.setImageResource(R.drawable.defaultmusicimg);
            }


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context.getApplicationContext(), "" + e, Toast.LENGTH_LONG);
        }
    }


//    public static void songDetail(int songIndex) {
//        try {
//
//
//            if (SongsList.get(songIndex).getSongImgPath() != null) {
//                imvSongImage.setImageURI(Uri.parse("file://" + SongsList.get(songIndex).getSongImgPath()));
//                imvSongImage.setAdjustViewBounds(true);
//
//
//            } else {
//                imvSongImage.setImageResource(R.drawable.defaultmusicimg);
//            }
//
//            tvMovieName.setText(SongsList.get(songIndex).getSongMoviename());
//            tvSongName.setText(SongsList.get(songIndex).getSongName());
//            tvSongComposer.setText(SongsList.get(songIndex).getSongComposer());
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    //
    public static void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    //    /**
//     * Background Runnable thread
//     */
    static Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            try {

                long totalDuration = (long) SongPlayerActivity.mediaPlayer.getDuration();
                long currentDuration = (long) SongPlayerActivity.mediaPlayer.getCurrentPosition();

                // Displaying Total Duration time
                SongPlayerActivity.songTotalDurationLabel.setText("" + SongPlayerActivity.utils.milliSecondsToTimer(totalDuration));

                // Displaying time completed playing
                SongPlayerActivity.songCurrentDurationLabel.setText("" + SongPlayerActivity.utils.milliSecondsToTimer(currentDuration));

                // Updating progress bar
                int progress = (int) (SongPlayerActivity.utils.getProgressPercentage(currentDuration, totalDuration));

                //Log.d("Progress", ""+progress);
                SongPlayerActivity.songSeekBar.setProgress(progress);

                // Running this thread after 100 milliseconds
                SongPlayerActivity.mHandler.postDelayed(this, 100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    //
//
//    @Override
//    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//
//    }
//
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        SongPlayerActivity.mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        try {

            mHandler.removeCallbacks(mUpdateTimeTask);
            int totalDuration = (int) SongPlayerActivity.mediaPlayer.getDuration();
            int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

            // forward or backward to certain seconds
            SongPlayerActivity.mediaPlayer.seekTo(currentPosition);

            // update timer progress again
            SongPlayerActivity.updateProgressBar();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imvPlayrPause:
                PlayorPause();
                break;
            case R.id.imvBackward:
                songBackward();
                break;
            case R.id.imvForward:
                songForward();
                break;
            case R.id.imvRepeat:
                songRepeat();
                break;
            case R.id.imvShuffle:
                songShuffle();
                break;
            case R.id.imvSongImage:
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    imageViewZoom();
//                }
                break;

        }
    }


    public void PlayorPause() {
        try {

            if (mediaPlayer == null) {
                return;
            }
            Intent intent;
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                imvPlayrPause.setImageResource(R.drawable.ic_play_circle_filled_white_24dp);
                if (!SongsList.isEmpty()) {
                    intent = new Intent(SongPlayerActivity.this.getApplicationContext(), Mp3PlayerService.class);
                    intent.putExtra("songTitle", "Song Title");
                    intent.putExtra("songPath", SongsList.get(currentSongIndex).getSongImgPath());
                    SongPlayerActivity.this.startService(intent);
                }
            } else {
                mediaPlayer.start();
                imvPlayrPause.setImageResource(R.drawable.ic_pause_circle_filled_white_24dp);
                if (!SongsList.isEmpty()) {
                    intent = new Intent(SongPlayerActivity.this.getApplicationContext(), Mp3PlayerService.class);
                    intent.putExtra("songTitle", "Song Title");
                    intent.putExtra("songPath", SongsList.get(currentSongIndex).getSongImgPath());
                    SongPlayerActivity.this.startService(intent);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void SongPlay() {

        try {
            if (mediaPlayer == null) {
                return;
            }
            Intent intent;
            mediaPlayer.start();
            imvPlayrPause.setImageResource(R.drawable.ic_pause_circle_filled_white_24dp);
            if (!SongsList.isEmpty()) {
                intent = new Intent(SongPlayerActivity.this.getApplicationContext(), Mp3PlayerService.class);
                intent.putExtra("songTitle", "Song Title");
                intent.putExtra("songPath", SongsList.get(currentSongIndex).getSongImgPath());
                SongPlayerActivity.this.startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void SongPause() {
        try {
            if (mediaPlayer == null) {
                return;
            }
            Intent intent;
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                imvPlayrPause.setImageResource(R.drawable.ic_play_circle_filled_white_24dp);
                if (!SongsList.isEmpty()) {
                    intent = new Intent(SongPlayerActivity.this.getApplicationContext(), Mp3PlayerService.class);
                    intent.putExtra("songTitle", "Song Title");
                    intent.putExtra("songPath", SongsList.get(currentSongIndex).getSongImgPath());
                    SongPlayerActivity.this.startService(intent);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void songBackward() {
        if (SongPlayerActivity.mediaPlayer == null) {
            return;
        }
        SongPlayerActivity.mediaPlayer.stop();
        SongPlayerActivity.mediaPlayer.release();
        if (currentSongIndex > 0) {
            SongPlayerActivity.this.playSong(SongPlayerActivity.currentSongIndex - 1);
            currentSongIndex = currentSongIndex - 1;
        } else {
            // play last song
            SongPlayerActivity.this.playSong(SongPlayerActivity.SongsList.size() - 1);
            SongPlayerActivity.currentSongIndex = SongPlayerActivity.SongsList.size() - 1;
        }
    }

    public void songForward() {
        if (SongPlayerActivity.mediaPlayer == null) {
            return;
        }

        SongPlayerActivity.mediaPlayer.stop();
        SongPlayerActivity.mediaPlayer.release();

        if (SongPlayerActivity.currentSongIndex < (SongPlayerActivity.SongsList.size() - 1)) {
            SongPlayerActivity.this.playSong(SongPlayerActivity.currentSongIndex + 1);
            SongPlayerActivity.currentSongIndex = SongPlayerActivity.currentSongIndex + 1;
        } else {
            // play first song
            SongPlayerActivity.this.playSong(0);
            SongPlayerActivity.currentSongIndex = 0;
        }

    }

    public void songShuffle() {

        if (isShuffle) {
            isShuffle = false;
            Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
            imvShuffle.setImageResource(R.drawable.ic_shuffle_white_24dp);
        } else {
            // make repeat to true
            isShuffle = true;
            Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
            // make shuffle to false
            isRepeat = false;
            imvShuffle.setImageResource(R.drawable.ic_shuffle_black_24dp);
            imvRepeat.setImageResource(R.drawable.ic_repeat_white_24dp);
        }
    }

    public void songRepeat() {
        if (isRepeat) {
            isRepeat = false;
//            Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
            Toast toast = new Toast(SongPlayerActivity.this);
            LayoutInflater lin = getLayoutInflater();
            View v = lin.inflate(R.layout.layout_customtoast, (ViewGroup) findViewById(R.id.linearCustomToast));
            TextView text = (TextView) v.findViewById(R.id.textToast);
            text.setText("Repeat is OFF");
            toast.setView(v);
            toast.show();
            imvRepeat.setImageResource(R.drawable.ic_repeat_white_24dp);
        } else {
            // make repeat to true
            isRepeat = true;
            Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
            // make shuffle to false
            isShuffle = false;
            imvRepeat.setImageResource(R.drawable.ic_repeat_one_white_24dp);
            imvShuffle.setImageResource(R.drawable.ic_shuffle_white_24dp);
        }
    }

//

    public void goBack(View view) {
        onBackPressed();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    public void imageViewZoom() {
//        expanded[0] = !expanded[0];
//        TransitionManager.beginDelayedTransition(l1, new TransitionSet().addTransition(new ChangeBounds()).addTransition(new ChangeImageTransform()));
//
//        ViewGroup.LayoutParams params = imvSongImage.getLayoutParams();
//        params.height = expanded[0] ? ViewGroup.LayoutParams.MATCH_PARENT :
//                ViewGroup.LayoutParams.WRAP_CONTENT;
////        params.height = expanded[0] ? ViewGroup.LayoutParams.MATCH_PARENT :
////                300;
//
//        imvSongImage.setLayoutParams(params);
//
//        imvSongImage.setScaleType(expanded[0] ? ImageView.ScaleType.CENTER_CROP :
//                ImageView.ScaleType.CENTER);
//    }

    @SuppressLint("WrongConstant")
    public void onBackPressed() {
//        if (mp != null) {
////            Intent i = new Intent();
////            i.setAction("android.intent.action.MAIN");
////            i.addCategory("android.intent.category.HOME");
////            startActivity(i);
//        } else {
        super.onBackPressed();
//        }
        try {
            if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
                ((NotificationManager) this.context.getSystemService("notification")).cancelAll();
                stopService(new Intent(getApplicationContext(), Mp3PlayerService.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }


}
