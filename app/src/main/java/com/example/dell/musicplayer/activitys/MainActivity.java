package com.example.dell.musicplayer.activitys;

import android.content.res.TypedArray;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.example.dell.musicplayer.R;
import com.example.dell.musicplayer.adapters.SongsAdapter;
import com.example.dell.musicplayer.models.SongInfoModel;

import java.util.ArrayList;

import static com.example.dell.musicplayer.activitys.SongPlayerActivity.context;

public class MainActivity extends AppCompatActivity {
    RecyclerView rv;
    android.support.v7.widget.SearchView searchView;
    FloatingActionButton fab;
    ArrayList<SongInfoModel> SongsInfoList = new ArrayList<>();
    SongsAdapter adapter;
    static int id = 0;
    SwipeRefreshLayout swipeRefresh;
    ImageView toolbarImage;
    int imageIndex = 0;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv = (RecyclerView) findViewById(R.id.rv);
        searchView = (SearchView) findViewById(R.id.searchView);
        toolbarImage = (ImageView) findViewById(R.id.toolbarImage);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setColorSchemeResources(R.color.swipe1, R.color.swipe2, R.color.swipe3);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SongsInfoList.clear();
                            getSongsList();
                            swipeRefresh.setRefreshing(false);
                            rv.getRecycledViewPool().clear();
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 3500);

            }


        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageIndex == 1) {
                    imageIndex = 0;
                    loadCollapsingImage(imageIndex);
                } else {
                    loadCollapsingImage(++imageIndex);
                }
            }
        });
        try {
            loadCollapsingImage(imageIndex);
            getSongsList();
            set_animator();
            set_layoutManager();
            set_adapter();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadCollapsingImage(int i) {
        TypedArray array = getResources().obtainTypedArray(R.array.images);
        toolbarImage.setImageDrawable(array.getDrawable(i));
    }


    public void getSongsList() {

        id = 0;
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor c = getApplicationContext().getContentResolver().query(uri, null, MediaStore.Audio.Media.IS_MUSIC + " != 0", null, null);
        id = 0;
        if (c.moveToFirst() != false && c.moveToFirst()) {
            do {
                try {
                    SongInfoModel model = new SongInfoModel();
                    long duration = c.getLong(c.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    model.setId(id);
                    model.setAlbumId(c.getLong(c.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
                    model.setSongPath(c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA)));
                    model.setSongMoviename(c.getString(c.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                    model.setSongArtist(c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                    model.setSongTime(String.valueOf((duration / 60000) + " : " + (duration % 60000 / 1000)));
                    model.setSongImgPath(SongImg(c.getLong(c.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))));
                    model.setSongName(StringFilter(c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE))));
                    model.setSongComposer(StringFilter(c.getString(c.getColumnIndex(MediaStore.Audio.Media.COMPOSER))));
                    SongsInfoList.add(id, model);
                    id++;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext() != false);
            c.close();
        }


    }

    public String StringFilter(String Name) {
        if (Name != null) {
            StringBuilder sb = null;

            boolean flag = false;
            sb = new StringBuilder(Name);

            for (int j = 0; j < sb.length(); j++) {

                if (sb.charAt(j) == '[' || sb.charAt(j) == '-' || sb.charAt(j) == '_' || sb.charAt(j) == '(' || sb.charAt(j) == '&')
                    flag = true;
                if (flag)
                    sb.setCharAt(j, '\0');
            }


            return sb.toString();
        }
        return "Unknown";
    }

    public String SongImg(final long albumId) {
        final String[] path = {null};
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Cursor cursor = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                            new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                            MediaStore.Audio.Albums._ID + "=?",
                            new String[]{String.valueOf(albumId)}, null);

                    if (cursor.moveToFirst() != false) {
                        path[0] = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        return path[0];
    }


    private void set_animator() {
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setChangeDuration(1000);
        rv.setItemAnimator(animator);
    }


    private void set_layoutManager() {
        try {

            rv.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            rv.setHasFixedSize(true);
            rv.setItemViewCacheSize(20);
            rv.setDrawingCacheEnabled(true);
            rv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void set_adapter() {
        try {
            adapter = new SongsAdapter(MainActivity.this, SongsInfoList);
            rv.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

}
