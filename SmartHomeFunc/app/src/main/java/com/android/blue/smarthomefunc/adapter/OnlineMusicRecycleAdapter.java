package com.android.blue.smarthomefunc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.application.AppCache;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.model.OnlineMusic;

import java.util.List;

/**
 * 排行榜榜单
 * Created by fht on 3/15/18.
 */

public class OnlineMusicRecycleAdapter extends RecyclerView.Adapter<OnlineMusicRecycleAdapter.MusicViewHolder>{

    private OnItemClickListener mOnItemClickListener;
    private OnMusicAdapterItemClickListener mMusicItemClickListener;
    private Context mContext;
    private List<OnlineMusic> mData;

    public OnlineMusicRecycleAdapter(Context context, List<OnlineMusic> data){
        mContext = context;
        mData = data;
    }



    @Override
    public MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MusicViewHolder holder = new MusicViewHolder(LayoutInflater.from(mContext).inflate(R.layout.online_music_recycle_item, parent, false));

        return holder;
    }

    @Override
    public void onBindViewHolder(final MusicViewHolder holder, final int position) {
        holder.title.setText(mData.get(position).getTitle());
        holder.artist.setText(mData.get(position).getArtist_name());
        LogUtils.i("position = "+position+", getPlayingPosition ="+AppCache.get().getPlayService().getPlayingPosition());

        if (position == AppCache.get().getPlayService().getPlayingPosition()){
            holder.redLineView.setVisibility(View.VISIBLE);
        }else{
            holder.redLineView.setVisibility(View.INVISIBLE);
        }

        if (mOnItemClickListener != null){
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
        if (mMusicItemClickListener != null){
            holder.addMusic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMusicItemClickListener.onAddPlayingListClick(position);
                }
            });

            holder.detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMusicItemClickListener.onMoreClick(position);
                }
            });
        }
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnMusicAdapterItemClickListener(OnMusicAdapterItemClickListener listener){
        mMusicItemClickListener = listener;
    }
    class MusicViewHolder extends RecyclerView.ViewHolder{
        private View redLineView;
        private ImageButton addMusic;
        private TextView title;
        private TextView artist;
        private ImageButton detail;
        private LinearLayout item;

        public MusicViewHolder(View view){
            super(view);
            redLineView = view.findViewById(R.id.recycle_item_music_red_line);
            addMusic = view.findViewById(R.id.recycle_item_music_add_playing);
            title = view.findViewById(R.id.recycle_item_music_title);
            artist = view.findViewById(R.id.recycle_item_music_artist);
            detail = view.findViewById(R.id.recycle_item_music_ellipsis_detail);
            item = view.findViewById(R.id.recycle_item_music_layout);
        }
    }
}
