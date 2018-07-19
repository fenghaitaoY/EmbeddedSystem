package com.android.blue.smarthomefunc.activity;

import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.adapter.VideoItemParseRecycleAdapter;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.executor.IParseWebPageNotify;
import com.android.blue.smarthomefunc.executor.ParseWebPages;
import com.android.blue.smarthomefunc.model.VideoSelectInfo;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailVideoActivity extends BaseActivity implements IParseWebPageNotify, View.OnClickListener {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.video_item_cover)
    ImageView videoItemCover;
    @BindView(R.id.video_item_title)
    TextView videoItemTitle;
    @BindView(R.id.video_item_subTitle)
    TextView videoItemSubTitle;
    @BindView(R.id.video_item_info)
    TextView videoItemInfo;
    @BindView(R.id.video_introduce_area)
    TextView videoIntroduceArea;
    @BindView(R.id.video_introduce_year)
    TextView videoIntroduceYear;
    @BindView(R.id.video_introduce_type)
    TextView videoIntroduceType;
    @BindView(R.id.video_introduce_content)
    TextView videoIntroduceContent;
    @BindView(R.id.video_select_tv_plays_recycler_view)
    RecyclerView videoSelectTvPlaysRecyclerView;
    @BindView(R.id.swipe_scrollView)
    ScrollView swipeScrollView;
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
    @BindView(R.id.video_head_background)
    ImageView videoHeadBackground;


    private String url;
    private String name;
    private ParseWebPages mParseWebPages;
    private VideoSelectInfo mVideoSelectInfo;
    private VideoItemParseRecycleAdapter mAdapter;
    int[] backColors = {R.color.black_trans_one, R.color.black_trans_two,
            R.color.black_trans_thr, R.color.black_trans_four,
            R.color.black_trans_five, R.color.black_trans_six,
            R.color.black_trans_seven, R.color.black_trans_eight,
            R.color.black_trans_nine, R.color.black_trans_ten,
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_video);
        ButterKnife.bind(this);

        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

        decorView.setSystemUiVisibility(option);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);


        url = getIntent().getStringExtra("video_detail_url");
        name = getIntent().getStringExtra("title_name");

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

        toolbarBackTitle.setText(name);
        toolbarBackTitle.setVisibility(View.GONE);
        swipeRefresh.setProgressViewOffset(false, 0, 180);
        swipeScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > 0 && scrollY < 400) {
                    toolbarSelf.setBackgroundResource(backColors[scrollY / 40]);

                }

                if (scrollY>=400){
                    toolbarBackTitle.setVisibility(View.VISIBLE);
                }else{
                    toolbarBackTitle.setVisibility(View.GONE);
                }

            }
        });

        init();
    }


    private void init() {
        mParseWebPages = ParseWebPages.getInstance();

        mParseWebPages.doParseSelectVideo(url);

        mParseWebPages.setParseWebPagesCompleted(this);

        toolbarBackIv.setOnClickListener(this);
    }


    @Override
    public void resolutionCompletedNotification() {
        LogUtils.i("----start----");
        mVideoSelectInfo = mParseWebPages.getSelectVideoInfo();


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(getApplicationContext())
                        .load(Uri.parse(mVideoSelectInfo.getSelectVideoImage()))
                        .error(R.drawable.default_video)
                        .into(videoItemCover);
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(getApplicationContext())
                        .load(Uri.parse(mVideoSelectInfo.getSelectVideoImage()))
                        .error(R.drawable.default_video)
                        .into(videoHeadBackground);

            }
        });

        videoItemTitle.setText(mVideoSelectInfo.getSelectVideoName());
        videoItemInfo.setText(mVideoSelectInfo.getSelectVideoUpdateToast());

        videoIntroduceYear.setText(mVideoSelectInfo.getVideoYears());
        videoIntroduceArea.setText(mVideoSelectInfo.getVideoArea());
        videoIntroduceType.setText(mVideoSelectInfo.getVideoType());
        videoIntroduceContent.setText(mVideoSelectInfo.getVideoIntro());


        setAdapter(mVideoSelectInfo);
    }


    private void setAdapter(final VideoSelectInfo vs) {
        mAdapter = new VideoItemParseRecycleAdapter(vs.getSelectVideoLists());

        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.HORIZONTAL);

        videoSelectTvPlaysRecyclerView.setItemAnimator(new DefaultItemAnimator());
        videoSelectTvPlaysRecyclerView.setLayoutManager(lm);
        videoSelectTvPlaysRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.toolbar_back_iv:
                finish();
                break;
        }
    }
}
