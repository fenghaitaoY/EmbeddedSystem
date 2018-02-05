package com.android.blue.smarthomefunc.service;

import android.content.Context;
import android.media.AudioManager;

/**
 * onAudioFocusChange():
 * AUDIOFOCUS_GAIN：获得了Audio Focus；
 * AUDIOFOCUS_LOSS：失去了Audio Focus，并将会持续很长的时间。这里因为可能会停掉很长时间，所以不仅仅要停止Audio的播放，最好直接释放掉Media资源。而因为停止播放Audio的时间会很长，如果程序因为这个原因而失去AudioFocus，最好不要让它再次自动获得AudioFocus而继续播放，不然突然冒出来的声音会让用户感觉莫名其妙，感受很不好。这里直接放弃AudioFocus，当然也不用再侦听远程播放控制【如下面代码的处理】。要再次播放，除非用户再在界面上点击开始播放，才重新初始化Media，进行播放。
 * AUDIOFOCUS_LOSS_TRANSIENT：暂时失去Audio Focus，并会很快再次获得。必须停止Audio的播放，但是因为可能会很快再次获得AudioFocus，这里可以不释放Media资源；
 * AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK：暂时失去AudioFocus，但是可以继续播放，不过要在降低音量。
 * Created by root on 1/22/18.
 */

public class AudioFocusManager implements AudioManager.OnAudioFocusChangeListener {
    private PlayService mPlayservice;
    private AudioManager mAudioManager;
    private boolean isPausedByFocusLossTransient;
    private int mVolumeWhenFocusLossTransientCanDuck;

    public AudioFocusManager(PlayService playService){
        mPlayservice = playService;
        mAudioManager = (AudioManager) playService.getSystemService(Context.AUDIO_SERVICE);
    }

    public boolean requestAudioFocus(){
        return mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
                == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    public void abandonAudioFocus(){
        mAudioManager.abandonAudioFocus(this);
    }
    @Override
    public void onAudioFocusChange(int focusChange) {
        int volume;
        switch (focusChange){
            case AudioManager.AUDIOFOCUS_GAIN://重新获得焦点
                if (!willPlay() && isPausedByFocusLossTransient){
                    //通话结束，或者其他应用播放结束
                    mPlayservice.playPause();
                }

                volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                if (mVolumeWhenFocusLossTransientCanDuck > 0 && volume == mVolumeWhenFocusLossTransientCanDuck/2){
                    //恢复音量
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mVolumeWhenFocusLossTransientCanDuck, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                }

                isPausedByFocusLossTransient = false;
                mVolumeWhenFocusLossTransientCanDuck = 0;
                break;
            case AudioManager.AUDIOFOCUS_LOSS: //永久丢失焦点，　被其他播放器占用
                if (willPlay()){
                    forceStop();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT://短暂丢失焦点,如来电
                if (willPlay()){
                    forceStop();
                    isPausedByFocusLossTransient = true;
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK: //有通知提示音
                //音量减弱
                volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                if (willPlay() && volume > 0){
                    mVolumeWhenFocusLossTransientCanDuck = volume;
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mVolumeWhenFocusLossTransientCanDuck/2,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                }
                break;
        }
    }

    /**
     * 当前是在播放，将做停止
     */
    private void forceStop() {
        if (mPlayservice.isPreparing()){
            mPlayservice.stop();
        }else if (mPlayservice.isPlaying()){
            mPlayservice.pause();
        }
    }

    /**
     * 判断当前是否正在播放
     * @return
     */
    private boolean willPlay() {
        boolean result = false;
        result = mPlayservice.isPreparing() || mPlayservice.isPlaying();
        return result;
    }


}
