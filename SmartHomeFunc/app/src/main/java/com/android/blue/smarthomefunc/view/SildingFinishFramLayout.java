package com.android.blue.smarthomefunc.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.android.blue.smarthomefunc.LogUtils;


/**
 * Created by Administrator on 2016/5/31.
 */
public class SildingFinishFramLayout extends FrameLayout {
    //SildingFinishFramLayout 布局的父布局
    private ViewGroup mParentView;

    /**
     * 滑动的最小距离
     */
    private int mTouchSlop;
    /**
     * 按下点的X坐标
     */
    private int downX;
    /**
     * 按下点的Y坐标
     */
    private int downY;
    /**
     * 临时存储X坐标
     */
    private int tempX;
    /**
     * 滑动类
     */
    private Scroller mScroller;
    /**
     * SildingFinishLayout的宽度
     */
    private int viewWidth;
    /**
     * 记录是否正在滑动
     */
    private boolean isSilding;

    private OnSildingFinishListener onSildingFinishListener;

    private boolean enableLeftSildeEvent = true; //是否开启左侧切换事件
    private boolean enableRightSildeEvent = true; // 是否开启右侧切换事件
    private final int size = 400; //按下时范围(处于这个范围内就启用切换事件，目的是使当用户从左右边界点击时才响应)
    private boolean isIntercept = false; //是否拦截触摸事件
    private boolean canSwitch;//是否可切换
    private boolean isSwitchFromLeft = false; //左侧切换
    private boolean isSwitchFromRight = false; //右侧侧切换
    private boolean isPreeButton = false; //按键位置

    private int buttonWidth;
    private int buttonHeight;

    public SildingFinishFramLayout(Context context) {
        this(context, null);
    }

    public SildingFinishFramLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SildingFinishFramLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        LogUtils.e( "设备的最小滑动距离:" + mTouchSlop);
        mScroller = new Scroller(context);

        int y = (int)getResources().getDisplayMetrics().density *80;
        buttonHeight = getResources().getDisplayMetrics().heightPixels - y;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            // 获取SildingFinishFramLayout所在布局的父布局
            mParentView = (ViewGroup) this.getParent();
            viewWidth = this.getWidth();
        }
        LogUtils.e("viewWidth=" + viewWidth);

    }

    public void setEnableLeftSildeEvent(boolean enableLeftSildeEvent) {
        this.enableLeftSildeEvent = enableLeftSildeEvent;
    }


    public void setEnableRightSildeEvent(boolean enableRightSildeEvent) {
        this.enableRightSildeEvent = enableRightSildeEvent;
    }


    /**
     * 设置OnSildingFinishListener, 在onSildingFinish()方法中finish Activity
     *
     * @param onSildingFinishListener
     */
    public void setOnSildingFinishListener(
            OnSildingFinishListener onSildingFinishListener) {
        this.onSildingFinishListener = onSildingFinishListener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float downX = ev.getRawX();
        float downY = ev.getRawY();
        LogUtils.e("downX =" + downX + ",viewWidth=" + viewWidth);
        LogUtils.e("downY =" + downY + ",viewWidth=" + (buttonHeight-getChildAt(5).getWidth()) + " , height :"+buttonHeight);

        if(downY>buttonHeight-getChildAt(5).getWidth() && downY <buttonHeight){
            LogUtils.i("SildingGinishFramLayout : down button sildingfinish ");
            isPreeButton = true;
        }else{
            isPreeButton = false;
        }
        if(enableLeftSildeEvent && downX < size && !isPreeButton){
            LogUtils.e("downX 在左侧范围内 ,拦截事件");
            isIntercept = true;
            isSwitchFromLeft = true;
            isSwitchFromRight = false;
            return true;
        }else if(enableRightSildeEvent && downX > (viewWidth - size) && !isPreeButton){
            LogUtils.e("downX 在右侧范围内 ,拦截事件");
            isIntercept = true;
            isSwitchFromRight = true;
            isSwitchFromLeft = false;
            return true;
        }else{
            LogUtils.e( "downX 不在范围内 ,不拦截事件");
            isIntercept = false;
            isSwitchFromLeft = false;
            isSwitchFromRight = false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isIntercept){//不拦截事件时 不处理
            return false;
        }
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX = tempX = (int) event.getRawX();
                downY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getRawX();
                int deltaX = tempX - moveX;
                tempX = moveX;
                if (Math.abs(moveX - downX) > mTouchSlop && Math.abs((int) event.getRawY() - downY) < mTouchSlop) {
                    isSilding = true;
                }

                LogUtils.e("scroll deltaX=" + deltaX);
                if(enableLeftSildeEvent){//左侧滑动
                    if (moveX - downX >= 0 && isSilding) {
                        mParentView.scrollBy(deltaX, 0);
                    }
                }

                if(enableRightSildeEvent){//右侧滑动
                    if (moveX - downX <= 0 && isSilding) {
                        mParentView.scrollBy(deltaX, 0);
                    }
                }

                LogUtils.e( "/onTouchEvent", "mParentView.getScrollX()=" + mParentView.getScrollX());
                break;
            case MotionEvent.ACTION_UP:
                isSilding = false;
                //mParentView.getScrollX() <= -viewWidth / 2  ==>指左侧滑动
                //mParentView.getScrollX() >= viewWidth / 2   ==>指右侧滑动
                if (mParentView.getScrollX() <= -viewWidth / 2 || mParentView.getScrollX() >= viewWidth / 2) {
                    canSwitch = true;
                    if(isSwitchFromLeft){
                        scrollToRight();
                    }

                    if(isSwitchFromRight){
                        scrollToLeft();
                    }
                } else {
                    scrollOrigin();
                    canSwitch = false;
                }
                break;
        }
        return true;

    }


    /**
     * 滚动出界面至右侧
     */
    private void scrollToRight() {
        final int delta = (viewWidth + mParentView.getScrollX());
        // 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
        mScroller.startScroll(mParentView.getScrollX(), 0, -delta + 1, 0, Math.abs(delta));
        postInvalidate();
    }

    /**
     * 滚动出界面至左侧
     */
    private void scrollToLeft() {
        final int delta = (viewWidth - mParentView.getScrollX());
        // 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
        mScroller.startScroll(mParentView.getScrollX(), 0, delta - 1, 0, Math.abs(delta));//此处就不可用+1，也不卡直接用delta
        postInvalidate();
    }

    /**
     * 滚动到起始位置
     */
    private void scrollOrigin() {
        int delta = mParentView.getScrollX();
        mScroller.startScroll(mParentView.getScrollX(), 0, -delta, 0,
                Math.abs(delta));
        postInvalidate();
    }

    @Override
    public void computeScroll() {
        // 调用startScroll的时候scroller.computeScrollOffset()返回true，
        if (mScroller.computeScrollOffset()) {
            mParentView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();

            if (mScroller.isFinished()) {
                if (onSildingFinishListener != null && canSwitch) {
                    LogUtils.e("mScroller finish");
                    if(isSwitchFromLeft){//回调，左侧切换事件
                        onSildingFinishListener.onSildingBack();
                    }

                    if(isSwitchFromRight){//右侧切换事件
                        onSildingFinishListener.onSildingForward();
                    }
                }
            }
        }
    }

    public interface OnSildingFinishListener {
        public void onSildingBack();
        public void onSildingForward();
    }

}
