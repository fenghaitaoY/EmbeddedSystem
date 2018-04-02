package com.android.blue.smarthomefunc.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.entity.LogUtils;

import butterknife.OnTextChanged;

/**
 * 自带删除功能的EditText
 * Created by fht on 4/2/18.
 */

public class EditTextDelete extends EditText {
    private Context mContext;
    private Drawable mDeleteDrawable;
    private Drawable mHeadDrawable;

    public EditTextDelete(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public EditTextDelete(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public EditTextDelete(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    public EditTextDelete(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        init();
    }


    private void init(){
        mDeleteDrawable = mContext.getResources().getDrawable(R.drawable.edittext_delete);
        mHeadDrawable = mContext.getResources().getDrawable(R.drawable.edittext_search);
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setDrawable();
            }
        });
        setDrawable();
    }

    private void setDrawable(){
        if (mDeleteDrawable != null){
            if (length() < 1){
                setCompoundDrawablesWithIntrinsicBounds(mHeadDrawable, null, null, null);
            }else{
                setCompoundDrawablesWithIntrinsicBounds(mHeadDrawable, null, mDeleteDrawable, null);
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDeleteDrawable != null && event.getAction() == MotionEvent.ACTION_UP){
            int eventX = (int) event.getRawX();
            int eventY = (int) event.getRawY();

            Rect rect = new Rect();//Rect类主要用于表示坐标系中的一块矩形区域，并可以对其做一些简单操作
            getGlobalVisibleRect(rect); //获取视图在屏幕坐标中的可视区域
            LogUtils.i("eventX = "+eventX+", eventY = "+eventY+", rect.left ="+rect.left+"rect.right="+rect.right);
            rect.left = rect.right - 70; //设置删除图标有效区域

            if (rect.contains(eventX, eventY)){
                setText("");
            }
        }

        return super.onTouchEvent(event);
    }
}
