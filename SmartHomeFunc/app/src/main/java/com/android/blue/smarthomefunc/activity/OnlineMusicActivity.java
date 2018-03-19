package com.android.blue.smarthomefunc.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.adapter.HeaderAndFooterWrapper;
import com.android.blue.smarthomefunc.adapter.OnItemClickListener;
import com.android.blue.smarthomefunc.adapter.OnMusicAdapterItemClickListener;
import com.android.blue.smarthomefunc.adapter.OnlineMusicRecycleAdapter;
import com.android.blue.smarthomefunc.application.AppCache;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.entity.MusicExtrasPara;
import com.android.blue.smarthomefunc.enums.LoadStateEnum;
import com.android.blue.smarthomefunc.executor.PlayOnlineMusic;
import com.android.blue.smarthomefunc.http.HttpCallback;
import com.android.blue.smarthomefunc.http.HttpClient;
import com.android.blue.smarthomefunc.model.Music;
import com.android.blue.smarthomefunc.model.OnlineMusic;
import com.android.blue.smarthomefunc.model.OnlineMusicList;
import com.android.blue.smarthomefunc.model.SongListInfo;
import com.android.blue.smarthomefunc.service.OnPlayerEventListener;
import com.android.blue.smarthomefunc.utils.ImageViewAnimator;
import com.android.blue.smarthomefunc.utils.MusicCoverLoaderUtils;
import com.android.blue.smarthomefunc.utils.ViewUtils;
import com.android.blue.smarthomefunc.view.CircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OnlineMusicActivity extends BaseActivity implements OnItemClickListener,
        OnMusicAdapterItemClickListener, OnPlayerEventListener, SeekBar.OnSeekBarChangeListener{


    @BindView(R.id.collapsing_image_view)
    ImageView collapsingImageView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.online_collapsing_tool_bar)
    CollapsingToolbarLayout onlineCollapsingToolBar;
    @BindView(R.id.choice_mode_iv)
    ImageView choiceModeIv;
    @BindView(R.id.choice_mode_tv)
    TextView choiceModeTv;
    @BindView(R.id.choice_mode_arrow)
    ImageView choiceModeArrow;
    @BindView(R.id.muilt_choice)
    TextView muiltChoice;
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
    @BindView(R.id.music_bar)
    FrameLayout musicBar;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.lv_loading)
    LinearLayout lvLoading;
    @BindView(R.id.lv_load_fail)
    LinearLayout lvLoadFail;

    private OnlineMusicRecycleAdapter recycleAdapter;
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private List<OnlineMusic> mData;
    private SongListInfo mSongListInfo;
    private DividerItemDecoration mDivider;
    private int currentListCount = 20;
    private int mOffset= 0;
    private View footView;
    private ImageViewAnimator mCoverAnimate;
    private int playingPosition;
    private LinearLayoutManager mLayoutManager;

    //recyclerview 视图中间
    private int recyclerHeight;
    private int viewHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_music);
        mData = new ArrayList<>();
        mCoverAnimate = new ImageViewAnimator();

        if (!checkServiceAlive()){
            return;
        }

        mSongListInfo = (SongListInfo) getIntent().getSerializableExtra(MusicExtrasPara.MUSIC_LIST_TYPE);
        setTitle(mSongListInfo.getTitle());


        if (mData.isEmpty()) {
            ViewUtils.changViewState(mRecyclerView, lvLoading, lvLoadFail, LoadStateEnum.LOADING);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
        setListener();
        initPlayingMusicBar();
        Glide.with(this)
                .load(mSongListInfo.getCoverUrl())
                .asBitmap()
                .placeholder(R.drawable.default_cover)
                .error(R.drawable.default_cover)
                .override(200, 200)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        collapsingImageView.setImageBitmap(resource);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCoverAnimate.initAnimate(musicBarCover);
       getMusic(mOffset);
        if (getPlayService().isPlaying()) {
            mCoverAnimate.startMusicBarCoverAnimate();
            musicBarPlaying.setImageResource(R.drawable.selector_music_bar_pause);
        } else {
            mCoverAnimate.stopMusicBarCoverAnimate();
            musicBarPlaying.setImageResource(R.drawable.selector_music_bar_playing);
        }
    }

    private void initPlayingMusicBar() {
        if (getPlayService().getPlayingMusic() != null) {
            LogUtils.i("init bar");
            musicBarSeekBar.setMax((int) getPlayService().getPlayingMusic().getDuration());
            musicTitle.setText(getPlayService().getPlayingMusic().getTitle());
            musicArtist.setText(getPlayService().getPlayingMusic().getArtist());
            musicBarCover.setImageBitmap(MusicCoverLoaderUtils.getInstance().loadThumbnail(getPlayService().getPlayingMusic()));
        }
    }

    @SuppressLint("InflateParams")
    private void init(){
        recycleAdapter = new OnlineMusicRecycleAdapter(this, mData);
        mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(recycleAdapter);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mHeaderAndFooterWrapper);
        mHeaderAndFooterWrapper.notifyDataSetChanged();
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //因为添加头，尾可能会产生多一个，所以在使用前清掉所有的view
        mHeaderAndFooterWrapper.removeAllHeader();
        mHeaderAndFooterWrapper.removeAllFoot();

        //添加分隔线
        mDivider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mDivider.setDrawable(getDrawable(R.drawable.recycle_divider_line_shape));

        footView = LayoutInflater.from(this).inflate(R.layout.auto_load_list_view_footer, null);
        footView.setClickable(false);


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LogUtils.i("onScrollStateChanged newState"+newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(1)){

                    getMusic(mOffset);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                LinearLayoutManager manager= (LinearLayoutManager) recyclerView.getLayoutManager();
                LogUtils.i("dx="+dx+" , dy ="+dy+", visible="+manager.getChildCount()+" , total="+manager.getItemCount()
                +", first="+manager.findFirstVisibleItemPosition()+". last="+manager.findLastVisibleItemPosition());
            }
        });
    }

    private void setListener(){
        recycleAdapter.setOnItemClickListener(this);
        recycleAdapter.setOnMusicAdapterItemClickListener(this);
        getPlayService().setOnPlayerEventListener(this);
        musicBarSeekBar.setOnSeekBarChangeListener(this);

    }


    private void getMusic(final int offset){
        HttpClient.getSongListInfo(mSongListInfo.getType(), currentListCount, offset, new HttpCallback<OnlineMusicList>() {
            @Override
            public void onSuccess(OnlineMusicList onlineMusicList) {
                if (offset == 0 && onlineMusicList == null){
                    ViewUtils.changViewState(mRecyclerView,lvLoading,lvLoadFail, LoadStateEnum.LOAD_FAIL);
                }else if (offset == 0){
                    ViewUtils.changViewState(mRecyclerView, lvLoading, lvLoadFail, LoadStateEnum.LOAD_SUCCESS);
                }

                if (onlineMusicList == null || onlineMusicList.getSong_list() == null
                        || onlineMusicList.getSong_list().size() == 0){
                    return;
                }
                mOffset +=currentListCount;

                mData.addAll(onlineMusicList.getSong_list());
                //下一首播放列表
                AppCache.get().getOnlineMusicList().clear();
                AppCache.get().getOnlineMusicList().addAll(mData);

                LogUtils.i("size ="+mData.size());
                for (OnlineMusic music : mData){
                    LogUtils.i("music"+ music.getTitle());
                }
                mRecyclerView.addItemDecoration(mDivider);
                mHeaderAndFooterWrapper.notifyDataSetChanged();

                LogUtils.i("item count ="+mHeaderAndFooterWrapper.getItemCount()+"  rece item count="+recycleAdapter.getItemCount());

            }

            @Override
            public void onFail(Exception e) {
                if (e instanceof RuntimeException){

                }
                if (offset == 0){
                    ViewUtils.changViewState(mRecyclerView,lvLoading,lvLoadFail, LoadStateEnum.LOAD_FAIL);
                }
            }
        });
    }

    @OnClick({R.id.music_bar_playing, R.id.music_bar_next, R.id.music_bar_list, R.id.music_bar})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.music_bar_playing:
                getPlayService().playPause();
                if (getPlayService().isPlaying()) {
                    musicBarPlaying.setImageResource(R.drawable.selector_music_bar_pause);
                } else {
                    if (getPlayService().getCurrentPosition() != musicBarSeekBar.getProgress()) {
                        getPlayService().seekTo(musicBarSeekBar.getProgress());
                    }
                    musicBarPlaying.setImageResource(R.drawable.selector_music_bar_playing);
                }
                break;
            case R.id.music_bar_next:
                playingPosition = 0;
                for (OnlineMusic music: AppCache.get().getOnlineMusicList()){
                    LogUtils.i("getId ="+getPlayService().getPlayingMusic().getId()+", song id="+music.getSong_id());
                    if (getPlayService().getPlayingMusic().getId() == Integer.valueOf(music.getSong_id())){
                        break;
                    }
                    playingPosition++;
                }
                LogUtils.i("next position ="+playingPosition+", size = "+AppCache.get().getOnlineMusicList().size());
                if (playingPosition < AppCache.get().getOnlineMusicList().size()-1){
                    play(AppCache.get().getOnlineMusicList().get(playingPosition+1));
                }else{
                    play(AppCache.get().getOnlineMusicList().get(0));
                }
                mRecyclerView.scrollToPosition(playingPosition);
                scrollToMiddle();

                break;
            case R.id.music_bar_list:

                break;
            case R.id.music_bar:
                LogUtils.i("start PlayingMainActivity");
                Intent intent = new Intent(this, PlayingMainActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemClick(View view, final int position) {
        LogUtils.i("position ="+position+", name="+mData.get(position).getTitle());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                play(mData.get(position));
                mHeaderAndFooterWrapper.notifyDataSetChanged();
            }
        });
        viewHeight = view.getHeight();
        Rect rect = new Rect();
        mRecyclerView.getGlobalVisibleRect(rect);
        recyclerHeight = rect.bottom-rect.top-viewHeight;

    }

    /**
     * recyclerview　视图滚动到中间位置
     */
    private void scrollToMiddle(){
        int firstPosition = mLayoutManager.findFirstVisibleItemPosition();
        LogUtils.i("firstPosition ="+firstPosition+" playingPosition="+playingPosition);
        mLayoutManager.scrollToPositionWithOffset(playingPosition,200);
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    private void play(OnlineMusic onlineMusic){
        new PlayOnlineMusic(this, onlineMusic){

            @Override
            public void onPrepare() {
                LogUtils.i("");
            }

            @Override
            public void onExecuteSuccess(Music music) {
                LogUtils.i("");
                getPlayService().play(music);
                //更新music bar
            }

            @Override
            public void onExecuteFail(Exception e) {
                LogUtils.i("");
            }
        }.execute();
    }

    private void artistInfo(OnlineMusic music){

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
    public void onMoreClick(int position) {

    }

    @Override
    public void onAddPlayingListClick(int position) {

    }

    @Override
    public void onLoverClick(int position) {

    }

    @Override
    public void onShareMusicClick(int position) {

    }

    @Override
    public void onChange(Music music) {
        LogUtils.i("music  title="+music.getTitle());
        initPlayingMusicBar();
        mCoverAnimate.stopMusicBarCoverAnimate();
        mHeaderAndFooterWrapper.notifyDataSetChanged();
    }

    @Override
    public void onPlayerStart() {
        musicBarPlaying.setImageResource(R.drawable.selector_music_bar_pause);
        mCoverAnimate.startMusicBarCoverAnimate();
    }

    @Override
    public void onPlayerPause() {
        musicBarPlaying.setImageResource(R.drawable.selector_music_bar_playing);
        mCoverAnimate.stopMusicBarCoverAnimate();
    }

    @Override
    public void onPublishProgress(int progress) {
        LogUtils.i("progress = "+progress);
        musicBarSeekBar.setProgress(progress);
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
}
