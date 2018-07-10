package com.android.blue.smarthomefunc.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
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

public class DetailVideoActivity extends BaseActivity implements IParseWebPageNotify , View.OnClickListener{

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.detail_video_iv)
    ImageView detailVideoIv;
    @BindView(R.id.detail_video_title_name)
    TextView detailVideoTitleName;
    @BindView(R.id.detail_video_ratio)
    TextView detailVideoRatio;
    @BindView(R.id.detail_play_recycler)
    RecyclerView detailPlayRecycler;
    @BindView(R.id.detail_play_title)
    TextView detailPlayTitle;
    @BindView(R.id.detail_play_introduce)
    TextView detailPlayIntroduce;
    @BindView(R.id.detail_play_video_list_choice)
    TextView detailPlayVideoListChoice;


    private String url;
    private String name;
    private ParseWebPages mParseWebPages;
    private VideoSelectInfo mVideoSelectInfo;
    private VideoItemParseRecycleAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_video);
        ButterKnife.bind(this);

        url = getIntent().getStringExtra("video_detail_url");
        name = getIntent().getStringExtra("title_name");

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(name);


        LogUtils.i(" url = " + url + " , name =" + name);

        init();
    }



    private void init() {
        mParseWebPages = ParseWebPages.getInstance();

        mParseWebPages.doParseSelectVideo(url);

        mParseWebPages.setParseWebPagesCompleted(this);

        detailPlayIntroduce.setOnClickListener(this);
        detailPlayVideoListChoice.setOnClickListener(this);
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
                        .into(detailVideoIv);
            }
        });


        detailVideoTitleName.setText(mVideoSelectInfo.getSelectVideoName());
        detailVideoRatio.setText(mVideoSelectInfo.getSelectVideoUpdateToast());
        detailPlayTitle.setText(mVideoSelectInfo.getSelectVideoName());

        setAdapter(mVideoSelectInfo);
    }


    private void setAdapter(final VideoSelectInfo vs) {
        mAdapter = new VideoItemParseRecycleAdapter(vs.getSelectVideoLists());

        /*GridLayoutManager gridLayoutManager = new GridLayoutManager(this, vs.getSelectVideoLists().size());
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (vs.getSelectVideoLists().size() > 5) {
                    return vs.getSelectVideoLists().size();
                }else {
                    return 1;
                }
            }
        });*/
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.HORIZONTAL);

        detailPlayRecycler.setItemAnimator(new DefaultItemAnimator());
        detailPlayRecycler.setLayoutManager(lm);
        detailPlayRecycler.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.detail_play_introduce:
                LogUtils.i(" click introduce ");
                break;
            case R.id.detail_play_video_list_choice:
                LogUtils.i("click list choice ");
                break;
        }
    }
}
