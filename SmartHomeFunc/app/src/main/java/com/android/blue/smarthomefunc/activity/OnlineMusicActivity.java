package com.android.blue.smarthomefunc.activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.android.blue.smarthomefunc.utils.ViewUtils;
import com.android.blue.smarthomefunc.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_music);
        //ViewUtils.changViewState(mRecyclerView,lvLoading,lvLoadFail, LoadStateEnum.LOAD_FAIL);
        mData = new ArrayList<>();

        if (!checkServiceAlive()){
            return;
        }

        mSongListInfo = (SongListInfo) getIntent().getSerializableExtra(MusicExtrasPara.MUSIC_LIST_TYPE);
        setTitle(mSongListInfo.getTitle());

        //ViewUtils.changViewState(mRecyclerView, lvLoading, lvLoadFail, LoadStateEnum.LOADING);
        init();
        setListener();

    }

    private void init(){
        recycleAdapter = new OnlineMusicRecycleAdapter(this, mData);
        mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(recycleAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        TextView tv = new TextView(this);
        mHeaderAndFooterWrapper.addFootView(tv);
        mRecyclerView.setAdapter(mHeaderAndFooterWrapper);
        mHeaderAndFooterWrapper.notifyDataSetChanged();
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //因为添加头，尾可能会产生多一个，所以在使用前清掉所有的view
        mHeaderAndFooterWrapper.removeAllHeader();
        mHeaderAndFooterWrapper.removeAllFoot();

        //添加分隔线
        mDivider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mDivider.setDrawable(getDrawable(R.drawable.recycle_divider_line_shape));

        View footView = LayoutInflater.from(this).inflate(R.layout.auto_load_list_view_footer, null, false);
        footView.setClickable(false);
        mHeaderAndFooterWrapper.addFootView(footView);

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


    @Override
    public void onItemClick(View view, int position) {
        LogUtils.i("position ="+position+", name="+mData.get(position).getTitle());
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    private void play(OnlineMusic onlineMusic){
        new PlayOnlineMusic(this, onlineMusic){

            @Override
            public void onPrepare() {

            }

            @Override
            public void onExecuteSuccess(Music music) {
                getPlayService().play(music);
                //更新music bar
            }

            @Override
            public void onExecuteFail(Exception e) {

            }
        };
    }

    private void artistInfo(OnlineMusic music){

    }



    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

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

    }

    @Override
    public void onPlayerStart() {

    }

    @Override
    public void onPlayerPause() {

    }

    @Override
    public void onPublishProgress(int progress) {

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
