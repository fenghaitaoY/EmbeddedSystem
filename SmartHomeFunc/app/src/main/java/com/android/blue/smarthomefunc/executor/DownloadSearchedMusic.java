package com.android.blue.smarthomefunc.executor;

import android.app.Activity;
import android.text.TextUtils;

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

        HttpClient.getMusicDownloadInfo(mSong.getSongid(), new HttpCallback<DownloadInfo>() {
            @Override
            public void onSuccess(DownloadInfo downloadInfo) {
                if (downloadInfo == null || downloadInfo.getBitrate() == null){
                    onFail(null);
                    return;
                }

                downloadMusic(downloadInfo.getBitrate().getFile_link(), artist, title, null);
                onExecuteSuccess(null);
            }

            @Override
            public void onFail(Exception e) {
                onExecuteFail(e);
            }
        });

        String lrcFileName = FileUtils.getLrcFileName(artist, title);
        File lrcFile = new File(FileUtils.getLrcDir()+lrcFileName);
        if (!lrcFile.exists()){
            downloadLrc(mSong.getSongid(), lrcFileName);
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
