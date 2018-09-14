package com.android.blue.smarthomefunc.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.android.blue.smarthomefunc.adapter.OnItemClickListener;
import com.android.blue.smarthomefunc.adapter.RecommendVideoBaseAdapter;
import com.android.blue.smarthomefunc.adapter.VideoItemParseRecycleAdapter;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.executor.IParseWebPageNotify;
import com.android.blue.smarthomefunc.executor.ParseWebPages;
import com.android.blue.smarthomefunc.model.RecommendVideo;
import com.android.blue.smarthomefunc.model.VideoSelectInfo;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailVideoActivity extends BaseActivity implements IParseWebPageNotify, View.OnClickListener, OnItemClickListener {


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

    @BindView(R.id.recommend_item_one_iv)
    ImageView recommendItemOneIv;
    @BindView(R.id.recommend_item_one_title)
    TextView recommendItemOneTitle;
    @BindView(R.id.recommend_item_two_iv)
    ImageView recommendItemTwoIv;
    @BindView(R.id.recommend_item_two_title)
    TextView recommendItemTwoTitle;
    @BindView(R.id.recommend_item_thr_iv)
    ImageView recommendItemThrIv;
    @BindView(R.id.recommend_item_thr_title)
    TextView recommendItemThrTitle;
    @BindView(R.id.recommend_item_four_iv)
    ImageView recommendItemFourIv;
    @BindView(R.id.recommend_item_four_title)
    TextView recommendItemFourTitle;
    @BindView(R.id.recommend_item_five_iv)
    ImageView recommendItemFiveIv;
    @BindView(R.id.recommend_item_five_title)
    TextView recommendItemFiveTitle;


    private String url;
    private String name;
    private ParseWebPages mParseWebPages;
    private VideoSelectInfo mVideoSelectInfo;
    private VideoItemParseRecycleAdapter mAdapter;
    private RecommendVideoBaseAdapter mRecommedAdapter;

    int[] backColors = {R.color.black_trans_one, R.color.black_trans_two,
            R.color.black_trans_thr, R.color.black_trans_four,
            R.color.black_trans_five, R.color.black_trans_six,
            R.color.black_trans_seven, R.color.black_trans_eight,
            R.color.black_trans_nine, R.color.black_trans_ten,
    };

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_video);
        ButterKnife.bind(this);

        //设置全屏透明状态栏
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

        decorView.setSystemUiVisibility(option);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);


        url = getIntent().getStringExtra("video_detail_url");
        name = getIntent().getStringExtra("title_name");

        //下拉刷新
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

        //设置刷新进度条在界面的显示位置
        swipeRefresh.setProgressViewOffset(false, 0, 180);
        swipeRefresh.setDistanceToTriggerSync(500);

        //根据滑动距离改变状态栏透明度, 滑动距离需要适配不同分辨率屏幕
        swipeScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                LogUtils.i("onScroll change scrollY=" + scrollY);
                if (scrollY >= 0 && scrollY < 340) {
                    toolbarSelf.setBackgroundResource(backColors[scrollY / 34]);

                }

                if (scrollY >= 340) {
                    toolbarBackTitle.setVisibility(View.VISIBLE);
                    toolbarSelf.setBackgroundResource(backColors[9]);
                } else {
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
        hideRecommend();
    }

    /**
     * 隐藏推荐栏
     */
    private void hideRecommend() {
        recommendItemOneIv.setVisibility(View.GONE);
        recommendItemOneTitle.setVisibility(View.GONE);

        recommendItemTwoIv.setVisibility(View.GONE);
        recommendItemTwoTitle.setVisibility(View.GONE);

        recommendItemThrIv.setVisibility(View.GONE);
        recommendItemThrTitle.setVisibility(View.GONE);

        recommendItemFourIv.setVisibility(View.GONE);
        recommendItemFourTitle.setVisibility(View.GONE);

        recommendItemFiveIv.setVisibility(View.GONE);
        recommendItemFiveTitle.setVisibility(View.GONE);
    }


    /**
     * 显示推荐电影栏
     *
     * @param videos
     */
    private void showRecommend(final List<RecommendVideo> videos) {
        for (int i = 0; i < videos.size(); i++) {
            if (i == 0) {
                final int position = 0;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getApplicationContext())
                                .load(videos.get(position).getRecVideoCover())
                                .placeholder(R.drawable.default_cover)
                                .into(recommendItemOneIv);
                    }
                });

                recommendItemOneIv.setVisibility(View.VISIBLE);
                recommendItemOneTitle.setText(videos.get(i).recVideoTitle);
                recommendItemOneTitle.setVisibility(View.VISIBLE);
            } else if (i == 1) {
                final int position = 1;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getApplicationContext())
                                .load(videos.get(position).getRecVideoCover())
                                .placeholder(R.drawable.default_cover)
                                .into(recommendItemTwoIv);
                    }
                });

                recommendItemTwoIv.setVisibility(View.VISIBLE);
                recommendItemTwoTitle.setText(videos.get(i).recVideoTitle);
                recommendItemTwoTitle.setVisibility(View.VISIBLE);
            } else if (i == 2) {
                final int position = 2;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getApplicationContext())
                                .load(videos.get(position).getRecVideoCover())
                                .placeholder(R.drawable.default_cover)
                                .into(recommendItemThrIv);
                    }
                });

                recommendItemThrIv.setVisibility(View.VISIBLE);
                recommendItemThrTitle.setText(videos.get(i).recVideoTitle);
                recommendItemThrTitle.setVisibility(View.VISIBLE);
            } else if (i == 3) {
                final int position = 3;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getApplicationContext())
                                .load(videos.get(position).getRecVideoCover())
                                .placeholder(R.drawable.default_cover)
                                .into(recommendItemFourIv);
                    }
                });

                recommendItemFourIv.setVisibility(View.VISIBLE);
                recommendItemFourTitle.setText(videos.get(i).recVideoTitle);
                recommendItemFourTitle.setVisibility(View.VISIBLE);
            } else if (i == 4) {
                final int position = 4;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getApplicationContext())
                                .load(videos.get(position).getRecVideoCover())
                                .placeholder(R.drawable.default_cover)
                                .into(recommendItemFiveIv);
                    }
                });

                recommendItemFiveIv.setVisibility(View.VISIBLE);
                recommendItemFiveTitle.setText(videos.get(i).recVideoTitle);
                recommendItemFiveTitle.setVisibility(View.VISIBLE);
            }
        }
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

        showRecommend(mVideoSelectInfo.getRecommendVideoList());

        setAdapter(mVideoSelectInfo);
    }


    private void setAdapter(final VideoSelectInfo vs) {
        mAdapter = new VideoItemParseRecycleAdapter(vs.getSelectVideoLists());

        mAdapter.setOnItemClickListener(this);

        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.HORIZONTAL);

        videoSelectTvPlaysRecyclerView.setItemAnimator(new DefaultItemAnimator());
        videoSelectTvPlaysRecyclerView.setLayoutManager(lm);
        videoSelectTvPlaysRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_back_iv:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        LogUtils.i(" position = " + position);
        Intent playVideoIntent = new Intent(this, PlayingVideoActivity.class);
        playVideoIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //播放路径

        playVideoIntent.putExtra("videopath", ParseWebPages.WUDI.concat(mVideoSelectInfo.getSelectVideoLists().get(position).getVideoListLink()));
        playVideoIntent.putExtra("position", position);
        startActivity(playVideoIntent);
    }

    @Override
    public void onItemLongClick(View view, int position) {
        LogUtils.i(" position = " + position);
    }
}
