package com.android.blue.smarthomefunc.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.android.blue.smarthomefunc.application.AppCache;
import com.android.blue.smarthomefunc.entity.Actions;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.enums.PlayModeEnum;
import com.android.blue.smarthomefunc.model.Music;
import com.android.blue.smarthomefunc.receiver.NoisyAudioStreamReceiver;
import com.android.blue.smarthomefunc.utils.MusicUtils;
import com.android.blue.smarthomefunc.utils.Preferences;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class PlayService extends Service implements MediaPlayer.OnCompletionListener{
    private static final long TIME_UPDATE = 300L;
    private static final int  STATE_IDLE = 0;
    private static final int  STATE_PREPARING = 1;
    private static final int  STATE_PLAYING = 2;
    private static final int  STATE_PAUSE = 3;

    private final NoisyAudioStreamReceiver mNoisyReceiver = new NoisyAudioStreamReceiver();
    private final IntentFilter mNoisyFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private final Handler mHandler = new Handler();
    //媒体控制
    private MediaPlayer mPlayer = new MediaPlayer();
    private AudioFocusManager mAudioFocusManager;
    private MediaSessionManager mMediaSessionManager;
    private OnPlayerEventListener mListener;

    //正在播放的歌曲
    private Music mPlayingMusic;
    //正在播放本地歌曲的序号
    private int mPlayingPosition = -1;
    private int mPlayState = STATE_IDLE;


    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i("onCreate :"+getClass().getSimpleName());
        mAudioFocusManager = new AudioFocusManager(this);
        mMediaSessionManager = new MediaSessionManager(this);
        //设置播放完成监听
        mPlayer.setOnCompletionListener(this);


    }

    @Override
    public IBinder onBind(Intent intent) {
        return new PlayBinder();
    }

    public static void startCommand(Context context, String action){
        Intent intentservice = new Intent(context, PlayService.class);
        intentservice.setAction(action);
        context.startService(intentservice);
    }

    /**
     * startService 启动service　回调
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null){
            switch (intent.getAction()){
                case Actions.ACTION_MEDIA_PLAY_PAUSE:
                    playPause();
                    break;
                case Actions.ACTION_MEDIA_NEXT:
                    next();
                    break;
                case Actions.ACTION_MEDIA_PREVIOUS:
                    prev();
                    break;
                default:
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        //播放完一首歌曲，播放下一曲
        next();
    }

    /**
     * 扫描本地音乐,因为扫描是比较耗时操作，最好放在非ＵＩ线程操作
     * @param callback
     */
    @SuppressLint("StaticFieldLeak")
    public void updateMusicList(final EventCallback<Void> callback){
        new AsyncTask<Void, Void, List<Music>>(){
            @Override
            protected List<Music> doInBackground(Void... voids) { //后台操作,搜索本地音乐
                return MusicUtils.scanMusic(PlayService.this);
            }

            @Override
            protected void onPostExecute(List<Music> musics) { //UI更新
                AppCache.get().getMusicList().clear();
                AppCache.get().getMusicList().addAll(musics);

                if (!AppCache.get().getMusicList().isEmpty()){
                    updatePalyingPosition();
                    mPlayingMusic = AppCache.get().getMusicList().get(mPlayingPosition);
                }
                if (mListener != null){
                    mListener.onMusicListUpdate();
                }
                if (callback != null){
                    callback.onEvent(null);
                }
            }
        }.execute();
    }

    public void play(int position){
        if (AppCache.get().getMusicList().isEmpty()){
            return;
        }

        if (position < 0){
            position = AppCache.get().getMusicList().size() -1;
        }else if (position >= AppCache.get().getMusicList().size()){
            position = 0;
        }
        mPlayingPosition = position;
        Music music = AppCache.get().getMusicList().get(mPlayingPosition);

        Preferences.saveCurrentSongId(music.getId());
        play(music);
    }

    /**
     * 播放开始准备
     * @param music
     */
    public void play(Music music){
        mPlayingMusic = music;
        try {
            mPlayer.reset();
            mPlayer.setDataSource(music.getPath());
            mPlayer.prepareAsync();
            mPlayState = STATE_PREPARING;
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    if (isPreparing()){
                        start();
                    }
                }
            });
            mPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
                    if (mListener != null){
                        mListener.onBufferingUpdate(percent);
                    }
                }
            });
            //通知
            mMediaSessionManager.updateMetaData(mPlayingMusic);
            mMediaSessionManager.updatePlaybackState();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 上一曲
     */
    public void prev(){
        if (AppCache.get().getMusicList().isEmpty()){
            return;
        }

        PlayModeEnum mode = PlayModeEnum.valueOf(Preferences.getPlayMode());
        switch (mode){
            case SHUFFLE:
                mPlayingPosition = new Random().nextInt(AppCache.get().getMusicList().size());
                play(mPlayingPosition);;
                break;
            case SINGLE:
                play(mPlayingPosition);
                break;
            case LOOP:
            default:
                play(mPlayingPosition - 1);
                break;
        }
    }

    /**
     * 下一曲
     */
    public void next(){
        if (AppCache.get().getMusicList().isEmpty()){
            return;
        }
        PlayModeEnum mode = PlayModeEnum.valueOf(Preferences.getPlayMode());
        switch (mode) {
            case SHUFFLE:
                mPlayingPosition = new Random().nextInt(AppCache.get().getMusicList().size());
                play(mPlayingPosition);
                break;
            case SINGLE:
                play(mPlayingPosition);
                break;
            case LOOP:
            default:
                play(mPlayingPosition + 1);
                break;
        }
    }

    public void playPause(){
        if (isPreparing()){
            LogUtils.i(" playPause stop");
            stop();
        }else if (isPlaying()){
            LogUtils.i(" playPause pause");
            pause();
        }else if (isPausing()){
            LogUtils.i(" playPause start");
            start();
        }else{
            LogUtils.i(" playPause play");
            play(getPlayingPosition());
        }
    }

    /**
     * 播放开始
     */
    protected void start(){
        if (!isPreparing() && isPausing()){
            return;
        }

        if (mAudioFocusManager.requestAudioFocus()){
            mPlayer.start();
            mPlayState = STATE_PLAYING;
            mHandler.post(mPublishRunnable);
            mMediaSessionManager.updatePlaybackState();
            registerReceiver(mNoisyReceiver, mNoisyFilter);

            if (mListener != null){
                mListener.onPlayerStart();
            }
        }
    }

    public boolean isPausing(){
        return mPlayState == STATE_PAUSE;
    }
    public boolean isPlaying(){
        return mPlayState == STATE_PLAYING;
    }

    public boolean isPreparing(){
        return mPlayState == STATE_PREPARING;
    }

    /**
     * 跳转到指定时间
     * @param msec
     */
    public void seekTo(int msec){
        if (isPlaying() || isPausing()){
            mPlayer.seekTo(msec);
            mMediaSessionManager.updatePlaybackState();
            if (mListener !=null){
                mListener.onPublish(msec);
            }
        }
    }

    /**
     * 播放停止
     */
    public void stop(){
        if (isIdle()){
            return;
        }
        pause();
        mPlayer.reset();
        mPlayState = STATE_IDLE;
    }

    /**
     * 暂停
     */
    public void pause(){
        if (!isPlaying()){
            return;
        }
        mPlayer.pause();
        mPlayState = STATE_PAUSE;
        mHandler.removeCallbacks(mPublishRunnable);
        mMediaSessionManager.updatePlaybackState();
        unregisterReceiver(mNoisyReceiver);

        if (mListener != null){
            mListener.onPlayerPause();
        }
    }

    public boolean isIdle(){
        return mPlayState == STATE_IDLE;
    }

    /**
     * 得到正在播放本地歌曲的序号
     * @return
     */
    public int getPlayingPosition(){
        return mPlayingPosition;
    }

    /**
     * 获取正在播放的歌曲
     * @return
     */
    public Music getmPlayingMusic(){
        return mPlayingMusic;
    }

    /**
     * 删除或下载后，刷新正在播放的本地歌曲的序号
     */
    public void updatePalyingPosition(){
        int position = 0;
        long id = Preferences.getCurrentSongId();
        for (int i =0; i< AppCache.get().getMusicList().size();i++){
            if (AppCache.get().getMusicList().get(i).getId() == id){
                position = i;
                break;
            }
        }
        mPlayingPosition = position;
        Preferences.saveCurrentSongId(AppCache.get().getMusicList().get(mPlayingPosition).getId());
    }

    public int getAudioSessionId(){
        return  mPlayer.getAudioSessionId();
    }


    public long getCurrentPosition(){
        if (isPlaying() || isPausing()){
            return mPlayer.getCurrentPosition();
        }else {
            return 0;
        }
    }

    private Runnable mPublishRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlaying() && mListener != null){
                mListener.onPublish(mPlayer.getCurrentPosition());
            }
            mHandler.postDelayed(this, TIME_UPDATE);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayer.reset();
        mPlayer.release();
        mPlayer = null;
        mAudioFocusManager.abandonAudioFocus();
        mMediaSessionManager.release();
        AppCache.get().setPlayService(null);
        LogUtils.i("PlayService onDestroy");
    }

    public void quit(){
        stop();
        stopSelf();
    }

    /**
     * 监听设置
     * @return
     */
    public OnPlayerEventListener getOnPlayerEventListener(){
        return mListener;
    }

    public void setOnPlayerEventListener(OnPlayerEventListener listener){
        mListener = listener;
    }


    public class PlayBinder extends Binder{
        public PlayService getService(){
            return PlayService.this;
        }
    }
}
