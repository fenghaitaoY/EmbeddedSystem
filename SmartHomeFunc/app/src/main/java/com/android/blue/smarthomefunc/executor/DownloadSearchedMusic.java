package com.android.blue.smarthomefunc.executor;

import android.app.Activity;
import android.text.TextUtils;

import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.http.HttpCallback;
import com.android.blue.smarthomefunc.http.HttpClient;
import com.android.blue.smarthomefunc.model.DownloadInfo;
import com.android.blue.smarthomefunc.model.Lrc;
import com.android.blue.smarthomefunc.model.SearchMusic;
import com.android.blue.smarthomefunc.utils.FileUtils;

import java.io.File;

/**
 * Created by fht on 4/2/18.
 */

public abstract class DownloadSearchedMusic extends DownloadMusic {
    private SearchMusic.Song mSong;

    public DownloadSearchedMusic(Activity activity, SearchMusic.Song song){
        super(activity);
        mSong = song;
    }


    @Override
    protected void download() {
        final String title = mSong.getTitle();
        final String artist = mSong.getArtist_name();
        //下载歌词
        String lrcFileName = FileUtils.getLrcFileName(artist, title);
        File lrcFile = new File(FileUtils.getLrcDir()+lrcFileName);
        if (!lrcFile.exists()){
            downloadLrc(mSong.getSongid(), lrcFileName);
        }

        //下载封面
        final String albumFileName = FileUtils.getAlbumFileName(artist, title);
        final File albumFile = new File(FileUtils.getAlbumDir(), albumFileName);

        //获取歌曲链接
        HttpClient.getMusicDownloadInfo(mSong.getSongid(), new HttpCallback<DownloadInfo>() {
            @Override
            public void onSuccess(DownloadInfo downloadInfo) {
                if (downloadInfo == null || downloadInfo.getBitrate() == null){
                    onFail(null);
                    return;
                }
                LogUtils.i("onSuccess ");
                downloadAlbum(downloadInfo, albumFileName);
                downloadMusic(downloadInfo.getBitrate().getFile_link(), artist, title, albumFile.getPath());
                onExecuteSuccess(null);
            }

            @Override
            public void onFail(Exception e) {
                onExecuteFail(e);
            }
        });

    }

    private void downloadAlbum(DownloadInfo downloadInfo, String albumFileName){
        if (downloadInfo != null) {
            String imageUrl = downloadInfo.getSonginfo().getPic_big();
            if (TextUtils.isEmpty(imageUrl)) {
                imageUrl = downloadInfo.getSonginfo().getPic_small();
            }
            LogUtils.i( " imageUrl " + imageUrl);
            if (!TextUtils.isEmpty(imageUrl)) {
                LogUtils.i("down load album");
                HttpClient.downloadFile(imageUrl, FileUtils.getAlbumDir(), albumFileName, null);
            }
        }
    }

    private void downloadLrc(String songId, final String fileName){
        HttpClient.getLrc(songId, new HttpCallback<Lrc>() {
            @Override
            public void onSuccess(Lrc lrc) {
                if (lrc == null || TextUtils.isEmpty(lrc.getLrcContent())){
                    return;
                }

                String filePath = FileUtils.getLrcDir()+fileName;
                FileUtils.saveLrcFile(filePath, lrc.getLrcContent());
            }

            @Override
            public void onFail(Exception e) {

            }
        });
    }
}
