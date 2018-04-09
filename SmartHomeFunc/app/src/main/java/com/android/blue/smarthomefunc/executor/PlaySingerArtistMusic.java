package com.android.blue.smarthomefunc.executor;

import android.app.Activity;
import android.text.TextUtils;

import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.http.HttpCallback;
import com.android.blue.smarthomefunc.http.HttpClient;
import com.android.blue.smarthomefunc.model.DownloadInfo;
import com.android.blue.smarthomefunc.model.Music;
import com.android.blue.smarthomefunc.model.OnlineMusic;
import com.android.blue.smarthomefunc.model.SingerArtistMusic;
import com.android.blue.smarthomefunc.utils.FileUtils;

import java.io.File;

/**
 * Created by root on 3/2/18.
 */

public abstract class PlaySingerArtistMusic extends PlayMusic {

    private SingerArtistMusic mSingerArtistMusic;

    public PlaySingerArtistMusic(Activity activity, SingerArtistMusic singerArtistMusic){
        super(activity, 3);
       mSingerArtistMusic = singerArtistMusic;
    }

    @Override
    protected void getPlayInfo() {
        String artist = mSingerArtistMusic.getAuthor();
        String title = mSingerArtistMusic.getTitle();

        music = new Music();
        music.setType(Music.Type.ONLINE);
        music.setTitle(title);
        music.setArtist(artist);
        music.setId(Integer.valueOf(mSingerArtistMusic.getSong_id()));
        music.setAlbum(mSingerArtistMusic.getAlbum_title());

        //下载歌词
        String lrcFileName = FileUtils.getLrcFileName(artist, title);
        File lrcFile = new File(FileUtils.getLrcDir()+lrcFileName);
        if (!lrcFile.exists() && !TextUtils.isEmpty(mSingerArtistMusic.getLrclink())){
            downloadLrc(mSingerArtistMusic.getLrclink(), lrcFileName);
        }else{
            mCounter++;
        }

        //下载封面
        String albumFileName = FileUtils.getAlbumFileName(artist, title);
        File albumFile = new File(FileUtils.getAlbumDir(), albumFileName);
        String picUrl = mSingerArtistMusic.getPic_big();
        LogUtils.i("downloadAlbum url ="+picUrl);
        if (TextUtils.isEmpty(picUrl)){
            picUrl = mSingerArtistMusic.getPic_s500();
        }

        if (!TextUtils.isEmpty(picUrl)){
            downloadAlbum(picUrl, albumFileName);
        }else{
            mCounter++;
        }
        music.setCoverPath(albumFile.getPath());

        //获取歌曲播放链接
        HttpClient.getMusicDownloadInfo(mSingerArtistMusic.getSong_id(), new HttpCallback<DownloadInfo>() {
            @Override
            public void onSuccess(DownloadInfo downloadInfo) {
                if( downloadInfo == null || downloadInfo.getBitrate() == null){
                    onFail(null);
                    return;
                }

                music.setPath(downloadInfo.getBitrate().getFile_link());
                music.setDuration(downloadInfo.getBitrate().getFile_duration() * 1000);
                LogUtils.i(" file link path = "+music.getPath()+", file duration = "+music.getDuration());
                LogUtils.i("");
                checkCounter();
            }

            @Override
            public void onFail(Exception e) {
                LogUtils.i(" 111111111  ");
                onExecuteFail(e);
            }
        });
    }

    private void downloadLrc(String url, String fileName){
        HttpClient.downloadFile(url, FileUtils.getLrcDir(), fileName, new HttpCallback<File>() {
            @Override
            public void onSuccess(File file) {

            }

            @Override
            public void onFail(Exception e) {

            }

            @Override
            public void onFinish() {
                LogUtils.i("222222");
                checkCounter();
            }
        });
    }

    private void downloadAlbum(String picUrl, String fileName){
        LogUtils.i("downloadAlbum url ="+picUrl);
        HttpClient.downloadFile(picUrl, FileUtils.getAlbumDir(), fileName, new HttpCallback<File>() {
            @Override
            public void onSuccess(File file) {

            }

            @Override
            public void onFail(Exception e) {

            }

            @Override
            public void onFinish() {
                LogUtils.i("33333");
                checkCounter();
            }
        });
    }
}