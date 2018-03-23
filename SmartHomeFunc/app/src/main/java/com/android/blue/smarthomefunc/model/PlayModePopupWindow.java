package com.android.blue.smarthomefunc.model;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.utils.Preferences;

/**
 * popupWindow 添加屏幕点击消失，　返回键点击消失功能
 * Created by fht on 3/22/18.
 */

public class PlayModePopupWindow extends PopupWindow {
    private Context mContext;
    private Drawable mBackgroundDrawable;
    private float mShowAlpha = 0.88f;
    //控件点击事件
    private LinearLayout popupQueue;
    private LinearLayout popupRandom;
    private LinearLayout popupOne;

    private ImageView popupRightQueue;
    private ImageView popupRightRandom;
    private ImageView popupRightOne;

    private static int LOOP =0;
    private static int SHUFFLE=1;
    private static int SINGLE = 2;

    private int mode;

    private OnClickChangePlayModeListener listener;

    public PlayModePopupWindow(Context context){
        mContext = context;
        initBasePopupWindow();
    }

    public interface OnClickChangePlayModeListener{
        void OnChangePlayMode(int value);
        void OnViewDismiss();
    }

    public void setOnClickChangePlayMode(OnClickChangePlayModeListener listener){
        this.listener = listener;
    }

    @Override
    public void setOutsideTouchable(boolean touchable) {
        super.setOutsideTouchable(touchable);
        if (touchable){
            if (mBackgroundDrawable == null) {
                mBackgroundDrawable = new ColorDrawable(0x00000000);
            }
            super.setBackgroundDrawable(mBackgroundDrawable);
        }else{
            super.setBackgroundDrawable(null);
        }
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        mBackgroundDrawable = background;
        setOutsideTouchable(isOutsideTouchable());
    }

    /** * 初始化BasePopupWindow的一些信息 * */
    private void initBasePopupWindow() {
        setAnimationStyle(android.R.style.Animation_Dialog);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(true);
    }

    public Context getContext(){
        return mContext;
    }

    @Override
    public void setContentView(View contentView) {
        if (contentView != null){
            contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            super.setContentView(contentView);
            popupQueue = contentView.findViewById(R.id.popup_queue);
            popupRandom = contentView.findViewById(R.id.popup_random);
            popupOne = contentView.findViewById(R.id.popup_one);

            popupRightQueue = contentView.findViewById(R.id.popup_queue_iv);
            popupRightRandom = contentView.findViewById(R.id.popup_random_iv);
            popupRightOne = contentView.findViewById(R.id.popup_one_iv);

            addKeyListener(contentView);
            initPlayMode();
        }
    }

    private void initPlayMode(){
        mode = Preferences.getPlayMode();
        if (mode == 0){
            popupRightQueue.setVisibility(View.VISIBLE);
            popupRightRandom.setVisibility(View.INVISIBLE);
            popupRightOne.setVisibility(View.INVISIBLE);
        }else if (mode == 1){
            popupRightQueue.setVisibility(View.INVISIBLE);
            popupRightRandom.setVisibility(View.VISIBLE);
            popupRightOne.setVisibility(View.INVISIBLE);
        }else if (mode == 2){
            popupRightQueue.setVisibility(View.INVISIBLE);
            popupRightRandom.setVisibility(View.INVISIBLE);
            popupRightOne.setVisibility(View.VISIBLE);
        }

    }



    /**
     * 为窗体添加outside点击事件
     * @param view
     */
    private void addKeyListener(View view) {
        if(view != null){
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                    LogUtils.i("keyCode = "+keyCode);
                    switch (keyCode){
                        case KeyEvent.KEYCODE_BACK:
                            dismiss();
                            return true;
                    }
                    return false;
                }
            });
            popupQueue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LogUtils.i("popup queue");
                    Preferences.savePlayMode(LOOP);
                    initPlayMode();
                    listener.OnChangePlayMode(LOOP);
                }
            });

            popupRandom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LogUtils.i("popup random");
                    Preferences.savePlayMode(SHUFFLE);
                    initPlayMode();
                    listener.OnChangePlayMode(SHUFFLE);
                }
            });

            popupOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LogUtils.i("popup one");
                    Preferences.savePlayMode(SINGLE);
                    initPlayMode();
                    listener.OnChangePlayMode(SHUFFLE);
                }
            });
        }

    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        showAnimator().start();
    }

    @Override
    public void showAsDropDown(View anchor) {
        super.showAsDropDown(anchor);
        showAnimator().start();
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        super.showAsDropDown(anchor, xoff, yoff);
        showAnimator().start();
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        super.showAsDropDown(anchor, xoff, yoff, gravity);
        showAnimator().start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        dismissAnimator().start();
        if (listener != null){
            listener.OnViewDismiss();
        }
    }

    /**
     *  窗口显示，窗口背景透明度渐变动画
     *
     */
    private ValueAnimator showAnimator(){
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, mShowAlpha);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
            }
        });
        animator.setDuration(360);
        return animator;
    }

    /**
     * 窗口隐藏，　背景透明度渐变动画
     *
     */
    private ValueAnimator dismissAnimator(){
        ValueAnimator animator = ValueAnimator.ofFloat(mShowAlpha,1.0f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
            }
        });
        animator.setDuration(320);
        return animator;
    }


}

