package com.example.dell.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 6/13/2017.
 */

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    View row;
    ArrayList<File> mysong = new ArrayList<>();
    String[] title;

    public MyAdapter(String[] title, Context context, ArrayList<File> mysong) {
        this.title = title;
        this.context = context;
        this.mysong = mysong;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        row = LayoutInflater.from(context).inflate(R.layout.listitem, parent, false);
        Item item = new Item(row, context, title, mysong);
        return item;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((Item) holder).t1.setText(title[position]);




    }

    @Override
    public int getItemCount() {
        return title.length;
    }

    public class Item extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView t1;
        Button b1;
        String[] title;
        ArrayList<File> mysong = new ArrayList<>();
        Context context;

        public Item(View itemView, Context context, String[] title, ArrayList<File> mysong) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.mysong = mysong;
            this.context = context;
            this.title = title;
            t1 = (TextView) itemView.findViewById(R.id.text1);
            b1 = (Button) itemView.findViewById(R.id.b1);


        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            this.context.startActivity(new Intent(context.getApplicationContext(), SongPlayer.class).putExtra("pos", position).putExtra("mysong", mysong).putExtra("title", title));
        }
    }
}
