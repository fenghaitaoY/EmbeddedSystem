package com.android.blue.smarthomefunc.executor;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.http.HttpCallback;
import com.android.blue.smarthomefunc.http.HttpClient;
import com.android.blue.smarthomefunc.model.DownloadInfo;
import com.android.blue.smarthomefunc.model.Lrc;
import com.android.blue.smarthomefunc.model.Music;
import com.android.blue.smarthomefunc.model.SearchMusic;
import com.android.blue.smarthomefunc.utils.FileUtils;

import java.io.File;

/**
 * 搜索歌曲执行器
 * Created by fht on 3/30/18.
 */

public abstract class PlaySearchMusic extends PlayMusic {
    private SearchMusic.Song mSong;

    public PlaySearchMusic(Activity activity, SearchMusic.Song song) {
        super(activity, 3);
        mSong = song;
    }


    @Override
    protected void getPlayInfo() {
        String lrcFileName = FileUtils.getLrcFileName(mSong.getArtist_name(), mSong.getTitle());
        File lrcFile = new File(FileUtils.getLrcDir()+lrcFileName);

        music = new Music();
        music.setType(Music.Type.ONLINE);
        music.setTitle(mSong.getTitle());
        music.setArtist(mSong.getArtist_name());
        music.setId(Integer.valueOf(mSong.getSongid()));


        //下载歌词
        if (!lrcFile.exists()){
            downloadLrc(lrcFile.getPath());
        }else {
            mCounter++;
        }
        //获取歌曲播放链接
        HttpClient.getMusicDownloadInfo(mSong.getSongid(), new HttpCallback<DownloadInfo>() {
            @Override
            public void onSuccess(DownloadInfo downloadInfo) {
                if( downloadInfo == null || downloadInfo.getBitrate() == null){
                    onFail(null);
                    return;
                }
                LogUtils.i("title = "+downloadInfo.getSonginfo().getTitle()
                +" , pic big ="+downloadInfo.getSonginfo().getPic_big());
                music.setPath(downloadInfo.getBitrate().getFile_link());
                music.setDuration(downloadInfo.getBitrate().getFile_duration() * 1000);
                String albumFileName = FileUtils.getAlbumFileName(mSong.getArtist_name(), mSong.getTitle());
                File albumFile = new File(FileUtils.getAlbumDir()+albumFileName);
                downloadAlbum(downloadInfo.getSonginfo().getPic_big(), albumFileName);
                music.setCoverPath(albumFile.getPath());

                checkCounter();
            }

            @Override
            public void onFail(Exception e) {
                onExecuteFail(e);
            }
        });
    }

    private void downloadAlbum(String pic, String fileName){
        HttpClient.downloadFile(pic, FileUtils.getAlbumDir(), fileName, new HttpCallback<File>() {
            @Override
            public void onSuccess(File file) {

            }

            @Override
            public void onFail(Exception e) {

            }

            @Override
            public void onFinish() {
                checkCounter();
            }
        });
    }

    private void downloadLrc(final String filePath){
        HttpClient.getLrc(mSong.getSongid(), new HttpCallback<Lrc>() {
            @Override
            public void onSuccess(Lrc lrc) {
                if (lrc != null && !TextUtils.isEmpty(lrc.getLrcContent())){
                    FileUtils.saveLrcFile(filePath,lrc.getLrcContent());
                }
            }

            @Override
            public void onFail(Exception e) {

            }

            @Override
            public void onFinish() {
                checkCounter();
            }
        });
    }
}
