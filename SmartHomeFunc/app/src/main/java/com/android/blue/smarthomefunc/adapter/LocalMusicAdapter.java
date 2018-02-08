package com.android.blue.smarthomefunc.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.application.AppCache;
import com.android.blue.smarthomefunc.model.Music;
import com.android.blue.smarthomefunc.service.PlayService;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * LocalMusic
 * Created by root on 2/6/18.
 */

public class LocalMusicAdapter extends BaseAdapter {
    private int mPlayingPosition;
    private OnMusicAdapterItemClickListener mListener;


    @Override
    public int getCount() {
        return AppCache.get().getMusicList().size();
    }

    @Override
    public Object getItem(int position) {
        return AppCache.get().getMusicList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.local_music_item_layout, viewGroup, false);
            holder = new ViewHolder();
            holder.add_bt = convertView.findViewById(R.id.add_playing_local);
            holder.title = convertView.findViewById(R.id.local_item_title);
            holder.artist = convertView.findViewById(R.id.local_item_artist);
            holder.ellipsis_detial = convertView.findViewById(R.id.ellipsis_detail);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        if (position == mPlayingPosition){
            //当前正在播放
        }else{

        }
        holder.add_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onAddPlayingListClick(position);
            }
        });
        holder.ellipsis_detial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onMoreClick(position);
            }
        });
        Music music = AppCache.get().getMusicList().get(position);
        holder.title.setText(music.getTitle());
        holder.artist.setText(music.getArtist());


        return convertView;
    }

    public void setOnMusicAdapterItemClickListener(OnMusicAdapterItemClickListener listener){
        mListener = listener;
    }

    public void updatePlayingPosition(PlayService playService){
        if (playService.getPlayingMusic() != null && playService.getPlayingMusic().getType() == Music.Type.LOCAL){
            mPlayingPosition = playService.getPlayingPosition();
        }else{
            mPlayingPosition = -1;
        }
    }

    private static class ViewHolder {
        private ImageButton add_bt;
        private TextView title;
        private TextView artist;
        private ImageButton ellipsis_detial;
    }
}
