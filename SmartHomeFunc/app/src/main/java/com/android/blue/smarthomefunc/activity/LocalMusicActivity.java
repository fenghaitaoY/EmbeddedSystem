package com.android.blue.smarthomefunc.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * android:descendantFocusability="blocksDescendants"
 * ListView中Item添加具有点击事件的控件时,ListView中item的点击事件失效，原因：焦点被子控件抢占，item失去焦点所以点击失效
 */
public class LocalMusicActivity extends BaseActivity implements AdapterView.OnItemClickListener,OnMusicAdapterItemClickListener, OnPlayerEventListener{

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);

        mLocalMusicAdapter = new LocalMusicAdapter();
        mList.setAdapter(mLocalMusicAdapter);

        //添加显示歌曲数量
        mListFooterView = getLayoutInflater().inflate(R.layout.list_footer_show_music_count, null);
        listFooterMusicCountTv = mListFooterView.findViewById(R.id.list_footer_show_count);
        mList.addFooterView(mListFooterView, null,true);
        listFooterMusicCountTv.setText(getResources().getString(R.string.list_footer_show_music_count, AppCache.get().getMusicList().size()));

        //list　item点击监听
        mList.setOnItemClickListener(this);
        mLocalMusicAdapter.setOnMusicAdapterItemClickListener(this);
        getPlayService().setOnPlayerEventListener(this);

        initPlayingMusicBar();
        mList.setSelection(getPlayService().getPlayingPosition());
    }

    private void initPlayingMusicBar(){
        musicBarSeekBar.setMax((int) getPlayService().getPlayingMusic().getDuration());
        musicTitle.setText(getPlayService().getPlayingMusic().getTitle());
        musicArtist.setText(getPlayService().getPlayingMusic().getArtist());
        musicBarCover.setImageBitmap(MusicCoverLoaderUtils.getInstance().loadThumbnail(getPlayService().getPlayingMusic()));
    }


    @Override
    public void onChange(Music music) {
        LogUtils.i("");
        initPlayingMusicBar();
    }

    @Override
    public void onPlayerStart() {
        LogUtils.i("");
        musicBarPlaying.setImageResource(R.drawable.selector_music_bar_pause);
    }

    @Override
    public void onPlayerPause() {
        LogUtils.i("");
        musicBarPlaying.setImageResource(R.drawable.selector_music_bar_playing);
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

    /**
     * ListView ITEM click　
     * @param adapterView
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        LogUtils.i(" position "+position);
        getPlayService().play(position);
        mList.setSelection(position);
    }
}
