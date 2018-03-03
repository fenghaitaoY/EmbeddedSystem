package com.android.blue.smarthomefunc.activity;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.adapter.LocalMusicAdapter;
import com.android.blue.smarthomefunc.adapter.OnMusicAdapterItemClickListener;
import com.android.blue.smarthomefunc.application.AppCache;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.model.Music;
import com.android.blue.smarthomefunc.service.OnPlayerEventListener;
import com.android.blue.smarthomefunc.utils.MusicCoverLoaderUtils;
import com.android.blue.smarthomefunc.view.CircleImageView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * android:descendantFocusability="blocksDescendants"
 * ListView中Item添加具有点击事件的控件时,ListView中item的点击事件失效，原因：焦点被子控件抢占，item失去焦点所以点击失效
 */
public class LocalMusicActivity extends BaseActivity implements AdapterView.OnItemClickListener,
        OnMusicAdapterItemClickListener, OnPlayerEventListener, SeekBar.OnSeekBarChangeListener{

    @BindView(R.id.list_local)
    ListView mList;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.music_bar_seekBar)
    SeekBar musicBarSeekBar;
    @BindView(R.id.music_title)
    TextView musicTitle;
    @BindView(R.id.music_artist)
    TextView musicArtist;
    @BindView(R.id.music_bar_playing)
    ImageView musicBarPlaying;
    @BindView(R.id.music_bar_next)
    ImageView musicBarNext;
    @BindView(R.id.music_bar_list)
    ImageView musicBarList;
    @BindView(R.id.music_bar_cover)
    CircleImageView musicBarCover;

    private LocalMusicAdapter mLocalMusicAdapter;
    private View mListFooterView;
    private TextView listFooterMusicCountTv;

    //前一次选中位置
    private int preSelectPosition;

    private ObjectAnimator coverAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);

        toolbar.setBackgroundResource(R.color.colorPrimaryDark);


        mLocalMusicAdapter = new LocalMusicAdapter();
        mList.setAdapter(mLocalMusicAdapter);
        mLocalMusicAdapter.updatePlayingPosition(getPlayService());

        //添加显示歌曲数量
        mListFooterView = getLayoutInflater().inflate(R.layout.list_footer_show_music_count, null);
        listFooterMusicCountTv = mListFooterView.findViewById(R.id.list_footer_show_count);
        mList.addFooterView(mListFooterView, null,true);
        listFooterMusicCountTv.setText(getResources().getString(R.string.list_footer_show_music_count, AppCache.get().getMusicList().size()));

        //list　item点击监听
        mList.setOnItemClickListener(this);
        mLocalMusicAdapter.setOnMusicAdapterItemClickListener(this);
        getPlayService().setOnPlayerEventListener(this);
        musicBarSeekBar.setOnSeekBarChangeListener(this);

        initPlayingMusicBar();
        mList.setSelection(getPlayService().getPlayingPosition());

        initAnimate();

        TextView tv = new TextView(this);
        tv.setText("13455532");
        tv.setTextSize(100);
        tv.setTextColor(Color.RED);
        tv.setGravity(Gravity.RIGHT);
        getWindow().addContentView(tv,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));

    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取正在播放歌曲位置
        preSelectPosition = getPlayService().getPlayingPosition();
        if (getPlayService().isPlaying()){
            musicBarPlaying.setImageResource(R.drawable.selector_music_bar_pause);
        }else{
            musicBarPlaying.setImageResource(R.drawable.selector_music_bar_playing);
        }
    }

    private void initPlayingMusicBar(){
        musicBarSeekBar.setMax((int) getPlayService().getPlayingMusic().getDuration());
        musicTitle.setText(getPlayService().getPlayingMusic().getTitle());
        musicArtist.setText(getPlayService().getPlayingMusic().getArtist());
        musicBarCover.setImageBitmap(MusicCoverLoaderUtils.getInstance().loadThumbnail(getPlayService().getPlayingMusic()));
    }

    @Override
    public void setActivityFullScreen(boolean flag) {
        //
    }

    /**
     * 初始化动画
     */
    private void initAnimate(){
        //初始动画
        coverAnimator = ObjectAnimator.ofFloat(musicBarCover, "rotation", 0f,359f);
        LinearInterpolator interpolator = new LinearInterpolator(); //设置匀速旋转
        coverAnimator.setDuration(6000);
        coverAnimator.setInterpolator(interpolator);
        coverAnimator.setRepeatCount(-1);
        coverAnimator.setRepeatMode(ValueAnimator.RESTART);
    }

    /**
     * 播放组合控件　专辑旋转
     */
    private void startMusicBarCoverAnimate(){
        if (coverAnimator.isPaused()){
            coverAnimator.resume();
        }else {
            coverAnimator.start();
        }

    }

    /**
     * 停止旋转
     */
    private void stopMusicBarCoverAnimate(){
       // musicBarCover.clearAnimation();
        coverAnimator.pause();
    }

    /**
     * 播放结束，下一曲切换
     * 跟新播放进度条，更新listView　item选中
     * @param music
     */
    @Override
    public void onChange(Music music) {
        LogUtils.i("");
        initPlayingMusicBar();
        mLocalMusicAdapter.updatePlayingPosition(getPlayService());
        updateItem(getPlayService().getPlayingPosition());
        stopMusicBarCoverAnimate();
    }

    @Override
    public void onPlayerStart() {
        LogUtils.i("");
        musicBarPlaying.setImageResource(R.drawable.selector_music_bar_pause);
        startMusicBarCoverAnimate();
    }

    @Override
    public void onPlayerPause() {
        LogUtils.i("");
        musicBarPlaying.setImageResource(R.drawable.selector_music_bar_playing);
        stopMusicBarCoverAnimate();

    }

    @Override
    public void onPublishProgress(int progress) {
        LogUtils.i(" progress: "+progress);
        musicBarSeekBar.setProgress(progress);
    }

    @Override
    public void onBufferingUpdate(int percent) {
        LogUtils.i("");
    }

    @Override
    public void onTimer(long remain) {

    }

    @Override
    public void onMusicListUpdate(){
        LogUtils.i("");
        updateView();
    }

    private void updateView() {
        if (AppCache.get().getMusicList().isEmpty()){
            //本地没有歌曲
        }else{
            //本地存在歌曲
        }
        mLocalMusicAdapter.updatePlayingPosition(getPlayService());
        mLocalMusicAdapter.notifyDataSetChanged();
    }


    @OnClick({R.id.music_bar_playing, R.id.music_bar_next, R.id.music_bar_list})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.music_bar_playing:
                getPlayService().playPause();
                if (getPlayService().isPlaying()){
                    musicBarPlaying.setImageResource(R.drawable.selector_music_bar_pause);
                }else {
                    musicBarPlaying.setImageResource(R.drawable.selector_music_bar_playing);
                }
                break;
            case R.id.music_bar_next:
                getPlayService().next();
                break;
            case R.id.music_bar_list:

                break;
        }
    }


    /**
     * Press ellipsis more action
     * @param position
     */
    @Override
    public void onMoreClick(int position) {
        LogUtils.i("");
    }

    /**
     * Press add music to Playing list
     * @param position
     */
    @Override
    public void onAddPlayingListClick(int position) {
        LogUtils.i("");
    }

    @Override
    public void onLoverClick(int position) {
        LogUtils.i("");
    }

    @Override
    public void onShareMusicClick(int position) {
        LogUtils.i("");
        sharedMusic(AppCache.get().getMusicList().get(position));
    }

    /**
     * ListView ITEM click　
     * @param adapterView
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LogUtils.i(" position "+position+", preSelectPosition:"+preSelectPosition);
        if (preSelectPosition != position || !getPlayService().isPlaying()) {
            preSelectPosition = position;
            getPlayService().play(position);
            mLocalMusicAdapter.updatePlayingPosition(getPlayService());
            updateItem(position);
        }


    }

    /**
     * ListView item 更新
     * @param position
     */
    private void updateItem(int position){
        int lastVisiblePosition = mList.getLastVisiblePosition();
        int firstVisiblePosition = mList.getFirstVisiblePosition();

        LogUtils.i("position = "+position+" , lastVisiblePosition="+lastVisiblePosition+", firstPosition="+firstVisiblePosition);
        if (position >= firstVisiblePosition && position <= lastVisiblePosition){
            View view = mList.getChildAt(position-firstVisiblePosition);
            TextView title = view.findViewById(R.id.local_item_title);
            LogUtils.i("title = "+title.getText());

            mLocalMusicAdapter.getView(position, view, mList);
            //解决点击其他item不刷新问题
            mList.invalidateViews();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int position, boolean press) {
        if (press){
            LogUtils.i(" onProgresschage position ="+position);
            getPlayService().seekTo(position);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    /**
     * 分享音乐
     * @param music
     */
    private void sharedMusic(Music music){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        File file = new File(music.getPath());
        shareIntent.setType("audio/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
    }
}
