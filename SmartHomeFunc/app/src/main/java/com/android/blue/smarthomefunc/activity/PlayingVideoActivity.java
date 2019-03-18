package com.android.blue.smarthomefunc.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;

public class PlayingVideoActivity extends BaseActivity implements IParseWebPageNotify{


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.vitamio_videoview)
    VideoView mVideoViewVitamio;
    @BindView(R.id.toolbar_back_iv)
    ImageView toolbarBackIv;
    @BindView(R.id.toolbar_back_title)
    TextView toolbarBackTitle;
    @BindView(R.id.toolbar_back_subtitle)
    TextView toolbarBackSubtitle;
    @BindView(R.id.toolbar_self)
    RelativeLayout toolbarSelf;
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

        Vitamio.isInitialized(getApplicationContext());

        setContentView(R.layout.activity_playing_video);
        ButterKnife.bind(this);

        video_url = getIntent().getStringExtra("videopath");
        position = getIntent().getIntExtra("position",0);
        LogUtils.i(""+video_url);

        View decorView = getWindow().getDecorView();


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

        /*if(!io.vov.vitamio.LibsChecker.checkVitamioLibs(this)) return;*/

        //网页解析
        mParseWebPages = ParseWebPages.getInstance();
        mParseWebPages.doParsePlayVideo(video_url);
        mParseWebPages.setParseWebPagesCompleted(this);

        initVideoView();
        mLoadingView.setVisibility(View.VISIBLE);


    }

    private void initVideoView(){
        MediaController mMediaController = new MediaController(this);
        mVideoViewVitamio.setMediaController(mMediaController);
        mVideoViewVitamio.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {

            }
        });

        mVideoViewVitamio.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });

        mVideoViewVitamio.setVisibility(View.GONE);
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

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoViewVitamio.isPlaying()){
            mVideoViewVitamio.stopPlayback();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

            mLoadingView.setVisibility(View.GONE);

            String videoPath = mPlayVideoInfo.getPlayVideoLists().get(position).getVideoLink();

            if (videoPath.endsWith("mp4") || videoPath.endsWith("m3u8") || videoPath.endsWith("mkv")
                || videoPath.endsWith("flv") || videoPath.endsWith("rmvb") || videoPath.endsWith("mms")){
                playVideo(videoPath);

            }else{
                LogUtils.i(mParseWebPages.getSelectVideoInfo().getSelectVideoLists().get(position).getVideoListLink());
                videoPath = mParseWebPages.getSelectVideoInfo().getSelectVideoLists().get(position).getVideoListLink();
                Intent webVideo = new Intent(this, WebViewVideoActivity.class);
                webVideo.putExtra("path", ParseWebPages.WUDI+videoPath);
                startActivity(webVideo);
                finish();
            }
        }
    }


    private void playVideo(String path){
        mVideoViewVitamio.setVisibility(View.VISIBLE);
        mVideoViewVitamio.setVideoPath(path);
        mVideoViewVitamio.requestFocus();
        mVideoViewVitamio.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setPlaybackSpeed(1.0f);
            }
        });
    }


}
