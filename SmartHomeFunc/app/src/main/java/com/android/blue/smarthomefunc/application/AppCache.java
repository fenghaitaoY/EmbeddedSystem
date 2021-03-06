package com.android.blue.smarthomefunc.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.LongSparseArray;

import com.android.blue.smarthomefunc.entity.BleDeviceEntity;
import com.android.blue.smarthomefunc.executor.DownloadMusicInfo;
import com.android.blue.smarthomefunc.model.Music;
import com.android.blue.smarthomefunc.model.OnlineMusic;
import com.android.blue.smarthomefunc.model.SearchMusic;
import com.android.blue.smarthomefunc.model.SingerArtistMusic;
import com.android.blue.smarthomefunc.model.SongListInfo;
import com.android.blue.smarthomefunc.service.PlayService;
import com.android.blue.smarthomefunc.utils.MusicCoverLoaderUtils;
import com.android.blue.smarthomefunc.utils.Preferences;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by root on 1/25/18.
 */

public class AppCache {
    private Context mContext;
    private PlayService mPlayService;
    // 本地歌曲列表
    private final List<Music> mMusicList = new ArrayList<>();
    //　在线歌曲
    private final List<OnlineMusic> mOnlineList = new ArrayList<>();
    // 歌单列表
    private final List<SongListInfo> mSongListInfos = new ArrayList<>();
    //　搜索列表
    private final List<SearchMusic.Song> mSearchList = new ArrayList<>();
    //歌手歌曲列表
    private final List<SingerArtistMusic> mSingerArtistMusic = new ArrayList<>();

    private final LongSparseArray<DownloadMusicInfo> mDownloadList = new LongSparseArray<>();

    //蓝牙列表
    private List<BleDeviceEntity> mBleRegistDeviceList = new ArrayList<>();

    private Application mApplication;

    private AppCache() {
    }

    private static class SingletonHolder {
        private static AppCache instance = new AppCache();
    }

    public static AppCache get() {
        return SingletonHolder.instance;
    }

    public void init(Application application) {
        mContext = application.getApplicationContext();
        Preferences.init(mContext);
        mApplication = application;

        //CrashHandler.getInstance().init();
        MusicCoverLoaderUtils.getInstance().init(mContext);
    }

    public Context getContext() {
        return mContext;
    }

    public PlayService getPlayService() {
        return mPlayService;
    }

    public void setPlayService(PlayService service) {
        mPlayService = service;
    }

    public List<Music> getMusicList() {
        return mMusicList;
    }

    public List<OnlineMusic> getOnlineMusicList(){return mOnlineList;};

    public List<SongListInfo> getSongListInfos() {
        return mSongListInfos;
    }

    public List<SearchMusic.Song> getSearchMusicList(){ return mSearchList;}

    public LongSparseArray<DownloadMusicInfo> getDownloadList() {
        return mDownloadList;
    }

    public List<SingerArtistMusic> getSingerArtistMusicList(){return mSingerArtistMusic; }

    public List<BleDeviceEntity> getBleDeviceList(){
        return mBleRegistDeviceList;
    }

    public Application getApplication(){return mApplication;}
}
