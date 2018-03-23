package com.android.blue.smarthomefunc.activity;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.enums.PlayModeEnum;
import com.android.blue.smarthomefunc.executor.SearchLrc;
import com.android.blue.smarthomefunc.model.Music;
import com.android.blue.smarthomefunc.service.OnPlayerEventListener;
import com.android.blue.smarthomefunc.utils.FileUtils;
import com.android.blue.smarthomefunc.utils.MusicCoverLoaderUtils;
import com.android.blue.smarthomefunc.utils.MusicUtils;
import com.android.blue.smarthomefunc.utils.Preferences;
import com.android.blue.smarthomefunc.utils.SystemUtils;
import com.bumptech.glide.Glide;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.wcy.lrcview.LrcView;

public class PlayingMainActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener,
        View.OnClickListener, OnPlayerEventListener, LrcView.OnPlayClickListener{

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.play_main_bg)
    ImageView mPlayBackground;
    @BindView(R.id.play_main_back)
    ImageView mBack;
    @BindView(R.id.play_main_title)
    TextView mPlayTitle;
    @BindView(R.id.play_main_share)
    ImageView mPlayShare;
    @BindView(R.id.play_main_artist)
    TextView mPlayArtist;
    @BindView(R.id.play_main_lrc)
    LrcView mPlayMainLrc;
    @BindView(R.id.play_main_start_time)
    TextView mPlayStartTime;
    @BindView(R.id.play_main_seek_bar)
    SeekBar mPlaySeekBar;
    @BindView(R.id.play_main_duration)
    TextView mPlayDuration;
    @BindView(R.id.play_main_mode)
    ImageView mPlayMode;
    @BindView(R.id.play_main_prev)
    ImageView mPlayPrev;
    @BindView(R.id.play_main_play)
    ImageView mPlayOrPause;
    @BindView(R.id.play_main_next)
    ImageView mPlayNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing_main);

        initListen();
        initPlayMode();
        changeMusic(getPlayService().getPlayingMusic());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlaySeekBar.setProgress((int) getPlayService().getCurrentPosition());

        mPlayMainLrc.setCurrentColor(Color.YELLOW); //歌词当前行
        mPlayMainLrc.setTimelineColor(Color.WHITE); //拖拽歌词线的颜色
        mPlayMainLrc.setTimeTextColor(Color.WHITE);   //右侧时间颜色
        mPlayMainLrc.setTimelineTextColor(Color.BLUE); //拖拽时间线出歌词的颜色
    }

    private void initListen(){
        mPlayOrPause.setOnClickListener(this);
        mPlayNext.setOnClickListener(this);
        mPlayPrev.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mPlaySeekBar.setOnSeekBarChangeListener(this);
        mPlayMainLrc.setOnPlayClickListener(this);
        mPlayMode.setOnClickListener(this);
        mPlayShare.setOnClickListener(this);
        getPlayService().setOnPlayerEventListener(this);
    }

    private void initPlayMode(){
        int mode = Preferences.getPlayMode();
        mPlayMode.setImageLevel(mode);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int position, boolean press) {
        if (press){
            if (mPlayMainLrc.hasLrc()){
                mPlayMainLrc.updateTime(position);
            }
            mPlayStartTime.setText(SystemUtils.formatTime(position));
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.play_main_back:
                finish();
                break;
            case R.id.play_main_share:
                MusicUtils.sharedMusic(this, getPlayService().getPlayingMusic());
                break;
            case R.id.play_main_next:
                getPlayService().next();
                break;
            case R.id.play_main_play:
                getPlayService().playPause();
                break;
            case R.id.play_main_prev:
                getPlayService().prev();
                break;
            case R.id.play_main_mode:
                switchPlayMode();
                break;
        }
    }


    @Override
    public void onChange(Music music) {
        changeMusic(music);
    }

    @Override
    public void onPlayerStart() {
        mPlayOrPause.setSelected(true);
    }

    @Override
    public void onPlayerPause() {
        mPlayOrPause.setSelected(false);
    }

    @Override
    public void onPublishProgress(int progress) {
        mPlaySeekBar.setProgress(progress);
        mPlayStartTime.setText(SystemUtils.formatTime(progress));
        LogUtils.i("  hasLrc = "+mPlayMainLrc.hasLrc());
        if (mPlayMainLrc.hasLrc()){
            LogUtils.i("  update Time");
            mPlayMainLrc.updateTime(progress);
        }
    }

    @Override
    public void onBufferingUpdate(int percent) {
        mPlaySeekBar.setSecondaryProgress(mPlaySeekBar.getMax()*100 / percent);
    }

    @Override
    public void onTimer(long remain) {

    }

    @Override
    public void onMusicListUpdate() {

    }

    @Override
    public boolean onPlayClick(long time) {
        if (getPlayService().isPlaying() || getPlayService().isPausing()){
            getPlayService().seekTo((int) time);
            if (getPlayService().isPausing()){
                getPlayService().playPause();
            }
            return true;
        }
        return false;
    }

    private void changeMusic(final Music music){
        if (music == null)return;

        mPlayTitle.setText(music.getTitle());
        mPlayArtist.setText(music.getArtist());
        mPlaySeekBar.setProgress((int) getPlayService().getCurrentPosition());
        mPlaySeekBar.setSecondaryProgress(0);
        mPlaySeekBar.setMax((int) music.getDuration());
        mPlayStartTime.setText(R.string.play_time_start);
        mPlayDuration.setText(SystemUtils.formatTime(music.getDuration()));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogUtils.i(" play lrc ");
                setPlayBackground(music);
                setPlayLrc(music);
            }
        });

        if (getPlayService().isPlaying() || getPlayService().isPreparing()){
            mPlayOrPause.setSelected(true);
        }else{
            mPlayOrPause.setSelected(false);
        }
    }

    private void switchPlayMode(){
        PlayModeEnum modeEnum = PlayModeEnum.valueOf(Preferences.getPlayMode());
        switch (modeEnum){
            case LOOP:
                modeEnum = PlayModeEnum.SHUFFLE;
                break;
            case SHUFFLE:
                modeEnum = PlayModeEnum.SINGLE;
                break;
            case SINGLE:
                modeEnum = PlayModeEnum.LOOP;
                break;
        }
        Preferences.savePlayMode(modeEnum.value());
        initPlayMode();
    }

    private void setPlayBackground(final Music music){
        mPlayBackground.setImageBitmap(MusicCoverLoaderUtils.getInstance().loadBlur(music));
    }

    private void setPlayLrc(Music music){
      if (music.getType() == Music.Type.LOCAL){
          String lrcPath = FileUtils.getLrcFilePath(music);
          if (!TextUtils.isEmpty(lrcPath)){
              loadLrc(lrcPath);
          }else{
              new SearchLrc(music.getArtist(), music.getTitle()){
                  @Override
                  public void onPrepare() {
                      loadLrc("");
                      setLrcLabel("正在搜索歌词");
                  }

                  @Override
                  public void onExecuteSuccess(String s) {
                      loadLrc(s);
                      setLrcLabel("暂无歌词");
                  }

                  @Override
                  public void onExecuteFail(Exception e) {
                      setLrcLabel("暂无歌词");
                  }
              }.execute();

            }
      }else{
          String lrcPath = FileUtils.getLrcDir()+FileUtils.getLrcFileName(music.getArtist(), music.getTitle());
          LogUtils.i("lrc Path:"+lrcPath);
          loadLrc(lrcPath);
      }

    }

    private void loadLrc(String path) {
        File file = new File(path);
        mPlayMainLrc.loadLrc(file);
    }

    private void setLrcLabel(String label){
        mPlayMainLrc.setLabel(label);
    }


}
