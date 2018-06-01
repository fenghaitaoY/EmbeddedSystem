package com.android.blue.smarthomefunc.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.model.HomepageSlideInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.List;

/**
 * Created by fht on 5/29/18.
 */

public class HomepageSlideLooperUtils {

    private  Handler mHandler = new UpdateHandler();

    private Context mContext;

    int looperCount = 0;
    int imageCount=0;
    boolean looperFlag = false;

    List<HomepageSlideInfo> mListSlides;
    TextView one;
    TextView two;
    TextView thr;
    ImageView iv;
    PostRunnabe mPostRunnable;

    class UpdateHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    updateLooper();

                    break;
            }
            LogUtils.i(" looper handler post delay");
            mHandler.postDelayed(mPostRunnable, 5000);
        }

    }

    class PostRunnabe implements Runnable{
        @Override
        public void run() {
            Message msg = Message.obtain();
            msg.what = 1;
            mHandler.sendMessage(msg);
        }
    }

    public HomepageSlideLooperUtils(Context context){
        mContext = context;
        mPostRunnable = new PostRunnabe();
    }

    /**
     * 更新执行的逻辑
     */
    private void updateLooper(){
        if (one == null || two == null || thr == null || iv == null){
            //累计
            if (looperCount < mListSlides.size()-1) {
                looperCount++;
            }else{
                looperCount = 0;
            }
            return;
        }
        LogUtils.i(" looperCount ="+looperCount);

        if (looperCount == 0){
            one.setBackgroundColor(mContext.getResources().getColor(R.color.white_96p));
            two.setBackgroundColor(mContext.getResources().getColor(R.color.black_trans));
            thr.setBackgroundColor(mContext.getResources().getColor(R.color.black_trans));
            one.setTextColor(Color.RED);
            two.setTextColor(mContext.getResources().getColor(R.color.black));
            thr.setTextColor(mContext.getResources().getColor(R.color.black));
        }else if (looperCount == 1){
            one.setBackgroundColor(mContext.getResources().getColor(R.color.black_trans));
            two.setBackgroundColor(mContext.getResources().getColor(R.color.white_96p));
            thr.setBackgroundColor(mContext.getResources().getColor(R.color.black_trans));
            two.setTextColor(Color.RED);
            one.setTextColor(mContext.getResources().getColor(R.color.black));
            thr.setTextColor(mContext.getResources().getColor(R.color.black));
        }else if (looperCount == 2){
            one.setBackgroundColor(mContext.getResources().getColor(R.color.black_trans));
            two.setBackgroundColor(mContext.getResources().getColor(R.color.black_trans));
            thr.setBackgroundColor(mContext.getResources().getColor(R.color.white_96p));
            thr.setTextColor(Color.RED);
            two.setTextColor(mContext.getResources().getColor(R.color.black));
            one.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        imageCount = looperCount;

        Glide.with(mContext).load(mListSlides.get(looperCount).getVideoImage())
                .asBitmap()
                .placeholder(R.drawable.default_video)
                .error(R.drawable.default_video)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        iv.setImageBitmap(resource);
                    }
                });

        //累计
        if (looperCount < mListSlides.size()-1) {
            looperCount++;
        }else{
            looperCount = 0;
        }
    }

    /**
     * 点击选择更新
     * @param clickCount
     */
    public void clickUpdateLooper(int clickCount){
        looperCount = clickCount;
        mHandler.removeCallbacks(mPostRunnable);
        Message msg = Message.obtain();
        msg.what = 1;
        mHandler.sendMessage(msg);


    }

    /**
     * 返回当前展示的number
     * @return
     */
    public int getClickSlideItemNum(){
        return imageCount;
    }

    /**
     * 自动循环更新
     * @param slideInfos
     * @param one
     * @param two
     * @param three
     * @param imageView
     */
    public void startLoop(List<HomepageSlideInfo> slideInfos, TextView one, TextView two, TextView three, final ImageView imageView){
        LogUtils.i("");
        if (looperFlag){
            return;
        }
        looperFlag = true;

        if (slideInfos != null && slideInfos.size() > 0){
            for (int i=0;i<slideInfos.size();i++){
                if (i == 0 &&one != null) {
                    one.setText(slideInfos.get(i).getVideoName());
                    this.one = one;
                }
                if (i == 1 && two != null) {
                    two.setText(slideInfos.get(i).getVideoName());
                    this.two = two;
                }
                if (i == 2 && three != null){
                    three.setText(slideInfos.get(i).getVideoName());
                    thr = three;
                }
            }
            mListSlides = slideInfos;
            if (imageView!= null)
                iv = imageView;

            //初始显示
            looperCount = 0;
            Message msg = Message.obtain();
            msg.what = 1;
            mHandler.sendMessage(msg);
        }
    }


    /**
     * 当不可见时,停止更新
     */
    public void stopLoop(){
        LogUtils.i("");
        looperFlag = false;
        mHandler.removeCallbacks(mPostRunnable);
    }
}
