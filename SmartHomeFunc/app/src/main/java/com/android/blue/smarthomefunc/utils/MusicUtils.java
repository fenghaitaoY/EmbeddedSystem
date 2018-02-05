package com.android.blue.smarthomefunc.utils;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.model.Music;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索本地歌曲工具类
 * Created by root on 1/24/18.
 */

public class MusicUtils {
    private static final String SELECTION = MediaStore.Audio.AudioColumns.SIZE + " >= ? AND "+MediaStore.Audio.AudioColumns.DURATION+" >= ?";

    /**
     * 扫描本地歌曲
     * 利用contentprovider query歌曲文件
     * @param context
     * @return
     */
    public static List<Music> scanMusic(Context context){
        List<Music> musicList = new ArrayList<>();

        long filterSize = Long.valueOf(Preferences.getFilterSize()) * 1024;
        long filterTime = Long.valueOf(Preferences.getFilterTime()) * 1000;

        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        BaseColumns._ID,
                        MediaStore.Audio.AudioColumns.IS_MUSIC,
                        MediaStore.Audio.AudioColumns.TITLE,
                        MediaStore.Audio.AudioColumns.ARTIST,
                        MediaStore.Audio.AudioColumns.ALBUM,
                        MediaStore.Audio.AudioColumns.ALBUM_ID,
                        MediaStore.Audio.AudioColumns.DATA,
                        MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                        MediaStore.Audio.AudioColumns.SIZE,
                        MediaStore.Audio.AudioColumns.DURATION
                }, SELECTION,
                new String[]{String.valueOf(filterSize),String.valueOf(filterTime)},
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor == null){
            return musicList;
        }

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC));
            if (isMusic == 0){
                continue;
            }
            //小于６０s的不加入音乐
            if (cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION)) < 60*1000){
                continue;
            }

            long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM));
            long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
            String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME));
            long fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.SIZE));

            LogUtils.i("id ="+id+" , title="+title+" , artist ="+artist+" , album="+album+" ,albumId="+albumId+" , duration="+duration
                    +", path＝"+path+" , fileName="+fileName+" , fileSize="+fileSize);
            Music music = new Music();
            music.setId(id);
            music.setType(Music.Type.LOCAL);
            music.setTitle(title);
            music.setArtist(artist);
            music.setAlbum(album);
            music.setAlbumId(albumId);
            music.setDuration(duration);
            music.setPath(path);
            music.setFileName(fileName);
            music.setFileSize(fileSize);

            musicList.add(music);
        }
        cursor.close();
        return musicList;
    }

    public static Uri getMediaStoreAlbumCoverUri(long albumId){
        Uri artUri = Uri.parse("content://media/external/audio/albumart");
        return ContentUris.withAppendedId(artUri, albumId);
    }

    public static boolean isAudioControlPanelAvailable(Context context){
        return isIntentAvailable(context, new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL));
    }

    private static boolean isIntentAvailable(Context context, Intent intent) {
        return context.getPackageManager().resolveActivity(intent, PackageManager.GET_RESOLVED_FILTER) != null;
    }
}
