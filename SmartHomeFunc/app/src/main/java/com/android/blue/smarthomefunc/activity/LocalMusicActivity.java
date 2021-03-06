package com.android.blue.smarthomefunc.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.adapter.LocalMusicAdapter;
import com.android.blue.smarthomefunc.adapter.OnMoreItemListener;
import com.android.blue.smarthomefunc.adapter.OnMusicAdapterItemClickListener;
import com.android.blue.smarthomefunc.application.AppCache;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.executor.MusicScannerClient;
import com.android.blue.smarthomefunc.model.Music;
import com.android.blue.smarthomefunc.service.OnPlayerEventListener;
import com.android.blue.smarthomefunc.utils.FileUtils;
import com.android.blue.smarthomefunc.utils.ImageViewAnimator;
import com.android.blue.smarthomefunc.utils.MusicCoverLoaderUtils;
import com.android.blue.smarthomefunc.utils.MusicUtils;
import com.android.blue.smarthomefunc.view.CircleImageView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * android:descendantFocusability="blocksDescendants"
 * ListView中Item添加具有点击事件的控件时,ListView中item的点击事件失效，原因：焦点被子控件抢占，item失去焦点所以点击失效
 */
public class LocalMusicActivity extends BaseActivity implements AdapterView.OnItemClickListener,
        OnMusicAdapterItemClickListener, OnPlayerEventListener, SeekBar.OnSeekBarChangeListener,
        OnMoreItemListener{

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
    @BindView(R.id.music_bar)
    FrameLayout musicBar;

    private LocalMusicAdapter mLocalMusicAdapter;
    private View mListFooterView;
    private TextView listFooterMusicCountTv;

    //前一次选中位置
    private int preSelectPosition;

    private ImageViewAnimator mCoverAnimate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);
        ButterKnife.bind(this);

        toolbar.setBackgroundResource(R.color.colorPrimaryDark);

        mLocalMusicAdapter = new LocalMusicAdapter(this);
        mList.setAdapter(mLocalMusicAdapter);

        //添加显示歌曲数量
        mListFooterView = getLayoutInflater().inflate(R.layout.list_footer_show_music_count, null);
        listFooterMusicCountTv = mListFooterView.findViewById(R.id.list_footer_show_count);
        mList.addFooterView(mListFooterView, null, false);
        mList.setFooterDividersEnabled(false);
        listFooterMusicCountTv.setText(getResources().getString(R.string.list_footer_show_music_count, AppCache.get().getMusicList().size()));
        mCoverAnimate = new ImageViewAnimator();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initPlayingMusicBar();
        setListener();
        mList.setSelection(getPlayService().getPlayingPosition());
        mLocalMusicAdapter.updatePlayingPosition(getPlayService());
        updateItem(getPlayService().getPlayingPosition());
        mCoverAnimate.initAnimate(musicBarCover);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取正在播放歌曲位置

        preSelectPosition = getPlayService().getPlayingPosition();


        setSelectIndex();
        if (getPlayService().isPlaying()) {
            mCoverAnimate.startMusicBarCoverAnimate();
            musicBarPlaying.setImageResource(R.drawable.selector_music_bar_pause);
        } else {
            mCoverAnimate.stopMusicBarCoverAnimate();
            musicBarPlaying.setImageResource(R.drawable.selector_music_bar_playing);
        }
    }


    private void setSelectIndex(){
        mList.setSelection(getPlayService().getPlayingPosition());
    }

    private void setListener(){
        //list　item点击监听
        mList.setOnItemClickListener(this);
        mLocalMusicAdapter.setOnMusicAdapterItemClickListener(this);
        mLocalMusicAdapter.setOnMoreItemClickListener(this);
        getPlayService().setOnPlayerEventListener(this);
        musicBarSeekBar.setOnSeekBarChangeListener(this);
    }

    /**
     * 在线播放的歌曲, 下载后在进入本地列表, bar更新错误及item没有显示已经下载并在播放的状态
     * 问题解决:由于下载结束之后会触发列表更新, 不管当前正在播放的music对象是否为null, 都会重新赋值
     * 导致获得当前播放音乐信息错误
     */
    private void initPlayingMusicBar() {
        if (getPlayService().getPlayingMusic() != null) {
            LogUtils.i(" init playing musicbar title = "+getPlayService().getPlayingMusic().getTitle());
            musicBarSeekBar.setMax((int) getPlayService().getPlayingMusic().getDuration());
            musicTitle.setText(getPlayService().getPlayingMusic().getTitle());
            musicArtist.setText(getPlayService().getPlayingMusic().getArtist());
            musicBarCover.setImageBitmap(MusicCoverLoaderUtils.getInstance().loadThumbnail(getPlayService().getPlayingMusic()));
        }
    }

    @Override
    public void setActivityFullScreen(boolean flag) {
        //
    }

    /**
     * 播放结束，下一曲切换
     * 跟新播放进度条，更新listView　item选中
     *
     * @param music
     */
    @Override
    public void onChange(Music music) {
        LogUtils.i("onChange music ="+music.getTitle());
        initPlayingMusicBar();
        setSelectIndex();
        mLocalMusicAdapter.updatePlayingPosition(getPlayService());
        updateItem(getPlayService().getPlayingPosition());
        mCoverAnimate.stopMusicBarCoverAnimate();
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
        LogUtils.i(" progress: " + progress);
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
    public void onMusicListUpdate() {
        LogUtils.i("");
        updateView();
    }

    private void updateView() {
        if (AppCache.get().getMusicList().isEmpty()) {
            //本地没有歌曲
        } else {
            //本地存在歌曲
            AppCache.get().getPlayService().updateMusicList(null);
        }
        mLocalMusicAdapter.updatePlayingPosition(getPlayService());
        mLocalMusicAdapter.notifyDataSetChanged();
    }


    @OnClick({R.id.music_bar_playing, R.id.music_bar_next, R.id.music_bar_list, R.id.music_bar})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.music_bar_playing:
                getPlayService().playPause();
                if (getPlayService().isPlaying()) {
                    musicBarPlaying.setImageResource(R.drawable.selector_music_bar_pause);
                } else {
                    if (getPlayService().getCurrentPosition() != musicBarSeekBar.getProgress()){
                        getPlayService().seekTo(musicBarSeekBar.getProgress());
                    }
                    musicBarPlaying.setImageResource(R.drawable.selector_music_bar_playing);
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
     * Press ellipsis more action
     *
     * @param position
     */
    @Override
    public void onMoreClick(View view, int position) {
        LogUtils.i("    -----fht-----");
        //配合OnlineMusicRecycleAdapter 更新显示更多
        mLocalMusicAdapter.notifyDataSetChanged();
    }

    /**
     * Press add music to Playing list
     *
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
        MusicUtils.sharedMusic(this, AppCache.get().getMusicList().get(position));
    }

    /**
     * ListView ITEM click
     *
     * @param adapterView
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LogUtils.i(" position " + position + ", preSelectPosition:" + preSelectPosition);
        if (preSelectPosition != position || !getPlayService().isPlaying()) {
            preSelectPosition = position;
            getPlayService().play(position);
            mLocalMusicAdapter.updatePlayingPosition(getPlayService());
            updateItem(position);
        }


    }

    /**
     * ListView item 更新
     *
     * @param position
     */
    private void updateItem(int position) {
        int lastVisiblePosition = mList.getLastVisiblePosition()-1;
        int firstVisiblePosition = mList.getFirstVisiblePosition();
        View view=null;
        LogUtils.i("updateItem position = " + position + " , lastVisiblePosition=" + lastVisiblePosition + ", firstPosition=" + firstVisiblePosition);
        if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
            view = mList.getChildAt(position - firstVisiblePosition);
            TextView title = view.findViewById(R.id.local_item_title);
            LogUtils.i(" updateItem title = " + title.getText());

        }else if (position < firstVisiblePosition){
            view = mList.getChildAt(firstVisiblePosition - position);
        }else if(position > lastVisiblePosition){
            view = mList.getChildAt(lastVisiblePosition-position);
        }
        if (view != null){
            mLocalMusicAdapter.getView(position, view, mList);
            //解决点击其他item不刷新问题
            mList.invalidateViews();
        }


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
    protected void onStop() {
        super.onStop();
    }


    /**
     * 更多　事件回调监听　开始
     * @param position
     */
    @Override
    public void onDownloadOnlineMusic(int position) {

    }

    @Override
    public void onSharedMusicFromMore(int position) {
        MusicUtils.sharedMusic(this, AppCache.get().getMusicList().get(position));
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
        LogUtils.i("delete music ="+position);
        deleteMusic(AppCache.get().getMusicList().get(position));
    }

    private void deleteMusic(final Music music){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = music.getTitle();
        String msg = getString(R.string.delete_music, title);
        builder.setTitle(getString(R.string.delete_dialog_title));
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                File file = new File(music.getPath());
                if (file.delete()){
                    boolean playing = (music == getPlayService().getPlayingMusic());
                    AppCache.get().getMusicList().remove(music);
                    if (playing){
                        getPlayService().stop();
                        getPlayService().playPause();
                    }else{
                        getPlayService().updatePlayingPosition();
                    }
                    updateView();

                    //刷新媒体库
                   /* Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.fromFile(new File(music.getPath())));
                    sendBroadcast(intent);*/

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new MusicScannerClient(getApplicationContext(),
                                    new File(music.getPath()));
                        }
                    }, 300);
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.create().show();
    }
    // 更多事件回调监听结束
}
