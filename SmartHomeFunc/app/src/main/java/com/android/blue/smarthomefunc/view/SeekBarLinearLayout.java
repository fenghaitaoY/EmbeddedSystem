package com.android.blue.smarthomefunc.view;
/*
 * 此类实现播放seekbar组合控件
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.blue.smarthomefunc.R;


public class SeekBarLinearLayout extends LinearLayout {

	private TextView currentTime,totalTime;
	private int max=100;  
    private String initTime="00:00:00";
   
    private SeekBar bar=null;
	
	@SuppressLint("NewApi")
	public SeekBarLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public SeekBarLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SeekText);
		max=array.getInt(R.styleable.SeekText_max, 100);
		initTime = array.getString(R.styleable.SeekText_time);
		array.recycle();
	}

	public SeekBarLinearLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		LayoutInflater.from(getContext()).inflate(R.layout.progress_self, this);
		bar = (SeekBar) findViewById(R.id.sbar_progress);
		bar.setMax(max);
		
		currentTime = (TextView) findViewById(R.id.tv_hasPlayed);
		totalTime = (TextView) findViewById(R.id.tv_duration);
		
		currentTime.setText(initTime);
		totalTime.setText(initTime);
		
		
	}
	
	
	
	
	
	
}
