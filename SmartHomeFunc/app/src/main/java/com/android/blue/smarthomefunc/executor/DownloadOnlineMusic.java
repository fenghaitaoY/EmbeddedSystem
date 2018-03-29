package com.android.blue.smarthomefunc.executor;

import android.app.Activity;
import android.text.TextUtils;

import com.android.blue.smarthomefunc.application.AppCache;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.http.HttpCallback;
import com.android.blue.smarthomefunc.http.HttpClient;
import com.android.blue.smarthomefunc.model.DownloadInfo;
import com.android.blue.smarthomefunc.model.OnlineMusic;
import com.android.blue.smarthomefunc.service.EventCallback;
import com.android.blue.smarthomefunc.utils.FileUtils;

import java.io.File;

/**
 * 下载在线音乐
 * Created by fht on 3/27/18.
 */

public abstract class DownloadOnlineMusic extends DownloadMusic {
    private OnlineMusic mOnlineMusic;

    public DownloadOnlineMusic(Activity activity, OnlineMusic onlineMusic){
        super(activity);
        mOnlineMusic = onlineMusic;
    }

    @Override
    protected void download() {
        final String artist = mOnlineMusic.getArtist_name();
        final String title = mOnlineMusic.getTitle();

        //下载歌词
        String lrcFileName = FileUtils.getLrcFileName(artist, title);
        File lrcFile = new File(FileUtils.getLrcDir()+lrcFileName);
        if (!TextUtils.isEmpty(mOnlineMusic.getLrclink()) && !lrcFile.exists()){
            HttpClient.downloadFile(mOnlineMusic.getLrclink(), FileUtils.getLrcDir(), lrcFileName, null);
        }
        LogUtils.i(" lrc file ok");

        //下载封面
        String albumFileName = FileUtils.getAlbumFileName(artist, title);
        final File albumFile = new File(FileUtils.getAlbumDir(), albumFileName);
        String imageUrl = mOnlineMusic.getPic_big();
        if (TextUtils.isEmpty(imageUrl)){
            imageUrl= mOnlineMusic.getPic_small();
        }
        LogUtils.i("albumFile exits ="+albumFile.exists()+" imageUrl "+imageUrl);
        if (!TextUtils.isEmpty(imageUrl)){
            LogUtils.i("down load album");
            HttpClient.downloadFile(imageUrl, FileUtils.getAlbumDir(), albumFileName, null);
        }

        LogUtils.i("album file ok");

        //获取歌曲下载链接
        HttpClient.getMusicDownloadInfo(mOnlineMusic.getSong_id(), new HttpCallback<DownloadInfo>() {
            @Override
            public void onSuccess(DownloadInfo downloadInfo) {
                if (downloadInfo == null || downloadInfo.getBitrate() == null){
                    onFail(null);
                    return;
                }
                downloadMusic(downloadInfo.getBitrate().getFile_link(), artist, title, albumFile.getPath());
                onExecuteSuccess(null);
            }

            @Override
            public void onFail(Exception e) {
                onExecuteFail(e);
            }

        });
    }
}
