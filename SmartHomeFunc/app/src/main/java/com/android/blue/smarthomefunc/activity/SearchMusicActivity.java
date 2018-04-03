package com.android.blue.smarthomefunc.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.adapter.OnItemClickListener;
import com.android.blue.smarthomefunc.adapter.OnMoreItemListener;
import com.android.blue.smarthomefunc.adapter.OnMusicAdapterItemClickListener;
import com.android.blue.smarthomefunc.adapter.SearchMusicRecycleAdapter;
import com.android.blue.smarthomefunc.application.AppCache;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.enums.LoadStateEnum;
import com.android.blue.smarthomefunc.executor.DownloadSearchedMusic;
import com.android.blue.smarthomefunc.executor.PlaySearchMusic;
import com.android.blue.smarthomefunc.executor.ShareOnlineMusic;
import com.android.blue.smarthomefunc.http.HttpCallback;
import com.android.blue.smarthomefunc.http.HttpClient;
import com.android.blue.smarthomefunc.model.Music;
import com.android.blue.smarthomefunc.model.OnlineMusic;
import com.android.blue.smarthomefunc.model.PlayModePopupWindow;
import com.android.blue.smarthomefunc.model.SearchMusic;
import com.android.blue.smarthomefunc.service.OnPlayerEventListener;
import com.android.blue.smarthomefunc.utils.ImageViewAnimator;
import com.android.blue.smarthomefunc.utils.MusicCoverLoaderUtils;
import com.android.blue.smarthomefunc.utils.ViewUtils;
import com.android.blue.smarthomefunc.view.CircleImageView;
import com.android.blue.smarthomefunc.view.EditTextDelete;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SearchMusicActivity extends BaseActivity implements OnItemClickListener,
        OnMusicAdapterItemClickListener, OnPlayerEventListener, SeekBar.OnSeekBarChangeListener,
        OnMoreItemListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.search_recycler_view)
    RecyclerView searchRecyclerView;
    @BindView(R.id.lv_loading)
    LinearLayout lvLoading;
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
    @BindView(R.id.search_back)
    ImageButton searchBack;
    @BindView(R.id.search_et)
    EditTextDelete searchEt;
    @BindView(R.id.search_tv)
    TextView searchTv;
    @BindView(R.id.fail_tv)
    TextView searchFailTv;

    private List<SearchMusic.Song> mSongData;
    private SearchMusicRecycleAdapter mSearchAdapter;
    private LinearLayoutManager mLayoutManager;
    private DividerItemDecoration mDivider;

    private ImageViewAnimator mCoverAnimate;
    private int playingPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_music);

        if (!checkServiceAlive()) {
            return;
        }

        mSongData = new ArrayList<>();
        mCoverAnimate = new ImageViewAnimator();
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

        if (mSongData.isEmpty()) {
            ViewUtils.changViewState(searchRecyclerView, lvLoading, lvLoadFail, LoadStateEnum.LOAD_SUCCESS);
        }

        if (getPlayService().isPlaying()) {
            mCoverAnimate.startMusicBarCoverAnimate();
            musicBarPlaying.setImageResource(R.drawable.selector_music_bar_pause);
        } else {
            mCoverAnimate.stopMusicBarCoverAnimate();
            musicBarPlaying.setImageResource(R.drawable.selector_music_bar_playing);
        }

    }

    private void setListener() {
        mSearchAdapter.setOnItemClickListener(this);
        mSearchAdapter.setOnMusicAdapterItemClickListener(this);
        mSearchAdapter.setOnMoreItemClickListener(this);
        getPlayService().setOnPlayerEventListener(this);
        musicBarSeekBar.setOnSeekBarChangeListener(this);

    }
    private void init() {
        mSearchAdapter = new SearchMusicRecycleAdapter(this, mSongData);
        mLayoutManager = new LinearLayoutManager(this);
        searchRecyclerView.setLayoutManager(mLayoutManager);
        searchRecyclerView.setAdapter(mSearchAdapter);
        mSearchAdapter.notifyDataSetChanged();
        searchRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mDivider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mDivider.setDrawable(getDrawable(R.drawable.recycle_divider_line_shape));
    }

    private void initPlayingMusicBar() {
        if (getPlayService().getPlayingMusic() != null) {
            musicBarSeekBar.setMax((int) getPlayService().getPlayingMusic().getDuration());
            musicBarSeekBar.setProgress((int) getPlayService().getCurrentPosition());
            musicTitle.setText(getPlayService().getPlayingMusic().getTitle());
            musicArtist.setText(getPlayService().getPlayingMusic().getArtist());
            musicBarCover.setImageBitmap(MusicCoverLoaderUtils.getInstance().loadThumbnail(getPlayService().getPlayingMusic()));
        }
    }


    private void searchMusic(String songName) {
        HttpClient.searchMusic(songName, new HttpCallback<SearchMusic>() {
            @Override
            public void onSuccess(final SearchMusic searchMusic) {
                if (searchMusic == null || searchMusic.getSong() == null) {
                    searchFailTv.setText(R.string.search_none);
                    ViewUtils.changViewState(searchRecyclerView, lvLoading, lvLoadFail, LoadStateEnum.LOAD_FAIL);
                    return;
                }

                LogUtils.i(" search success music");
                if (searchMusic.getSong().size() == 0){
                    searchFailTv.setText(R.string.search_none);
                    return;
                }

                mSongData.clear();
                mSongData.addAll(searchMusic.getSong());
                ViewUtils.changViewState(searchRecyclerView, lvLoading, lvLoadFail, LoadStateEnum.LOAD_SUCCESS);


                AppCache.get().getSearchMusicList().clear();
                AppCache.get().getSearchMusicList().addAll(searchMusic.getSong());

                searchRecyclerView.addItemDecoration(mDivider);
                mSearchAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFail(Exception e) {
                searchFailTv.setText(R.string.search_none);
                ViewUtils.changViewState(searchRecyclerView, lvLoading, lvLoadFail, LoadStateEnum.LOAD_FAIL);
            }
        });
    }


    @OnClick({R.id.music_bar_playing, R.id.music_bar_next, R.id.music_bar_list, R.id.music_bar,
            R.id.search_back, R.id.search_tv})
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
                for (SearchMusic.Song music : AppCache.get().getSearchMusicList()) {
                    LogUtils.i("getId =" + getPlayService().getPlayingMusic().getId() + ", song id=" + music.getSongid());
                    if (getPlayService().getPlayingMusic().getId() == Integer.valueOf(music.getSongid())) {
                        break;
                    }
                    playingPosition++;
                }
                LogUtils.i("next position =" + playingPosition + ", size = " + AppCache.get().getSearchMusicList().size());
                if (playingPosition < AppCache.get().getSearchMusicList().size() - 1) {
                    play(playingPosition+1);
                } else {
                    play(0);
                }
                searchRecyclerView.scrollToPosition(playingPosition);
                scrollToMiddle();
                break;
            case R.id.music_bar_list:
                break;
            case R.id.music_bar:
                Intent intent = new Intent(this, PlayingMainActivity.class);
                startActivity(intent);
                break;
            case R.id.search_back:
                finish();
                break;
            case R.id.search_tv:
                //点击搜索
                if (!TextUtils.isEmpty(searchEt.getText())) {
                    hideKeyboard();
                    ViewUtils.changViewState(searchRecyclerView, lvLoading, lvLoadFail, LoadStateEnum.LOADING);
                    searchMusic(searchEt.getText().toString());
                }
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

    private void play(int position){
        new PlaySearchMusic(this, mSongData.get(position)){

            @Override
            public void onPrepare() {

            }

            @Override
            public void onExecuteSuccess(Music music) {
                getPlayService().play(music);
                LogUtils.i("cover "+music.getCoverPath());
            }

            @Override
            public void onExecuteFail(Exception e) {

            }
        }.execute();
    }

    /**
     * edittext 输入完成　隐藏键盘
     */
    public void hideKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    // seekbar start
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
    //seekbar end

    //more listen start
    @Override
    public void onDownloadOnlineMusic(int position) {
        new DownloadSearchedMusic(this, mSongData.get(position)){

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
        new ShareOnlineMusic(this, mSongData.get(position).getTitle(), mSongData.get(position).getSongid()){

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
    //more listen end

    //recycler click start
    @Override
    public void onItemClick(View view, final int position) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                play(position);
                initPlayingMusicBar();
                mSearchAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }


    @Override
    public void onMoreClick(View view, int position) {
        //配合OnlineMusicRecycleAdapter 更新显示更多
        mSearchAdapter.notifyDataSetChanged();
    }
    //recycler click end

    @Override
    public void onAddPlayingListClick(int position) {

    }

    @Override
    public void onLoverClick(int position) {

    }

    @Override
    public void onShareMusicClick(int position) {

    }

    //以下是播放回调
    @Override
    public void onChange(Music music) {
        LogUtils.i("");
        initPlayingMusicBar();
        mCoverAnimate.stopMusicBarCoverAnimate();
        mSearchAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPlayerStart() {
        LogUtils.i("");
        musicBarPlaying.setImageResource(R.drawable.selector_music_bar_pause);
        mCoverAnimate.startMusicBarCoverAnimate();
    }

    @Override
    public void onPlayerPause() {
        LogUtils.i("");
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

}
