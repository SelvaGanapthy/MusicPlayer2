package com.example.dell.musicplayer.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dell.musicplayer.activitys.SongPlayerActivity;
import com.example.dell.musicplayer.anim.AnimationUtils;
import com.example.dell.musicplayer.R;
import com.example.dell.musicplayer.models.SongInfoModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Dell on 6/13/2017.
 */

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {
    Context context;
    View view;
    ArrayList<SongInfoModel> dataList = new ArrayList<>();
    ArrayList<SongInfoModel> filterList = new ArrayList<>();
    ArrayList<SongInfoModel> SongList = new ArrayList<>();

    public SongsAdapter(Context context, ArrayList<SongInfoModel> dataList) {
        this.context = context;
        this.dataList = dataList;
        this.filterList = dataList;
        this.SongList = dataList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.songs_adapter, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        try {
            final SongInfoModel model = filterList.get(position);

            if (model.getSongImgPath() != null) {
                try {
                    Picasso.get()
                            .load(Uri.parse("file://" + model.getSongImgPath()))
//                        .resize(90, 90)
                            .error(R.drawable.defaultmusicimg).into(holder.ivSong);
//                        .noFade().centerCrop()
//.noPlaceholder()
                    ;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                holder.ivSong.setImageResource(R.drawable.defaultmusicimg);
            }


            holder.tvSongName.setText(model.getSongName());
            holder.tvSongTime.setText(model.getSongTime());

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context.getApplicationContext(), SongPlayerActivity.class)
//                        .putExtra("position", model.getId())
                            .putExtra("position", model.getId())
                            .putExtra("filterList", SongList));
                }
            });

            holder.tvArtistName.setText(model.getSongComposer());
            AnimationUtils.animateSunblind(holder, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    filterList = dataList;
                } else {

                    ArrayList<SongInfoModel> filteredList1 = new ArrayList<>();

                    for (SongInfoModel dataModel : dataList) {

                        if (dataModel.getSongName().toLowerCase().contains(charString) || dataModel.getSongMoviename().toLowerCase().contains(charString)) {

                            filteredList1.add(dataModel);
                        }
                    }

                    filterList = filteredList1;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filterList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filterList = (ArrayList<SongInfoModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    @Override
    public int getItemCount() {
        return filterList.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSongName, tvArtistName, tvSongTime;
        CardView cardView;
        ImageView ivSong;
        View lineCode;


        public ViewHolder(View itemView) {
            super(itemView);
            ivSong = (ImageView) itemView.findViewById(R.id.imSong);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            cardView.setBackgroundResource(R.drawable.card_bg);
            lineCode = (View) itemView.findViewById(R.id.lineCode);
            lineCode.setBackgroundColor(Color.TRANSPARENT);
            tvSongName = (TextView) itemView.findViewById(R.id.tvSongName);
            tvArtistName = (TextView) itemView.findViewById(R.id.tvArtistName);
            tvSongTime = (TextView) itemView.findViewById(R.id.tvSongTime);
        }


    }


}
