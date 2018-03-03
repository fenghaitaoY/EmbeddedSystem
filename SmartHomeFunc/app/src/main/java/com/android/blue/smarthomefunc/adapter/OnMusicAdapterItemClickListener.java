package com.android.blue.smarthomefunc.adapter;

/**
 * 歌曲列表栏按键监听
 * Created by root on 2/7/18.
 */

public interface OnMusicAdapterItemClickListener {
    void onMoreClick(int position);
    void onAddPlayingListClick(int position);
    void onLoverClick(int position);
    void onShareMusicClick(int position);
}
