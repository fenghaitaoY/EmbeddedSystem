package com.android.blue.smarthomefunc.adapter;

/**
 * Created by fht on 3/27/18.
 */

public interface OnMoreItemListener {
    void onDownloadOnlineMusic(int position);
    void onSharedMusicFromMore(int position);
    void onAddMusicToPlayListFromMore(int position);
    void onMusicInfoFromMore(int position);
    void onSetMusicToRingFromMore(int position);
    void onDeleteMusicFromMore(int position);
}
