package com.example.dell.musicplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class SongPlayer extends AppCompatActivity implements View.OnClickListener {
    static MediaPlayer mp;
    static android.media.MediaMetadataRetriever mmr;
    ArrayList<File> mysong;
    TextView songtext, tt, mname1, composer,author;
    SeekBar seekBar;
    ImageView songimg;
    Thread updateSeekbar;
    int position;
    String mname, composname,authorname;
    String[] m;
    Button play, nxt, pre;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_player);
        songimg = (ImageView) findViewById(R.id.songimg);
        songtext = (TextView) findViewById(R.id.songtext);
        mname1 = (TextView) findViewById(R.id.stext);
        author=(TextView)findViewById(R.id.author);
        composer = (TextView) findViewById(R.id.composer);
        tt = (TextView) findViewById(R.id.tt);
        songtext.setSelected(true);
        play = (Button) findViewById(R.id.play);
        nxt = (Button) findViewById(R.id.nxt);
        pre = (Button) findViewById(R.id.pre);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        mmr = new MediaMetadataRetriever();
        updateSeekbar = new Thread() {
            @Override
            public void run() {
                int totalduration = mp.getDuration(), currentduration = 0;
                while (currentduration < totalduration) {
                    try {
                        sleep(500);
                        currentduration = mp.getCurrentPosition();
                        seekBar.setProgress(currentduration);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        if (mp != null) {
            mp.stop();
            mp.release();
        }
        Intent i = getIntent();
        m = i.getStringArrayExtra("title");
        mysong = (ArrayList) i.getParcelableArrayListExtra("mysong");
        position = i.getIntExtra("pos", 0);
        uri = Uri.parse(mysong.get(position).toString());
        mp = MediaPlayer.create(getApplicationContext(), uri);
        songdetail(mysong, position);
        mp.start();
        songtext.setText(m[position] + " Song   From   Hello-muQic.mp3");
        play.setOnClickListener(this);
        play.setText("!!");
        nxt.setOnClickListener(this);
        nxt.setText(">>");
        pre.setOnClickListener(this);
        pre.setText("<<");
        seekBar.setMax(mp.getDuration());
        updateSeekbar.start();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(seekBar.getProgress());
            }
        });
        tt.setText(" " + (mp.getDuration() / 60000) + ":" + (mp.getDuration() % 60000) / 1000 + "  ");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.play:
                if (mp.isPlaying()) {
                    mp.pause();
                    play.setText(">");
                } else {
                    mp.start();
                    play.setText("!!");
                }
                break;
            case R.id.nxt:
                mp.stop();
                mp.release();
                position = (position + 1) % mysong.size();
                uri = Uri.parse(mysong.get(position).toString());
                mp = MediaPlayer.create(getApplicationContext(), uri);
                songdetail(mysong, position);
                mp.start();
                tt.setText(" " + (mp.getDuration() / 60000) + ":" + (mp.getDuration() % 60000) / 1000 + "  ");
                songtext.setText(m[position] + " Song   From   Hello-muqic.mp3");
                seekBar.setMax(mp.getDuration());
                break;
            case R.id.pre:
                mp.stop();
                mp.release();
                position = (position - 1 < 0) ? mysong.size() - 1 : position - 1;
                uri = Uri.parse(mysong.get(position).toString());
                mp = MediaPlayer.create(getApplicationContext(), uri);
                songdetail(mysong, position);
                mp.start();
                tt.setText(" " + (mp.getDuration() / 60000) + ":" + (mp.getDuration() % 60000) / 1000 + "  ");
                songtext.setText(m[position] + "  Song   From   Hello-muqic.mp3");
                seekBar.setMax(mp.getDuration());
                break;
        }
    }

    private void songdetail(ArrayList<File> songlist, int position1) {
        mmr.setDataSource(songlist.get(position1).getPath());
        mname = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        authorname=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);
        composname = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER);
        byte[] data = mmr.getEmbeddedPicture();
        if (data != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            songimg.setImageBitmap(bitmap);
            songimg.setAdjustViewBounds(true);
            //songimg.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1000));
            mname1.setText("" + mname);
        } else {
            songimg.setImageResource(R.drawable.girl);
            mname1.setText("unknown");
        }
        if (mname != null && composname != null &&authorname!=null ) {
            mname1.setText("" + mname);
            composer.setText("" + composname + " Hits");
            author.setText("Year : "+authorname);
        } else {
            mname1.setText("Unknown");
            composer.setText("Unknown");
            author.setText(" Unknown");
        }

    }
}
