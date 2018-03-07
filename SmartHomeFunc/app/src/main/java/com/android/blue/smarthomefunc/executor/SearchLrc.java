package com.android.blue.smarthomefunc.executor;

import android.text.TextUtils;

import com.android.blue.smarthomefunc.http.HttpCallback;
import com.android.blue.smarthomefunc.http.HttpClient;
import com.android.blue.smarthomefunc.model.Lrc;
import com.android.blue.smarthomefunc.model.SearchMusic;
import com.android.blue.smarthomefunc.utils.FileUtils;

import java.util.IllegalFormatCodePointException;

/**
 * Created by fht on 3/7/18.
 */

public abstract class SearchLrc implements IExecutor<String> {
    private String artist;
    private String title;

    public SearchLrc(String artist, String title){
        this.artist = artist;
        this.title = title;
    }

    @Override
    public void execute() {
        onPrepare();
        searchLrc();
    }

    private void searchLrc() {
        HttpClient.searchMusic(title + "-" + artist, new HttpCallback<SearchMusic>() {
            @Override
            public void onSuccess(SearchMusic searchMusic) {
                if (searchMusic == null|| searchMusic.getSong() == null || searchMusic.getSong().isEmpty()){
                    onFail(null);
                    return;
                }
                downloadLrc(searchMusic.getSong().get(0).getSongid());
            }

            @Override
            public void onFail(Exception e) {
                onExecuteFail(e);
            }
        });
    }

    private void downloadLrc(String songId){
        HttpClient.getLrc(songId, new HttpCallback<Lrc>() {
            @Override
            public void onSuccess(Lrc lrc) {
                if (lrc == null || TextUtils.isEmpty(lrc.getLrcContent())){
                    onFail(null);
                    return;
                }
                String lrcPath = FileUtils.getLrcDir()+FileUtils.getLrcFileName(artist,title);
                FileUtils.saveLrcFile(lrcPath,lrc.getLrcContent());
                onExecuteSuccess(lrcPath);
            }

            @Override
            public void onFail(Exception e) {
                onExecuteFail(e);
            }
        });
    }
}
