package com.android.blue.smarthomefunc.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.adapter.HeaderAndFooterWrapper;
import com.android.blue.smarthomefunc.adapter.OnItemClickListener;
import com.android.blue.smarthomefunc.adapter.OnMoreItemListener;
import com.android.blue.smarthomefunc.adapter.OnMusicAdapterItemClickListener;
import com.android.blue.smarthomefunc.adapter.OnlineMusicRecycleAdapter;
import com.android.blue.smarthomefunc.adapter.SingerArtistMusicRecycleAdapter;
import com.android.blue.smarthomefunc.application.AppCache;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.entity.MusicExtrasPara;
import com.android.blue.smarthomefunc.enums.LoadStateEnum;
import com.android.blue.smarthomefunc.enums.PlayModeEnum;
import com.android.blue.smarthomefunc.executor.DownloadSingerArtistMusic;
import com.android.blue.smarthomefunc.executor.PlaySingerArtistMusic;
import com.android.blue.smarthomefunc.executor.ShareOnlineMusic;
import com.android.blue.smarthomefunc.http.HttpCallback;
import com.android.blue.smarthomefunc.http.HttpClient;
import com.android.blue.smarthomefunc.model.Music;
import com.android.blue.smarthomefunc.model.OnlineMusic;
import com.android.blue.smarthomefunc.model.PlayModePopupWindow;
import com.android.blue.smarthomefunc.model.SingerArtistMusic;
import com.android.blue.smarthomefunc.model.SingerArtistMusicList;
import com.android.blue.smarthomefunc.model.SingerLIst;
import com.android.blue.smarthomefunc.service.OnPlayerEventListener;
import com.android.blue.smarthomefunc.utils.ImageViewAnimator;
import com.android.blue.smarthomefunc.utils.MusicCoverLoaderUtils;
import com.android.blue.smarthomefunc.utils.Preferences;
import com.android.blue.smarthomefunc.utils.ViewUtils;
import com.android.blue.smarthomefunc.view.CircleImageView;
import com.android.blue.smarthomefunc.view.CustomDividerItemDecoration;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SingerArtistActivity extends BaseActivity implements OnItemClickListener,
        OnMusicAdapterItemClickListener, OnPlayerEventListener, SeekBar.OnSeekBarChangeListener,
        PlayModePopupWindow.OnClickChangePlayModeListener, OnMoreItemListener {

    @BindView(R.id.collapsing_image_view)
    ImageView collapsingImageView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.update_time_tv)
    TextView updateTimeTv;
    @BindView(R.id.detail_tv)
    TextView detailTv;
    @BindView(R.id.online_collapsing_tool_bar)
    CollapsingToolbarLayout onlineCollapsingToolBar;
    @BindView(R.id.choice_mode_iv)
    ImageView choiceModeIv;
    @BindView(R.id.choice_mode_tv)
    TextView choiceModeTv;
    @BindView(R.id.choice_mode_arrow)
    ImageView choiceModeArrow;
    @BindView(R.id.choice_play_mode_layout)
    LinearLayout choicePlayModeLayout;
    @BindView(R.id.muilt_choice)
    TextView muiltChoice;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.lv_loading)
    LinearLayout lvLoading;
    @BindView(R.id.fail_tv)
    TextView failTv;
    @BindView(R.id.lv_load_fail)
    LinearLayout lvLoadFail;
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


    List<SingerArtistMusic> mData = new ArrayList<>();
    private SingerArtistMusicRecycleAdapter mAdapter;

    private ImageViewAnimator mCoverAnimate;
    private int playingPosition;
    private LinearLayoutManager mLayoutManager;
    private SingerLIst.Singer mSinger;
    private CustomDividerItemDecoration mDivider;
    private PlayModePopupWindow popupWindow;

    private int currentListCount = 20;
    private int mOffset = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singer_artist);
        mCoverAnimate = new ImageViewAnimator();

        if (!checkServiceAlive()) {
            return;
        }
        mSinger = (SingerLIst.Singer) getIntent().getExtras().get(MusicExtrasPara.SINGER);
        setTitle(mSinger.getName());

        Glide.with(this).load(mSinger.getAvatar_big())
                .asBitmap()
                .placeholder(R.drawable.default_cover)
                .error(R.drawable.default_cover)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        collapsingImageView.setImageBitmap(resource);
                    }
                });

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


    }

    @Override
    protected void onResume() {
        super.onResume();
        mCoverAnimate.initAnimate(musicBarCover);

        updateSingerArtistMusicListTime();
        updatePlayMode();

        getSingerArtistMusics(mOffset);

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
    private void init() {
        mAdapter = new SingerArtistMusicRecycleAdapter(this, mData);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //添加分隔线
        mDivider = new CustomDividerItemDecoration(this, CustomDividerItemDecoration.VERTICAL, 100,0,0,0);
        mDivider.setDrawable(getDrawable(R.drawable.recycle_divider_line_shape));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LogUtils.i("onScrollStateChanged newState" + newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(1)) {
                    //getSingerArtistMusics(mOffset);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                LogUtils.i("dx=" + dx + " , dy =" + dy + ", visible=" + manager.getChildCount() + " , total=" + manager.getItemCount()
                        + ", first=" + manager.findFirstVisibleItemPosition() + ". last=" + manager.findLastVisibleItemPosition());
            }
        });
    }

    private void setListener() {
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnMusicAdapterItemClickListener(this);
        mAdapter.setOnMoreItemClickListener(this);
        getPlayService().setOnPlayerEventListener(this);
        musicBarSeekBar.setOnSeekBarChangeListener(this);
    }


    private void updateSingerArtistMusicListTime() {
        //更新时间
        if (mData == null || mData.size() == 0) {
            updateTimeTv.setVisibility(View.GONE);
        } else {
            updateTimeTv.setVisibility(View.VISIBLE);
            updateTimeTv.setText(getString(R.string.song_list_update_time, mData.get(0).getPublishtime()));
        }
    }

    private void updatePlayMode() {
        int mode = Preferences.getPlayMode();
        PlayModeEnum modeEnum = PlayModeEnum.valueOf(mode);
        switch (modeEnum) {
            case LOOP: //顺序
                choiceModeIv.setImageResource(R.drawable.online_music_play_mode_queue);
                choiceModeTv.setText(R.string.play_mode_queue);
                break;
            case SHUFFLE: //随机
                choiceModeIv.setImageResource(R.drawable.online_music_play_mode_random);
                choiceModeTv.setText(R.string.play_mode_random);
                break;
            case SINGLE: //单曲
                choiceModeIv.setImageResource(R.drawable.online_music_play_mode_one);
                choiceModeTv.setText(R.string.play_mode_one_loop);
                break;
            default:
                break;
        }
    }

    private void getSingerArtistMusics(final int offset){
        HttpClient.getSingerArtistMusicList(mSinger.getTing_uid(), offset, Integer.valueOf(mSinger.getSongs_total()), new HttpCallback<SingerArtistMusicList>() {
            @Override
            public void onSuccess(SingerArtistMusicList singerArtistMusicList) {
                if ((singerArtistMusicList == null && offset == 0)|| singerArtistMusicList.getSingerArtistMusicList() == null){
                    ViewUtils.changViewState(mRecyclerView, lvLoading, lvLoadFail, LoadStateEnum.LOAD_FAIL);
                    return;
                }else {
                    ViewUtils.changViewState(mRecyclerView, lvLoading, lvLoadFail, LoadStateEnum.LOAD_SUCCESS);
                }
                if (singerArtistMusicList.getSingerArtistMusicList().size() == 0){
                    return;
                }

                mData.addAll(singerArtistMusicList.getSingerArtistMusicList());
                updateSingerArtistMusicListTime();

                AppCache.get().getSingerArtistMusicList().clear();
                AppCache.get().getSingerArtistMusicList().addAll(mData);

                mRecyclerView.addItemDecoration(mDivider);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(Exception e) {

            }
        });
    }



    @OnClick({R.id.music_bar_playing, R.id.music_bar_next, R.id.music_bar_list, R.id.music_bar, R.id.detail_tv, R.id.choice_play_mode_layout})
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
                for (SingerArtistMusic music : AppCache.get().getSingerArtistMusicList()) {
                    if (getPlayService().getPlayingMusic().getId() == Integer.valueOf(music.getSong_id())) {
                        break;
                    }
                    playingPosition++;
                }
                if (playingPosition < AppCache.get().getSingerArtistMusicList().size() - 1) {
                    play(AppCache.get().getSingerArtistMusicList().get(playingPosition+1));
                } else {
                    play(AppCache.get().getSingerArtistMusicList().get(0));
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

            case R.id.choice_play_mode_layout:
                LogUtils.i("play mode layout");

                if (popupWindow == null) {
                    popupWindow = new PlayModePopupWindow(this);
                }
                popupWindow.setContentView(LayoutInflater.from(this).inflate(R.layout.popup_window_play_mode, null));
                popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setFocusable(true);
                popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
                popupWindow.setOutsideTouchable(true);
                popupWindow.setOnClickChangePlayMode(this);
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();

                } else {
                    choiceModeArrow.setImageResource(R.drawable.online_music_play_mode_arrow_up);
                    popupWindow.showAsDropDown(view, 90, 40);

                }
                break;
            case R.id.detail_tv:

                break;
        }
    }

    /**
     * recyclerview　视图滚动到中间位置
     */
    private void scrollToMiddle() {
        int firstPosition = mLayoutManager.findFirstVisibleItemPosition();
        LogUtils.i("firstPosition =" + firstPosition + " playingPosition=" + playingPosition);
        mLayoutManager.scrollToPositionWithOffset(playingPosition, 200);
    }


    private void play(SingerArtistMusic music){
        new PlaySingerArtistMusic(this, music){

            @Override
            public void onPrepare() {

            }

            @Override
            public void onExecuteSuccess(Music music) {
                getPlayService().play(music);
            }

            @Override
            public void onExecuteFail(Exception e) {

            }
        }.execute();
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
    public void onDownloadOnlineMusic(int position) {
        new DownloadSingerArtistMusic(this, mData.get(position)){

            @Override
            public void onPrepare() {
                Toast.makeText(getApplicationContext(), "准备下载．．．", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onExecuteSuccess(Void aVoid) {

            }

            @Override
            public void onExecuteFail(Exception e) {
                Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    @Override
    public void onSharedMusicFromMore(int position) {
        new ShareOnlineMusic(this, mData.get(position).getTitle(), mData.get(position).getSong_id()){

            @Override
            public void onPrepare() {
                
            }

            @Override
            public void onExecuteSuccess(Void aVoid) {

            }

            @Override
            public void onExecuteFail(Exception e) {

            }
        }.execute();
    }

    @Override
    public void onAddMusicToPlayListFromMore(int position) {

    }

    @Override
    public void onMusicInfoFromMore(int position) {

    }

    @Override
    public void onSetMusicToRingFromMore(int position) {

    }

    @Override
    public void onDeleteMusicFromMore(int position) {

    }

    @Override
    public void onItemClick(View view, final int position) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                play(mData.get(position));
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Override
    public void onMoreClick(View view, int position) {
        //配合OnlineMusicRecycleAdapter 更新显示更多
        mAdapter.notifyDataSetChanged();
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
        LogUtils.i("music  title=" + music.getTitle());
        initPlayingMusicBar();
        mCoverAnimate.stopMusicBarCoverAnimate();
        mAdapter.notifyDataSetChanged();
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

    @Override
    public void OnChangePlayMode(int value) {
        updatePlayMode();
    }

    @Override
    public void OnViewDismiss() {
        choiceModeArrow.setImageResource(R.drawable.online_music_play_mode_arrow_down);
    }
}
