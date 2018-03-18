package com.android.blue.smarthomefunc.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

/**
 * Created by fht on 3/16/18.
 */

public class ImageViewAnimator {
    private ObjectAnimator coverAnimator;
    /**
     * 初始化动画
     */
    public  void initAnimate(ImageView iv) {
        if (coverAnimator == null) {
            //初始动画
            coverAnimator = ObjectAnimator.ofFloat(iv, "rotation", 0f, 359f);
            LinearInterpolator interpolator = new LinearInterpolator(); //设置匀速旋转
            coverAnimator.setDuration(6000);
            coverAnimator.setInterpolator(interpolator);
            coverAnimator.setRepeatCount(-1);
            coverAnimator.setRepeatMode(ValueAnimator.RESTART);
        }
    }

    /**
     * 播放组合控件　专辑旋转
     */
    public  void startMusicBarCoverAnimate() {
        if (coverAnimator.isPaused()) {
            coverAnimator.resume();
        } else {
            coverAnimator.start();
        }

    }

    /**
     * 停止旋转
     */
    public void stopMusicBarCoverAnimate() {
        coverAnimator.pause();
    }

    /**
     * 取消
     */
    public  void cancelMusicBarCoverAnimate(){
        coverAnimator.cancel();
        coverAnimator = null;
    }

}
