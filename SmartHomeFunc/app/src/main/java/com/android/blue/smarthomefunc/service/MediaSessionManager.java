package com.android.blue.smarthomefunc.service;

import android.media.MediaMetadata;
import android.os.Build;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.android.blue.smarthomefunc.application.AppCache;
import com.android.blue.smarthomefunc.model.Music;
import com.android.blue.smarthomefunc.utils.MusicCoverLoaderUtils;

/**
 * 耳机多媒体按钮监控
 * 管理元数据和播放控件/状态
 * 受控端主要需要实现媒体的播放，这个和之前的没有区别，其次需要创建MediaSession，创建的时候需要设置唯一的session标识符，
 * 这样控制端可以根据该标识符来确定控制的session。然后需要实现的是session的回调方法（SessionCallback），该回调方法中定义了媒体播放的常见操作，
 * play、pause、next、previous等，在创建session时需要把该callback设置到session中，并实现收到回调的处理办法，
 * 比如onPlay的时候就启动本地的播放器进行播放，onPause的时候就暂停。同时需要把播放器的状态通过session设置到控制端，
 * MediaSession提供专门的方法setPlaybackState和setMetadata来更新本地播放器的状态和信息到控制端
 * Created by root on 1/23/18.
 */

public class MediaSessionManager {
    //long 占８个字节　８＊８=64bit　使用按位异或组合action使用
    private static final long MEDIA_SESSION_ACTIONS = PlaybackStateCompat.ACTION_PLAY /*1<<2*/
            | PlaybackStateCompat.ACTION_PAUSE          /*1 << 1*/
            | PlaybackStateCompat.ACTION_PLAY_PAUSE     /*1 << 9*/
            | PlaybackStateCompat.ACTION_SKIP_TO_NEXT   /*1 << */
            | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS/*1 << 4*/
            | PlaybackStateCompat.ACTION_STOP           /*1 << 0*/
            | PlaybackStateCompat.ACTION_SEEK_TO;       /*1 << 8*/

    private PlayService mPlayService;
    private MediaSessionCompat mMediaSession;

    public MediaSessionManager(PlayService playService){
        mPlayService = playService;
        setupMediaSession();
    }

    private void setupMediaSession() {
        mMediaSession = new MediaSessionCompat(mPlayService, "MediaSessionManager");
        //设置FLAG 用途
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS | MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
        //设置MediaSession回调监听,主要用于设置API21+的耳机按钮监听
        mMediaSession.setCallback(mCallback);
        //设置MediaSession启动 (很重要,不启动则无法接受到数据)
        mMediaSession.setActive(true);
    }

    public void updatePlaybackState(){
        int state ;
        if (mPlayService.isPlaying() || mPlayService.isPreparing()){
            state = PlaybackStateCompat.STATE_PLAYING;
        }else{
            state = PlaybackStateCompat.STATE_PAUSED;
        }
        //Updates the current playback state.
        mMediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                .setActions(MEDIA_SESSION_ACTIONS)
                .setState(state,mPlayService.getCurrentPosition(),1)
                .build());
    }

    public void updateMetaData(Music music){
        if (music == null){
            mMediaSession.setMetadata(null);
            return;
        }

        MediaMetadataCompat.Builder metaData = new MediaMetadataCompat.Builder();
        metaData.putString(MediaMetadataCompat.METADATA_KEY_TITLE, music.getTitle());
        metaData.putString(MediaMetadataCompat.METADATA_KEY_ARTIST,music.getArtist());
        metaData.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, music.getAlbum());
        metaData.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, music.getArtist());
        metaData.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, music.getDuration());
        metaData.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, MusicCoverLoaderUtils.getInstance().loadThumbnail(music));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            metaData.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, AppCache.get().getMusicList().size());
        }

        mMediaSession.setMetadata(metaData.build());
    }


    public void release(){
        mMediaSession.setCallback(null);
        mMediaSession.setActive(false);
        mMediaSession.release();
    }

    private MediaSessionCompat.Callback mCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            mPlayService.playPause();
        }

        @Override
        public void onPause() {
            mPlayService.playPause();
        }

        @Override
        public void onSkipToNext() {
            mPlayService.next();
        }

        @Override
        public void onSkipToPrevious() {
            mPlayService.prev();
        }

        @Override
        public void onStop() {
            mPlayService.stop();
        }

        @Override
        public void onSeekTo(long pos) {
            mPlayService.seekTo((int)pos);
        }
    };
}
