package com.android.blue.smarthomefunc.service;


import android.os.Handler;
import android.text.format.DateUtils;

import com.android.blue.smarthomefunc.application.AppCache;
import com.android.blue.smarthomefunc.entity.LogUtils;

/**
 * Created by root on 1/26/18.
 */

public class QuitTimer {
    private PlayService mPlayService;
    private EventCallback<Long> mTimerCallback;
    private Handler mHandler;
    private long mTimerRemain;

    private static QuitTimer INSTANCE;

    public static QuitTimer getInstance(){
        if (INSTANCE == null){
            synchronized (QuitTimer.class){
                if (INSTANCE == null){
                    INSTANCE = new QuitTimer();
                }
            }
        }
        return INSTANCE;
    }

    private QuitTimer(){}

    public  void init(PlayService playService, Handler handler, EventCallback<Long> timerCallback){
        mPlayService = playService;
        mHandler = handler;
        mTimerCallback = timerCallback;
    }

    public void start(long milli){
        stop();
        LogUtils.i("QuitTimer start milli ="+milli);
        if (milli > 0){
            mTimerRemain = milli+ DateUtils.SECOND_IN_MILLIS;
            mHandler.post(mQuitRunnable);
        }else{
            mTimerRemain = 0;
            mTimerCallback.onEvent(mTimerRemain);
        }
    }

    private Runnable mQuitRunnable = new Runnable() {
        @Override
        public void run() {
            LogUtils.i(" QuitRunnable mTimerRemain ="+mTimerRemain);
            mTimerRemain -= DateUtils.SECOND_IN_MILLIS; //减一秒
            if (mTimerRemain > 0){
                mTimerCallback.onEvent(mTimerRemain);
                mHandler.postDelayed(mQuitRunnable, DateUtils.SECOND_IN_MILLIS);
            }else{
                mPlayService.quit();
            }

        }
    };

    private void stop() {
        mHandler.removeCallbacks(mQuitRunnable);
    }
}
