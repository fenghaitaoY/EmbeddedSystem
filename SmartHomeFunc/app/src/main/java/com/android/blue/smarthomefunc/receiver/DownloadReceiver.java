package com.android.blue.smarthomefunc.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.blue.smarthomefunc.application.AppCache;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.executor.DownloadMusicInfo;
import com.android.blue.smarthomefunc.utils.id3.ID3TagUtils;
import com.android.blue.smarthomefunc.utils.id3.ID3Tags;

import java.io.File;


/**
 * Created by fht on 3/28/18.
 */

public class DownloadReceiver extends BroadcastReceiver {
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onReceive(Context context, Intent intent) {
        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
        DownloadMusicInfo downloadMusicInfo = AppCache.get().getDownloadList().get(id);
        LogUtils.i("  receiver cover path ="+downloadMusicInfo.getCoverPath());
        if (downloadMusicInfo != null){
            Toast.makeText(context, "下载成功", Toast.LENGTH_SHORT).show();

            String musicPath = downloadMusicInfo.getMusicPath();
            String coverPath = downloadMusicInfo.getCoverPath();
            if (!TextUtils.isEmpty(musicPath) && !TextUtils.isEmpty(coverPath)){
                File musicFile = new File(musicPath);
                File coverFile = new File(coverPath);

                if (musicFile.exists() && coverFile.exists()){
                    ID3Tags id3Tags = new ID3Tags.Builder().setCoverFile(coverFile).build();
                    ID3TagUtils.setID3Tags(musicFile, id3Tags, false);
                }
            }

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (AppCache.get().getPlayService() != null){
                        AppCache.get().getPlayService().updateMusicList(null);
                    }
                }
            }, 1000);
        }
    }
}
