package com.android.blue.smarthomefunc.executor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.application.AppCache;
import com.android.blue.smarthomefunc.utils.FileUtils;
import com.android.blue.smarthomefunc.utils.NetworkUtils;
import com.android.blue.smarthomefunc.utils.Preferences;

/**
 * Created by root on 3/2/18.
 */

public abstract class DownloadMusic implements IExecutor<Void> {
    private Activity mActivity;

    public DownloadMusic(Activity activity){
        mActivity = activity;
    }

    @Override
    public void execute() {
        checkNetwork();
    }

    private void checkNetwork() {
        boolean mobileNetworkDownload = Preferences.enableMoblieNetworkDownload();
        if (NetworkUtils.isActiveNetworkMobile(mActivity) && !mobileNetworkDownload){
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle(R.string.tips);
            builder.setMessage(R.string.download_tips);
            builder.setPositiveButton(R.string.download_tips_sure, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    downloadWrapper();
                }
            });
            builder.setNegativeButton(R.string.cancel, null);
            builder.setCancelable(false);
            builder.create().show();
        }else {
            downloadWrapper();
        }
    }

    protected void downloadWrapper(){
        onPrepare();
        download();
    }

    protected abstract void download();

    protected void downloadMusic(String url, String artist, String title, String coverPath){
        try {
            String fileName = FileUtils.getMp3FileName(artist, title);
            Uri uri = Uri.parse(fileName);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle(FileUtils.getFileName(artist, title));
            request.setDescription(mActivity.getString(R.string.downloading));
            request.setDestinationInExternalPublicDir(FileUtils.getRelativeMusicDir(), fileName);
            request.setMimeType(MimeTypeMap.getFileExtensionFromUrl(url));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            request.setAllowedOverRoaming(false); //禁止漫游
            DownloadManager downloadManager = (DownloadManager) AppCache.get().getContext().getSystemService(Context.DOWNLOAD_SERVICE);
            long id = downloadManager.enqueue(request);
            String musicAbsPath = FileUtils.getMusicDir().concat(fileName);
            DownloadMusicInfo downloadMusicInfo = new DownloadMusicInfo(title, musicAbsPath, coverPath);
            AppCache.get().getDownloadList().put(id, downloadMusicInfo);
        }catch (Throwable e){
            e.printStackTrace();
            Toast.makeText(mActivity, mActivity.getString(R.string.download_fail), Toast.LENGTH_SHORT).show();
        }

    }
}
