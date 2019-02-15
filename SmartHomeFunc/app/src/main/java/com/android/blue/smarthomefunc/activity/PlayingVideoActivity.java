package com.android.blue.smarthomefunc.activity;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.executor.IParseWebPageNotify;
import com.android.blue.smarthomefunc.executor.MediaController;
import com.android.blue.smarthomefunc.executor.ParseWebPages;
import com.android.blue.smarthomefunc.model.PlayVideoInfo;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLOnBufferingUpdateListener;
import com.pili.pldroid.player.PLOnCompletionListener;
import com.pili.pldroid.player.PLOnErrorListener;
import com.pili.pldroid.player.PLOnVideoSizeChangedListener;
import com.pili.pldroid.player.widget.PLVideoTextureView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayingVideoActivity extends BaseActivity implements IParseWebPageNotify{


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.plvideo_texture_view)
    PLVideoTextureView mPLVideoTextureView;
    @BindView(R.id.toolbar_back_iv)
    ImageView toolbarBackIv;
    @BindView(R.id.toolbar_back_title)
    TextView toolbarBackTitle;
    @BindView(R.id.toolbar_back_subtitle)
    TextView toolbarBackSubtitle;
    @BindView(R.id.toolbar_self)
    RelativeLayout toolbarSelf;
    @BindView(R.id.CoverView)
    ImageView mCoverView;
    @BindView(R.id.LoadingView)
    LinearLayout mLoadingView;

    private String video_url;
    private int position;
    private ParseWebPages mParseWebPages;
    private String videoPath;
    private PlayVideoInfo mPlayVideoInfo;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing_video);
        ButterKnife.bind(this);

        video_url = getIntent().getStringExtra("videopath");
        position = getIntent().getIntExtra("position",0);
        LogUtils.i(""+video_url);

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
        //播放视频保持常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();

        LogUtils.i("状态栏 hight = " + getStatusBarHeight());

        MediaController mMediaController = new MediaController(this);
        mMediaController.setOnClickSpeedAdjustListener(new MediaController.OnClickSpeedAdjustListener() {
            @Override
            public void onClickNormal() {
                mPLVideoTextureView.setPlaySpeed(0X00010001);
            }

            @Override
            public void onClickFaster() {
                mPLVideoTextureView.setPlaySpeed(0X00020001);
            }

            @Override
            public void onClickSlower() {
                mPLVideoTextureView.setPlaySpeed(0X00010002);
            }
        });
        mPLVideoTextureView.setMediaController(mMediaController);

        mPLVideoTextureView.setOnVideoSizeChangedListener(new PLOnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(int width, int height) {
                LogUtils.i(" width ="+width+" , height="+height);
            }
        });

        mPLVideoTextureView.setOnBufferingUpdateListener(new PLOnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(int precent) {
                LogUtils.i(" precent = "+precent);
            }
        });

        mPLVideoTextureView.setOnCompletionListener(new PLOnCompletionListener() {
            @Override
            public void onCompletion() {
                LogUtils.i(" completion ");
            }
        });

        mPLVideoTextureView.setOnErrorListener(new PLOnErrorListener() {
            @Override
            public boolean onError(int i) {
                return false;
            }
        });

        mPLVideoTextureView.setLooping(getIntent().getBooleanExtra("loop", false));

        mPLVideoTextureView.setBufferingIndicator(mLoadingView); //显示正在加载
        mPLVideoTextureView.setCoverView(mCoverView); //显示第一页

        AVOptions options = new AVOptions();
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, 0);
        options.setInteger(AVOptions.KEY_MEDIACODEC, AVOptions.MEDIA_CODEC_SW_DECODE);
        int startPos = getIntent().getIntExtra("start-pos", 0);
        options.setInteger(AVOptions.KEY_START_POSITION, startPos * 1000);
        mPLVideoTextureView.setAVOptions(options);

        mPLVideoTextureView.setDisplayOrientation(90);
        mPLVideoTextureView.setDisplayAspectRatio(PLVideoTextureView.ASPECT_RATIO_PAVED_PARENT);

        //mPLVideoTextureView.setVideoPath("/storage/emulated/0/DCIM/Camera/VID_20180828_140905.mp4");
        //mPLVideoTextureView.setVideoPath("rtmp://live.hkstv.hk.lxdns.com/live/hks");
        mParseWebPages = ParseWebPages.getInstance();
        mParseWebPages.doParsePlayVideo(video_url);
        mParseWebPages.setParseWebPagesCompleted(this);
    }


    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    @Override
    protected void onResume() {
        super.onResume();
        //mPLVideoTextureView.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPLVideoTextureView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPLVideoTextureView.stopPlayback();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        LogUtils.i("   hasFocus = " + hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

    }

    @Override
    public void resolutionCompletedNotification() {
        mPlayVideoInfo = mParseWebPages.getPlayVideoInfo();

        LogUtils.i("link = "+mPlayVideoInfo.getPlayVideoLists().get(position).getVideoLink()+" , position ="+position);
        if(mPlayVideoInfo.getPlayVideoLists().size() > 0) {
            mPLVideoTextureView.setVideoPath(mPlayVideoInfo.getPlayVideoLists().get(position).getVideoLink());
            mPLVideoTextureView.start();
        }
    }
}
