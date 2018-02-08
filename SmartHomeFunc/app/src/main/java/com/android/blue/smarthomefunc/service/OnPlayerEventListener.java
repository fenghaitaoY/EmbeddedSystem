package com.android.blue.smarthomefunc.service;

import com.android.blue.smarthomefunc.model.Music;

/**
 * Created by root on 1/22/18.
 */

public interface OnPlayerEventListener {
    /**
     * 切换歌曲
     * @param music
     */
    void onChange(Music music);

    /**
     * 播放
     */
    void onPlayerStart();

    /**
     * 暂停
     */
    void onPlayerPause();

    /**
     * 更新进度
     * @param progress
     */
    void onPublishProgress(int progress);

    /**
     * 缓冲百分比
     * @param percent
     */
    void onBufferingUpdate(int percent);

    /**
     * 更新定时停止播放时间
     * @param remain
     */
    void onTimer(long remain);

    void onMusicListUpdate();
}
