package com.android.blue.smarthomefunc.adapter;

import android.view.View;

/**
 * 歌曲列表栏按键监听
 * Created by root on 2/7/18.
 */

public interface OnMusicAdapterItemClickListener {
    void onMoreClick(View view, int position);
    void onAddPlayingListClick(int position);
    void onLoverClick(int position);
    void onShareMusicClick(int position);
}
