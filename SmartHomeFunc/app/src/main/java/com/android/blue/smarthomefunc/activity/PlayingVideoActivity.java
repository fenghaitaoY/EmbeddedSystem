package com.android.blue.smarthomefunc.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.entity.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlayingVideoActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    /* @BindView(R.id.play_video_iv)
     ImageView playVideoIv;*/
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.toolbar_back_iv)
    ImageView toolbarBackIv;
    @BindView(R.id.toolbar_back_title)
    TextView toolbarBackTitle;
    @BindView(R.id.toolbar_back_subtitle)
    TextView toolbarBackSubtitle;
    @BindView(R.id.toolbar_self)
    RelativeLayout toolbarSelf;
    @BindView(R.id.swipe_scrollView)
    ScrollView swipeScrollView;

    int [] backColors = {R.color.black_trans_one, R.color.black_trans_two,
                        R.color.black_trans_thr, R.color.black_trans_four,
                        R.color.black_trans_five, R.color.black_trans_six,
                        R.color.black_trans_seven, R.color.black_trans_eight,
                        R.color.black_trans_nine, R.color.black_trans_ten,
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing_video);
        ButterKnife.bind(this);


        View decorView = getWindow().getDecorView();

        //int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
        // int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        //         | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

        int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

        decorView.setSystemUiVisibility(option);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();

        swipeRefresh.setColorSchemeColors(Color.GREEN, Color.YELLOW, Color.RED);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LogUtils.i(" ----- onRefresh-----");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(false);
                    }
                }, 2000);

            }
        });

        swipeRefresh.setProgressViewOffset(false, 0, 180);
        swipeScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @SuppressLint("Range")
            @Override
            public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                LogUtils.i(" scrollX="+scrollX+" , scrollY="+scrollY+" , oldScrollX="+oldScrollX+" , oldScrollY="+oldScrollY);
                if (scrollY >0 && scrollY < 400) {
                    toolbarSelf.setBackgroundResource(backColors[scrollY/40 ]);
                }

            }
        });

        LogUtils.i("状态栏 hight = "+getStatusBarHeight());

    }



    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    @OnClick({R.id.toolbar_back_iv, R.id.toolbar_back_title, R.id.toolbar_back_subtitle, R.id.toolbar_self, R.id.swipe_scrollView, R.id.swipe_refresh})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toolbar_back_iv:
                break;
            case R.id.toolbar_back_title:
                break;
            case R.id.toolbar_back_subtitle:
                break;
            case R.id.toolbar_self:
                break;
            case R.id.swipe_scrollView:
                break;
            case R.id.swipe_refresh:
                break;
        }
    }


   /* @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        LogUtils.i("   hasFocus = "+hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    |View.SYSTEM_UI_FLAG_FULLSCREEN
                    |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

    }*/
}
