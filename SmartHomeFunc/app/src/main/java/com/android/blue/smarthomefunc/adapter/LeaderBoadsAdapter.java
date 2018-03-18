package com.android.blue.smarthomefunc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.http.HttpCallback;
import com.android.blue.smarthomefunc.http.HttpClient;
import com.android.blue.smarthomefunc.model.OnlineMusic;
import com.android.blue.smarthomefunc.model.OnlineMusicList;
import com.android.blue.smarthomefunc.model.SongListInfo;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by fht on 3/13/18.
 */

public class LeaderBoadsAdapter extends BaseAdapter {

    private static final int TYPE_PROFILE = 0;
    private static final int TYPE_MUSIC_LIST = 1;

    private Context mContext;
    private List<SongListInfo> mSongLists;

    public LeaderBoadsAdapter(List<SongListInfo> data){
        mSongLists = data;
    }

    @Override
    public int getCount() {
        return mSongLists.size();
    }

    @Override
    public Object getItem(int position) {
        return mSongLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        LogUtils.i("-----fht getView---position="+position+" type="+mSongLists.get(position).getType());
        LeaderboadsHolder mHolder;
        mContext = viewGroup.getContext();
        View convertView = view;
        if (convertView == null) {
            convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.online_songlist_item_layout, viewGroup, false);
            mHolder = new LeaderboadsHolder();
            mHolder.mSonglistCover = convertView.findViewById(R.id.song_list_item_cover);
            mHolder.mSonglistMusic1 = convertView.findViewById(R.id.song_list_item_music1);
            mHolder.mSonglistMusic2 = convertView.findViewById(R.id.song_list_item_music2);
            mHolder.mSonglistMusic3 = convertView.findViewById(R.id.song_list_item_music3);

            mHolder.mMusicCount1 = convertView.findViewById(R.id.song_list_item_music1_count);
            mHolder.mMusicCount2 = convertView.findViewById(R.id.song_list_item_music2_count);
            mHolder.mMusicCount3 = convertView.findViewById(R.id.song_list_item_music3_count);
            mHolder.mBillboardName = convertView.findViewById(R.id.song_list_item_billboard_name);

            convertView.setTag(mHolder);

        } else {
            mHolder = (LeaderboadsHolder) convertView.getTag();
        }
        setSongListInfoView(mHolder, mSongLists.get(position));
        return convertView;
    }

    private void setSongListInfoView(final LeaderboadsHolder holder, final SongListInfo info){
        if (info.getCoverUrl() == null){
            LogUtils.i("fht setSongListInfoView info type:"+info.getType()+", title:"+info.getTitle());
            holder.mSonglistCover.setImageResource(R.drawable.default_cover);
            holder.mSonglistMusic1.setText(R.string.online_loading);
            holder.mSonglistMusic2.setText(R.string.online_loading);
            holder.mSonglistMusic3.setText(R.string.online_loading);
            holder.mBillboardName.setText(R.string.null_artist);

            HttpClient.getSongListInfo(info.getType(), 3, 0, new HttpCallback<OnlineMusicList>() {
                @Override
                public void onSuccess(OnlineMusicList onlineMusicList) {
                    parse(onlineMusicList, info);
                    setData(info,holder);
                }

                @Override
                public void onFail(Exception e) {

                }
            });
        }else{
            LogUtils.i("-----fht coverurl ---");
            setData(info,holder);
        }
    }

    private void parse(OnlineMusicList onlineMusicList, SongListInfo songListInfo){
        LogUtils.i("url "+onlineMusicList.getBillboard().getPic_s260()+
                " music1 title:"+ onlineMusicList.getSong_list().get(0).getTitle()+
                " ,album :"+onlineMusicList.getSong_list().get(0).getAlbum_title()+
                ", artist:"+onlineMusicList.getSong_list().get(0).getArtist_name()+
                ", lrclink:"+onlineMusicList.getSong_list().get(0).getLrclink()+
                ", pic big:"+onlineMusicList.getSong_list().get(0).getPic_big()+
                ", pic small:"+onlineMusicList.getSong_list().get(0).getPic_small()+
                ", song id:"+onlineMusicList.getSong_list().get(0).getSong_id()+
                ", ting uid:"+onlineMusicList.getSong_list().get(0).getTing_uid()+
                ", billboard name :"+ onlineMusicList.getBillboard().getName());

        List<OnlineMusic> musics = onlineMusicList.getSong_list();
        songListInfo.setCoverUrl(onlineMusicList.getBillboard().getPic_s640());
        songListInfo.setBillboard(onlineMusicList.getBillboard().getName());
        if (musics.size() >=1){
            songListInfo.setMusic1(mContext.getString(R.string.online_song_list_music, musics.get(0).getTitle()
            ,musics.get(0).getArtist_name()));
        }else{
            songListInfo.setMusic1("");
        }

        if (musics.size() >= 2){
            songListInfo.setMusic2(mContext.getString(R.string.online_song_list_music, musics.get(1).getTitle(),
                    musics.get(1).getArtist_name()));
        }else{
            songListInfo.setMusic2("");
        }

        if (musics.size() >=3){
            songListInfo.setMusic3(mContext.getString(R.string.online_song_list_music, musics.get(2).getTitle(),
                    musics.get(2).getArtist_name()));
        }else{
            songListInfo.setMusic3("");
        }
    }

    private void setData(SongListInfo info, LeaderboadsHolder holder){
        LogUtils.i("count 1 ="+info.getMusic1()+", count 2="+info.getMusic2()+" , count 3 ="+
        info.getMusic3());
        if (info.getMusic1().equals("")){
            holder.mMusicCount1.setVisibility(View.GONE);
        }else{
            holder.mMusicCount1.setVisibility(View.VISIBLE);
        }
        if (info.getMusic2().equals("")){
            holder.mMusicCount2.setVisibility(View.GONE);
        }else{
            holder.mMusicCount2.setVisibility(View.VISIBLE);
        }
        if (info.getMusic3().equals("")){
            holder.mMusicCount3.setVisibility(View.GONE);
        }else {
            holder.mMusicCount3.setVisibility(View.VISIBLE);
        }

        holder.mSonglistMusic1.setText(info.getMusic1());
        holder.mSonglistMusic2.setText(info.getMusic2());
        holder.mSonglistMusic3.setText(info.getMusic3());
        holder.mBillboardName.setText(info.getBillboard());
        Glide.with(mContext).load(info.getCoverUrl())
                .error(R.drawable.default_cover)
                .placeholder(R.drawable.default_cover)
                .into(holder.mSonglistCover);
    }


    private static class LeaderboadsHolder {
        private ImageView mSonglistCover;
        private TextView mSonglistMusic1;
        private TextView mSonglistMusic2;
        private TextView mSonglistMusic3;
        private TextView mMusicCount1;
        private TextView mMusicCount2;
        private TextView mMusicCount3;
        private TextView mBillboardName;

    }
}