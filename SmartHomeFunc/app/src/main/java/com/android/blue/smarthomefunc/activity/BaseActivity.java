package com.android.blue.smarthomefunc.activity;

import android.app.Application;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.application.AppCache;
import com.android.blue.smarthomefunc.application.SmartHomeApplication;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.service.PlayService;
import com.android.blue.smarthomefunc.utils.PermissionManage;

import butterknife.ButterKnife;

public class BaseActivity extends AppCompatActivity {
    private BaseActivity mContext;
    protected Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setActivityFullScreen(true);
        setVolumeControlStream(AudioManager.STREAM_MUSIC); //设置基于BASE的activity 音量键是设置音乐音量

        mContext = this;



    }

    public void setActivityFullScreen(boolean flag){
        if (flag){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
                View decorView = getWindow().getDecorView();
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
                WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
                localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
            }
        }
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initView();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initView();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        initView();
    }

    private void initView(){
        LogUtils.i("BaseActivity initView , init butterKnife , init toolbar ");
        ButterKnife.bind(this);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        if (mToolbar == null){
            throw new IllegalStateException("This activity not include Toolbar");
        }
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public PlayService getPlayService(){
        PlayService playService = AppCache.get().getPlayService();
        if (playService == null){
            throw new NullPointerException("Playservice is null");
        }
        return playService;
    }

    protected boolean checkServiceAlive(){
        if (AppCache.get().getPlayService() == null){
            startActivity(new Intent(this, SplashActivity.class));
            ((SmartHomeApplication)getApplication()).removeAllActivity();
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManage.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
