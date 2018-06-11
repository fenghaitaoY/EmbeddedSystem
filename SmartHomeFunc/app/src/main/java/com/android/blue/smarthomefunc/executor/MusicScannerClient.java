package com.android.blue.smarthomefunc.executor;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

import com.android.blue.smarthomefunc.entity.LogUtils;

import java.io.File;

/**
 * 更新媒体库, kk版本之后使用广播的形式更新不能够实现,顾使用系统媒体扫描类实现
 * Created by fht on 6/11/18.
 */

public class MusicScannerClient implements MediaScannerConnection.MediaScannerConnectionClient {
    private MediaScannerConnection mMediaScanner;
    private File mFile;

    public MusicScannerClient(Context context, File file){
        mFile = file;
        mMediaScanner = new MediaScannerConnection(context, this);
        mMediaScanner.connect();
    }


    @Override
    public void onMediaScannerConnected() {
        LogUtils.i(""+mFile.getAbsolutePath());
        mMediaScanner.scanFile(mFile.getAbsolutePath(), null);

    }

    @Override
    public void onScanCompleted(String s, Uri uri) {
        LogUtils.i("");
        mMediaScanner.disconnect();
    }
}
