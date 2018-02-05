package com.android.blue.smarthomefunc.executor;

import com.android.blue.smarthomefunc.entity.LogUtils;

/**
 * 下载音乐信息类
 * Created by root on 1/25/18.
 */

public class DownloadMusicInfo {
    private String title;
    private String musicPath;
    private String coverPath;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMusicPath() {
        return musicPath;
    }

    public void setMusicPath(String musicPath) {
        this.musicPath = musicPath;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public DownloadMusicInfo(String title, String musicPath, String coverPath){
        this.title = title;
        this.musicPath = musicPath;
        this.coverPath = coverPath;
        LogUtils.i("DonwnloadMusicInfo title:"+title+" , musicpath:"+musicPath+" , coverPath:"+coverPath);
    }

}
