package com.android.blue.smarthomefunc.fragment;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.adapter.VideoHomepageRecycleAdapter;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.executor.IParseWebPageNotify;
import com.android.blue.smarthomefunc.executor.ParseWebPages;
import com.android.blue.smarthomefunc.model.HomepageListInfo;
import com.android.blue.smarthomefunc.model.HomepageSlideInfo;
import com.android.blue.smarthomefunc.utils.HomepageSlideLooperUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * onAttach -> onCreate -> onCreateView -> onActivityCreate ->
 * onStart -> onResume -> onPause -> onStop -> onDestroyView -> onDestroy ->onDetach
 * <p>
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomVideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomVideoFragment extends Fragment implements IParseWebPageNotify, View.OnClickListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.scrolling_recommend_iv)
    ImageView scrollingRecommendIv;
    @BindView(R.id.scrolling_tv_one)
    TextView scrollingTvOne;
    @BindView(R.id.scrolling_tv_two)
    TextView scrollingTvTwo;
    @BindView(R.id.scrolling_tv_three)
    TextView scrollingTvThree;
    @BindView(R.id.video_homepage_recyclerView)
    RecyclerView videoHomepageRecyclerView;


    private String mParam1;
    private String mParam2;

    private View videoView;
    private Unbinder unbinder;
    private ParseWebPages mParseWebPages;
    private HomepageSlideLooperUtils mSlideLooperUtils;

    List<HomepageSlideInfo> homepageSlideInfos;
    List<HomepageListInfo> homepageListInfos;

    VideoHomepageRecycleAdapter mAdapter;


    public CustomVideoFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomVideoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomVideoFragment newInstance(String param1, String param2) {
        CustomVideoFragment fragment = new CustomVideoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        videoView = inflater.inflate(R.layout.fragment_custom_video, container, false);

        unbinder = ButterKnife.bind(this, videoView);
        mParseWebPages = ParseWebPages.getInstance();
        mSlideLooperUtils = new HomepageSlideLooperUtils(getContext());
        homepageListInfos = new ArrayList<>();
        init();
        return videoView;
    }


    private void init(){
        mAdapter = new VideoHomepageRecycleAdapter(homepageListInfos);
        videoHomepageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        videoHomepageRecyclerView.setAdapter(mAdapter);
        videoHomepageRecyclerView.setItemAnimator(new DefaultItemAnimator());



        //解析滚动推荐
        mParseWebPages.doParseSlideVideo(ParseWebPages.WUDI);
        mParseWebPages.setParseWebPagesCompleted(this);
        scrollingTvOne.setOnClickListener(this);
        scrollingTvTwo.setOnClickListener(this);
        scrollingTvThree.setOnClickListener(this);
        scrollingRecommendIv.setOnClickListener(this);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (homepageSlideInfos != null && homepageSlideInfos.size() > 0){
            mSlideLooperUtils.startLoop(homepageSlideInfos, scrollingTvOne, scrollingTvTwo, scrollingTvThree, scrollingRecommendIv);
        }
    }

    @Override
    public void resolutionCompletedNotification() {
        LogUtils.i("");
        homepageSlideInfos = mParseWebPages.getHomePageSlideList();
        homepageListInfos = mParseWebPages.getHomepageListInfos();
        mAdapter.notifyDataSetChanged();
        mSlideLooperUtils.startLoop(homepageSlideInfos, scrollingTvOne, scrollingTvTwo, scrollingTvThree, scrollingRecommendIv);
    }

    @Override
    public void onStop() {
        super.onStop();
        mSlideLooperUtils.stopLoop();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.scrolling_tv_one:
                if (homepageSlideInfos.size() > 0) {
                    mSlideLooperUtils.clickUpdateLooper(0);
                }
                break;
            case R.id.scrolling_tv_two:
                if (homepageSlideInfos.size() >= 1) {
                    mSlideLooperUtils.clickUpdateLooper(1);
                }
                break;
            case R.id.scrolling_tv_three:
                if (homepageSlideInfos.size() >= 2) {
                    mSlideLooperUtils.clickUpdateLooper(2);
                }
                break;
            case R.id.scrolling_recommend_iv:
                LogUtils.i("num ="+mSlideLooperUtils.getClickSlideItemNum()+" url="+homepageSlideInfos.get(mSlideLooperUtils.getClickSlideItemNum()).getVideoLink());
                break;
        }

    }
}