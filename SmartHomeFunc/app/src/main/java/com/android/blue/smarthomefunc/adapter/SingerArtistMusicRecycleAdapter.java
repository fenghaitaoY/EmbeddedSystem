package com.android.blue.smarthomefunc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.application.AppCache;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.model.Music;
import com.android.blue.smarthomefunc.model.OnlineMusic;
import com.android.blue.smarthomefunc.model.SingerArtistMusic;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 排行榜榜单
 * Created by fht on 3/15/18.
 */

public class SingerArtistMusicRecycleAdapter extends RecyclerView.Adapter<SingerArtistMusicRecycleAdapter.MusicViewHolder>{

    private OnItemClickListener mOnItemClickListener; //歌曲item点击监听
    private OnMusicAdapterItemClickListener mMusicItemClickListener; // 歌曲item 内　add　detail点击回调监听
    private OnMoreItemListener mOnMoreItemListener; //more click

    private Context mContext;
    private int needShowSecFuncPosition = 0;
    private boolean show = false;
    private OnlineMoreGridAdapter adapter;

    private List<SingerArtistMusic> mData;

    private boolean isExist= false;

    public SingerArtistMusicRecycleAdapter(Context context, List<SingerArtistMusic> data){
        mData = data;
        init(context);
    }

    private void init(Context context){
        mContext = context;
        adapter = new OnlineMoreGridAdapter(context, OnlineMoreGridAdapter.ONLINE_TYPE);
    }

    @Override
    public MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogUtils.i("");
        MusicViewHolder holder = new MusicViewHolder(LayoutInflater.from(mContext).inflate(R.layout.online_music_recycle_item, parent, false));

        return holder;
    }

    @Override
    public void onBindViewHolder(final MusicViewHolder holder, final int position) {
        LogUtils.i("start postion="+position);
        holder.title.setText(mData.get(position).getTitle());
        holder.artist.setText(mData.get(position).getAuthor());

        holder.gridView.setAdapter(adapter);

        if (AppCache.get().getPlayService().getPlayingMusic() != null) {
            if (Integer.valueOf(AppCache.get().getSingerArtistMusicList().get(position).getSong_id()) ==
                    AppCache.get().getPlayService().getPlayingMusic().getId()) {
                holder.redLineView.setVisibility(View.VISIBLE);
            } else {
                holder.redLineView.setVisibility(View.INVISIBLE);
            }
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
                    needShowSecFuncPosition = position;
                    if (holder.gridView.getVisibility() == View.GONE){
                        show = true;
                    }else{
                        show = false;
                    }

                    mMusicItemClickListener.onMoreClick(holder.gridView,position);

                }
            });
        }

        holder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                TextView tv = view.findViewById(R.id.grid_item_tv);
                LogUtils.i(" gridview on click"+needShowSecFuncPosition+", tv = "+tv.getText());
                if (mOnMoreItemListener != null) {

                    if (tv.getText().equals(mContext.getString(R.string.music_more_download))) {
                        //下载
                        mOnMoreItemListener.onDownloadOnlineMusic(needShowSecFuncPosition);

                    } else if (tv.getText().equals(mContext.getString(R.string.music_more_share))) {
                        //分享
                        mOnMoreItemListener.onSharedMusicFromMore(needShowSecFuncPosition);

                    } else if (tv.getText().equals(mContext.getString(R.string.music_more_add))) {
                        //添加
                        mOnMoreItemListener.onAddMusicToPlayListFromMore(needShowSecFuncPosition);

                    } else if (tv.getText().equals(mContext.getString(R.string.music_more_info))) {
                        //歌曲信息
                        mOnMoreItemListener.onMusicInfoFromMore(needShowSecFuncPosition);

                    } else if (tv.getText().equals(mContext.getString(R.string.music_more_ring))) {
                        //设为铃声
                        mOnMoreItemListener.onSetMusicToRingFromMore(needShowSecFuncPosition);

                    } else if (tv.getText().equals(mContext.getString(R.string.music_more_delete))) {
                        //删除
                        mOnMoreItemListener.onDeleteMusicFromMore(needShowSecFuncPosition);
                    }
                }
            }
        });

        // 子布局的显示，隐藏　在adapter中刷新不能全部刷新，会导致有些条目不该显示，实际显示，　用onMoreClick
        //　回调　notifyDataSetChanged　更新列表, 用全局变量存储需要显示的item，在更新后显示,解决上述问题
        //判断当前要显示小菜单的歌曲是否本地已经下载
        for (Music localMusic : AppCache.get().getMusicList()){
            if (mData.get(needShowSecFuncPosition).getTitle().equals(localMusic.getTitle()) &&
                    mData.get(needShowSecFuncPosition).getAuthor().equals(localMusic.getArtist())){
                isExist = true;
                break;
            }else{
                isExist = false;
            }
        }


        if (show && position == needShowSecFuncPosition){
            holder.gridView.setVisibility(View.VISIBLE);
            adapter.updateDownloadSatus(isExist);
        }else {
            holder.gridView.setVisibility(View.GONE);
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

    public void setOnMoreItemClickListener(OnMoreItemListener listener){
        mOnMoreItemListener = listener;
    }

    class MusicViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.recycle_item_music_red_line)
        View redLineView;
        @BindView(R.id.recycle_item_music_add_playing)
        ImageButton addMusic;
        @BindView(R.id.recycle_item_music_title)
        TextView title;
        @BindView(R.id.recycle_item_music_artist)
        TextView artist;
        @BindView(R.id.recycle_item_music_ellipsis_detail)
        ImageButton detail;
        @BindView(R.id.recycle_item_music_layout)
        LinearLayout item;
        @BindView(R.id.online_grid_detail)
        GridView gridView;

        public MusicViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
