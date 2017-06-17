package com.example.dell.musicplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class SongPlayer extends AppCompatActivity implements View.OnClickListener {
    static MediaPlayer mp;
    ArrayList<File> mysong;
    TextView songtext;
    SeekBar seekBar;
    Thread updateSeekbar;
    int position;
    String[] m;
    Button play, nxt, pre;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_player);
        songtext = (TextView) findViewById(R.id.songtext);
        songtext.setSelected(true);
        play = (Button) findViewById(R.id.play);
        nxt = (Button) findViewById(R.id.nxt);
        pre = (Button) findViewById(R.id.pre);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
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
                //super.run();
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
        mp.start();
        songtext.setText( m[position] + " Song   From   Hello-muQic.mp3");
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
                mp.start();
                songtext.setText("U hearing "+m[position] + " song from Hello-muqic.mp3");
                seekBar.setMax(mp.getDuration());
                break;
            case R.id.pre:
                mp.stop();
                mp.release();
                position = (position - 1 < 0) ? mysong.size() - 1 : position - 1;
                uri = Uri.parse(mysong.get(position).toString());
                mp = MediaPlayer.create(getApplicationContext(), uri);
                mp.start();
                songtext.setText("U hearing "+m[position] + " song from Hello-muqic.mp3");
                seekBar.setMax(mp.getDuration());
                break;
        }
    }
}
