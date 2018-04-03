package com.android.blue.smarthomefunc.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ListView 更换为RecyclerView
 *
 * LocalMusic
 * Created by root on 2/6/18.
 */

public class LocalMusicAdapter extends BaseAdapter {
    private int mPlayingPosition;
    private OnMusicAdapterItemClickListener mListener;

    private OnMoreItemListener mOnMoreItemListener; //more click
    private OnlineMoreGridAdapter adapter;
    private int needShowSecFuncPosition;
    private boolean show = false;
    private Context mContext;

    public LocalMusicAdapter(Context context){
        mContext = context;
        adapter = new OnlineMoreGridAdapter(context, OnlineMoreGridAdapter.LOCAL_TYPE);
    }

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
        final ViewHolder holder;
        View convertView = view;
        if (convertView == null){
            convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.local_music_item_layout, viewGroup, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.gridView.setAdapter(adapter);

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
                LogUtils.i(" onmore click");
                needShowSecFuncPosition = position;
                if (holder.gridView.getVisibility() == View.GONE){
                    show = true;
                }else{
                    show = false;
                }

                mListener.onMoreClick(view, position);
            }
        });
        holder.ellipsis_child_detial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtils.i(" child onmore click");
                needShowSecFuncPosition = position;
                if (holder.gridView.getVisibility() == View.GONE){
                    show = true;
                }else{
                    show = false;
                }

                mListener.onMoreClick(view, position);
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
                        show = false; //解决删除歌曲，　more 菜单仍显示问题
                    }
                }
            }
        });

        Music music = AppCache.get().getMusicList().get(position);
        holder.title.setText(music.getTitle());
        holder.artist.setText(music.getArtist());

        // 子布局的显示，隐藏　在adapter中刷新不能全部刷新，会导致有些条目不该显示，实际显示，　用onMoreClick
        //　回调　notifyDataSetChanged　更新列表, 用全局变量存储需要显示的item，在更新后显示,解决上述问题
        LogUtils.i(" show = "+show+"position ="+needShowSecFuncPosition+" getview position ="+position);
        if (show && position == needShowSecFuncPosition){
            LogUtils.i("显示");
            holder.gridView.setVisibility(View.VISIBLE);
        }else {
            LogUtils.i("隐藏");
            holder.gridView.setVisibility(View.GONE);
        }


        return convertView;
    }

    public void setOnMusicAdapterItemClickListener(OnMusicAdapterItemClickListener listener){
        mListener = listener;
    }

    public void setOnMoreItemClickListener(OnMoreItemListener listener){
        mOnMoreItemListener = listener;
    }

    public void updatePlayingPosition(PlayService playService){
        if (playService.getPlayingMusic() != null && playService.getPlayingMusic().getType() == Music.Type.LOCAL){
            mPlayingPosition = playService.getPlayingPosition();
        }else{
            mPlayingPosition = -1;
        }
    }

    class ViewHolder {
        @BindView(R.id.add_playing_local)
        ImageButton add_bt;
        @BindView(R.id.local_item_title)
        TextView title;
        @BindView(R.id.local_item_artist)
        TextView artist;
        @BindView(R.id.ellipsis_detail)
        ImageButton ellipsis_detial;
        @BindView(R.id.ellipsis_child_detail)
        ImageButton ellipsis_child_detial;
        @BindView(R.id.local_item_cover)
        ImageView cover;
        @BindView(R.id.local_item_child_title)
        TextView childTitle;
        @BindView(R.id.local_item_heart)
        ImageView heart;
        @BindView(R.id.local_item_share)
        ImageView share;
        @BindView(R.id.local_item_down)
        ImageView download;
        @BindView(R.id.online_grid_detail)
        GridView gridView;
        @BindView(R.id.item_music_empty_layout)
        RelativeLayout mDefault;
        @BindView(R.id.item_music_select_layout)
        LinearLayout mSelect;

        public ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}
