package com.android.blue.smarthomefunc.view;

/**
 * Created by fht on 16-5-12.实现跑马灯滚动显示
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RemoteViews.RemoteView;
import android.widget.TextView;

@RemoteView
public class AlwaysMarqueeTextView extends TextView {
    public AlwaysMarqueeTextView(Context context) {
        super(context);
    }
    public AlwaysMarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public AlwaysMarqueeTextView(Context context, AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public boolean isFocused() {
        return true;
    }
}

