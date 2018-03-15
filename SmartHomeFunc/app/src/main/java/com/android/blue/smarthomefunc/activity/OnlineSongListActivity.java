package com.android.blue.smarthomefunc.activity;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.fragment.LeaderboardsFragment;
import com.android.blue.smarthomefunc.model.Music;
import com.android.blue.smarthomefunc.service.OnPlayerEventListener;
import com.android.blue.smarthomefunc.utils.MusicCoverLoaderUtils;
import com.android.blue.smarthomefunc.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class OnlineSongListActivity extends BaseActivity implements ViewPager.OnPageChangeListener,
        OnPlayerEventListener, SeekBar.OnSeekBarChangeListener{
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.online_song_list_tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.online_song_list_view_pager)
    ViewPager mViewPager;
    @BindView(R.id.music_bar_seekBar)
    SeekBar mSeekBar;
    @BindView(R.id.music_title)
    TextView mTitle;
    @BindView(R.id.music_artist)
    TextView mArtist;
    @BindView(R.id.music_bar_playing)
    ImageView mMusicBarPlaying;
    @BindView(R.id.music_bar_next)
    ImageView mMusicBarNext;
    @BindView(R.id.music_bar_list)
    ImageView mMusicBarList;
    @BindView(R.id.music_bar_cover)
    CircleImageView mMusicBarCover;
    @BindView(R.id.music_bar)
    FrameLayout mBarFrameLayout;

    private OnlineSongListPagerAdapter mAdapter;
    private List<Fragment> listFragments=new ArrayList<>();
    private List<String> mTabIndicators = new ArrayList<>();

    private ObjectAnimator coverAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_song_list);

        getSupportActionBar().setTitle(R.string.music_online);
        mToolbar.setNavigationIcon(R.drawable.play_main_back_selector);

        String[] titles = getResources().getStringArray(R.array.oneline_tab_title);
        mTabIndicators.clear();
        for (String title : titles){
            mTabIndicators.add(title);
        }

        listFragments.add(LeaderboardsFragment.newInstance("songlist","online"));
        listFragments.add(LeaderboardsFragment.newInstance("recommend","online"));
        listFragments.add(LeaderboardsFragment.newInstance("singer","online"));
        listFragments.add(LeaderboardsFragment.newInstance("sort","online"));
        mAdapter = new OnlineSongListPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        //将viewpager　和TabLayout 绑定起来
        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.getTabAt(1).select();
        mViewPager.setCurrentItem(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.i("onResume");
        if (getPlayService().isPlaying()) {
            LogUtils.i(" start animate");
            startMusicBarCoverAnimate();
            mMusicBarPlaying.setImageResource(R.drawable.selector_music_bar_pause);
        } else {
            LogUtils.i(" stop animate");
            stopMusicBarCoverAnimate();
            mMusicBarPlaying.setImageResource(R.drawable.selector_music_bar_playing);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.i("onStart");
        initPlayingMusicBar();
        setListener();
        initAnimate();

    }
    /**
     * 初始化动画
     */
    private void initAnimate() {
        if (coverAnimator == null) {
            //初始动画
            coverAnimator = ObjectAnimator.ofFloat(mMusicBarCover, "rotation", 0f, 359f);
            LinearInterpolator interpolator = new LinearInterpolator(); //设置匀速旋转
            coverAnimator.setDuration(6000);
            coverAnimator.setInterpolator(interpolator);
            coverAnimator.setRepeatCount(-1);
            coverAnimator.setRepeatMode(ValueAnimator.RESTART);
        }
    }

    /**
     * 播放组合控件　专辑旋转
     */
    private void startMusicBarCoverAnimate() {
        if (coverAnimator.isPaused()) {
            coverAnimator.resume();
        } else {
            coverAnimator.start();
        }

    }

    /**
     * 停止旋转
     */
    private void stopMusicBarCoverAnimate() {
        coverAnimator.pause();
    }

    private void setListener(){
        //list　item点击监听
        getPlayService().setOnPlayerEventListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    private void initPlayingMusicBar() {
        if (getPlayService().getPlayingMusic() != null) {
            mSeekBar.setMax((int) getPlayService().getPlayingMusic().getDuration());
            mTitle.setText(getPlayService().getPlayingMusic().getTitle());
            mArtist.setText(getPlayService().getPlayingMusic().getArtist());
            mMusicBarCover.setImageBitmap(MusicCoverLoaderUtils.getInstance().loadThumbnail(getPlayService().getPlayingMusic()));
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mTabLayout.getTabAt(position).select();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int position, boolean press) {
        if (press) {
            LogUtils.i(" onProgresschage position =" + position);
            getPlayService().seekTo(position);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onChange(Music music) {
        initPlayingMusicBar();
        stopMusicBarCoverAnimate();
    }

    @Override
    public void onPlayerStart() {
        LogUtils.i("");
        mMusicBarPlaying.setImageResource(R.drawable.selector_music_bar_pause);
        startMusicBarCoverAnimate();
    }

    @Override
    public void onPlayerPause() {
        LogUtils.i("");
        mMusicBarPlaying.setImageResource(R.drawable.selector_music_bar_playing);
        stopMusicBarCoverAnimate();
    }

    @Override
    public void onPublishProgress(int progress) {
        mSeekBar.setProgress(progress);
    }

    @Override
    public void onBufferingUpdate(int percent) {

    }

    @Override
    public void onTimer(long remain) {

    }

    @Override
    public void onMusicListUpdate() {

    }

    @OnClick({R.id.music_bar_playing, R.id.music_bar_next, R.id.music_bar_list, R.id.music_bar})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.music_bar_playing:
                getPlayService().playPause();
                if (getPlayService().isPlaying()) {
                    mMusicBarPlaying.setImageResource(R.drawable.selector_music_bar_pause);
                } else {
                    if (getPlayService().getCurrentPosition() != mSeekBar.getProgress()){
                        getPlayService().seekTo(mSeekBar.getProgress());
                    }
                    mMusicBarPlaying.setImageResource(R.drawable.selector_music_bar_playing);
                }
                break;
            case R.id.music_bar_next:
                getPlayService().next();
                break;
            case R.id.music_bar_list:

                break;
            case R.id.music_bar:
                LogUtils.i("start PlayingMainActivity");
                Intent intent=new Intent(this,PlayingMainActivity.class);
                startActivity(intent);
                //overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_from_left);
                break;
        }
    }

    /**
     * Fragment Adapter
     */
    class OnlineSongListPagerAdapter extends FragmentPagerAdapter {
        public OnlineSongListPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return listFragments.get(position);
        }

        @Override
        public int getCount() {
            return mTabIndicators.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabIndicators.get(position);
        }
    }
}
