package com.android.blue.smarthomefunc.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.application.AppCache;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.model.Music;
import com.android.blue.smarthomefunc.service.PlayService;
import com.android.blue.smarthomefunc.utils.MusicCoverLoaderUtils;

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
    public View getView(final int position, View view, final ViewGroup viewGroup) {
        LogUtils.i("LocalmusicAdapter getView position = "+position+" , mPlayingPosition="+mPlayingPosition);
        ViewHolder holder;
        View convertView = view;
        if (convertView == null){
            convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.local_music_item_layout, viewGroup, false);
            holder = new ViewHolder();
            holder.add_bt = convertView.findViewById(R.id.add_playing_local);
            holder.title = convertView.findViewById(R.id.local_item_title);
            holder.artist = convertView.findViewById(R.id.local_item_artist);
            holder.ellipsis_detial = convertView.findViewById(R.id.ellipsis_detail);
            holder.mDefault = convertView.findViewById(R.id.item_music_empty_layout);
            holder.mSelect = convertView.findViewById(R.id.item_music_select_layout);

            holder.cover = convertView.findViewById(R.id.local_item_cover);
            holder.childTitle = convertView.findViewById(R.id.local_item_child_title);
            holder.heart = convertView.findViewById(R.id.local_item_heart);
            holder.share = convertView.findViewById(R.id.local_item_share);
            holder.download = convertView.findViewById(R.id.local_item_down);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        if (position == mPlayingPosition){
            //当前正在播放
            holder.mSelect.setVisibility(View.VISIBLE);
            holder.mDefault.setVisibility(View.GONE);

            holder.cover.setImageBitmap(MusicCoverLoaderUtils.getInstance().loadThumbnail(AppCache.get().getMusicList().get(position)));
            holder.childTitle.setText(AppCache.get().getMusicList().get(position).getTitle());
        }else{
            holder.mSelect.setVisibility(View.GONE);
            holder.mDefault.setVisibility(View.VISIBLE);
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


        holder.heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onLoverClick(position);
            }
        });

        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onShareMusicClick(position);
            }
        });

        Music music = AppCache.get().getMusicList().get(position);
        holder.title.setText(music.getTitle());
        holder.artist.setText(music.getArtist());

        holder.mSelect.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                LogUtils.i("onSystemuiView  i="+i);
            }
        });
        if (holder.mSelect.isFocused()){
            LogUtils.i("width="+holder.cover.getWidth());
        }
        if (holder.mSelect.getVisibility() == View.VISIBLE){
            LogUtils.i(" 2 width="+holder.cover.getWidth());
        }


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
        private ImageView cover;
        private TextView childTitle;
        private ImageView heart;
        private ImageView share;
        private ImageView download;

        RelativeLayout mDefault;
        LinearLayout mSelect;
    }
}
