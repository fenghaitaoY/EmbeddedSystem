package com.android.blue.smarthomefunc.view;
/*
    此类实现滑动删除布局
 */
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by Bruce on 11/24/14.
 */
public class SwipeLayout extends LinearLayout {

    private ViewDragHelper viewDragHelper;
    private View contentView;
    private View actionView;
    private int dragDistance;
    private final double AUTO_OPEN_SPEED_LIMIT = 800.0;
    private int draggedX;

    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        viewDragHelper = ViewDragHelper.create(this, new DragHelperCallback());
    }

    //在SwipeLayout的inflate事件中，获取到contentView和actionView
    @Override
    protected void onFinishInflate() {
        contentView = getChildAt(0);
        actionView = getChildAt(1);
        actionView.setVisibility(GONE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //在SwipeLayout的measure事件中，设置拖动的距离为actionView的宽度
        dragDistance = actionView.getMeasuredWidth();
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        //用来确定contentView和actionView是可以拖动的
        @Override
        public boolean tryCaptureView(View view, int i) {
            return view == contentView || view == actionView;
        }

        //onViewPositionChanged在被拖动的view位置改变的时候调用，
        // 如果被拖动的view是contentView，我们需要在这里更新actionView的位置，反之亦然。
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            draggedX = left;
            if (changedView == contentView) {
                actionView.offsetLeftAndRight(dx);
            } else {
                contentView.offsetLeftAndRight(dx);
            }
            if (actionView.getVisibility() == View.GONE) {
                actionView.setVisibility(View.VISIBLE);
            }
            invalidate();
        }

        //clampViewPositionHorizontal用来限制view在x轴上拖动，
        //要实现水平拖动效果必须要实现这个方法，我们这里因为仅仅需要实现水平拖动，所以没有实现clampViewPositionVertical方法。
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == contentView) {
                final int leftBound = getPaddingLeft();
                final int minLeftBound = -leftBound - dragDistance;
                final int newLeft = Math.min(Math.max(minLeftBound, left), 0);
                return newLeft;
            } else {
                final int minLeftBound = getPaddingLeft() + contentView.getMeasuredWidth() - dragDistance;
                final int maxLeftBound = getPaddingLeft() + contentView.getMeasuredWidth() + getPaddingRight();
                final int newLeft = Math.min(Math.max(left, minLeftBound), maxLeftBound);
                return newLeft;
            }
        }

        //getViewHorizontalDragRange方法用来限制view可以拖动的范围
        @Override
        public int getViewHorizontalDragRange(View child) {
            return dragDistance;
        }

        //根据滑动手势的速度以及滑动的距离来确定是否显示actionView。
        //smoothSlideViewTo方法用来在滑动手势之后实现惯性滑动效果
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            boolean settleToOpen = false;
            if (xvel > AUTO_OPEN_SPEED_LIMIT) {
                settleToOpen = false;
            } else if (xvel < -AUTO_OPEN_SPEED_LIMIT) {
                settleToOpen = true;
            } else if (draggedX <= -dragDistance / 2) {
                settleToOpen = true;
            } else if (draggedX > -dragDistance / 2) {
                settleToOpen = false;
            }

            final int settleDestX = settleToOpen ? -dragDistance : 0;
            viewDragHelper.smoothSlideViewTo(contentView, settleDestX, 0);
            ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
        }
    }
    /*
        onInterceptTouchEvent()用于处理事件并改变事件的传递方向。
        返回值为false时事件会传递给子控件的onInterceptTouchEvent()；
        返回值为true时事件会传递给当前控件的onTouchEvent()，而不在传递给子控件，这就是所谓的Intercept(截断)。

        onTouchEvent() 用于处理事件，返回值决定当前控件是否消费（consume）了这个事件。
        可能你要问是否消费了又区别吗，反正我已经针对事件编写了处理代码？
        答案是有区别！比如ACTION_MOVE或者ACTION_UP发生的前提是一定曾经发生了ACTION_DOWN，
        如果你没有消费ACTION_DOWN，那么系统会认为ACTION_DOWN没有发生过，所以ACTION_MOVE或者ACTION_UP就不能被捕获。
    */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.i("fenghaitao", "=====onInterceptTouchEvent====");
        if(viewDragHelper.shouldInterceptTouchEvent(ev)) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        Log.i("fenghaitao","==========onTouchEvent======"+event.getX()+" ,==width="+width+" , ===height="+height);
        if(event.getX() > width*0.6) {
            viewDragHelper.processTouchEvent(event);
            return true;
        }
        return false;




    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
}
