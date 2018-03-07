package com.android.blue.smarthomefunc.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.activity.LocalMusicActivity;
import com.android.blue.smarthomefunc.application.AppCache;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.model.Music;
import com.android.blue.smarthomefunc.service.OnPlayerEventListener;
import com.android.blue.smarthomefunc.service.PlayService;
import com.android.blue.smarthomefunc.utils.MusicCoverLoaderUtils;
import com.android.blue.smarthomefunc.view.CircleImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link MusicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicFragment extends Fragment implements OnPlayerEventListener, SeekBar.OnSeekBarChangeListener{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.music_toolbar_search)
    ImageView mMusicSearch;
    @BindView(R.id.music_toolbar)
    LinearLayout mToolbar;
    @BindView(R.id.music_bar_local)
    LinearLayout mLocalMusicBt;
    @BindView(R.id.music_bar_love)
    LinearLayout mLoveMusicBt;
    @BindView(R.id.music_bar_download)
    LinearLayout mDownloadMusicBt;
    @BindView(R.id.music_bar_recent)
    LinearLayout mRecentMusicBt;
    @BindView(R.id.music_online_layout)
    LinearLayout mOnlineMusicBt;
    @BindView(R.id.music_list_layout)
    LinearLayout mListMusicBt;

    @BindView(R.id.music_bar_seekBar)
    SeekBar mSeekbar;
    @BindView(R.id.music_title)
    TextView mMusicTitle;
    @BindView(R.id.music_artist)
    TextView mMusicArtist;
    @BindView(R.id.music_bar_playing)
    ImageView mBarPlayingBt;
    @BindView(R.id.music_bar_next)
    ImageView mBarNextBt;
    @BindView(R.id.music_bar_list)
    ImageView mBarListBt;
    @BindView(R.id.music_bar_cover)
    CircleImageView mBarImageCover;


    @BindView(R.id.music_local_count)
    TextView mMusicLocalCount;
    @BindView(R.id.music_love_count)
    TextView mMusicLoveCount;
    @BindView(R.id.music_download_count)
    TextView mMusicDownloadCount;
    @BindView(R.id.music_recent_count)
    TextView mMusicRecentCount;

    private String mParam1;
    private String mParam2;

    private View mMusicFragmentView;
    private Context mContext;
    private Unbinder butterknife;


    public MusicFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MusicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MusicFragment newInstance(String param1, String param2) {
        MusicFragment fragment = new MusicFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        // Inflate the layout for this fragment
        mMusicFragmentView = inflater.inflate(R.layout.fragment_music, container, false);
        butterknife = ButterKnife.bind(this, mMusicFragmentView);
        mContext = getActivity();


        return mMusicFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initMusicCountShow();
        initMusicBar();
        getPlayService().setOnPlayerEventListener(this);
        mSeekbar.setOnSeekBarChangeListener(this);
        if (getPlayService().isPlaying()){
            mBarPlayingBt.setImageResource(R.drawable.selector_music_bar_pause);
        }else {
            mBarPlayingBt.setImageResource(R.drawable.selector_music_bar_playing);
        }
    }

    private void initMusicCountShow(){
        mMusicLocalCount.setText(AppCache.get().getMusicList().size()+"");

    }

    private void initMusicBar(){
        mBarImageCover.setImageBitmap(MusicCoverLoaderUtils.getInstance().loadThumbnail(getPlayService().getPlayingMusic()));
        mSeekbar.setMax((int)getPlayService().getPlayingMusic().getDuration());
        mMusicTitle.setText(getPlayService().getPlayingMusic().getTitle());
        mMusicArtist.setText(getPlayService().getPlayingMusic().getArtist());
        mSeekbar.setProgress((int) getPlayService().getCurrentPosition());
    }


    public PlayService getPlayService(){
        PlayService playService = AppCache.get().getPlayService();
        if (playService == null){
            //throw new NullPointerException("Playservice is null");
        }
        return playService;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        butterknife.unbind();
    }

    @OnClick({R.id.music_toolbar_search, R.id.music_bar_local, R.id.music_bar_love, R.id.music_bar_download, R.id.music_bar_recent, R.id.music_online_layout, R.id.music_list_layout, R.id.music_bar_playing, R.id.music_bar_next, R.id.music_bar_list})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.music_toolbar_search:
                LogUtils.i("toolbar search");
                break;
            case R.id.music_bar_local:
                LogUtils.i(" local music");
                Intent localMusic = new Intent(getActivity(), LocalMusicActivity.class);
                startActivity(localMusic);
                break;
            case R.id.music_bar_love:
                LogUtils.i(" love music ");
                break;
            case R.id.music_bar_download:
                LogUtils.i("download music ");
                break;
            case R.id.music_bar_recent:
                LogUtils.i("recent music ");
                break;
            case R.id.music_online_layout:
                LogUtils.i("online layout ");
                break;
            case R.id.music_list_layout:
                LogUtils.i(" list layout ");
                break;
            case R.id.music_bar_playing:
                LogUtils.i("playing bar ");
                getPlayService().playPause();
                if (getPlayService().isPlaying()){
                    mBarPlayingBt.setImageResource(R.drawable.selector_music_bar_pause);
                }else {
                    mBarPlayingBt.setImageResource(R.drawable.selector_music_bar_playing);
                }
                break;
            case R.id.music_bar_next:
                LogUtils.i(" next bar ");
                getPlayService().next();
                break;
            case R.id.music_bar_list:
                LogUtils.i(" list bar ");
                break;
        }
    }

    @Override
    public void onChange(Music music) {
        initMusicBar();
    }

    @Override
    public void onPlayerStart() {
        mBarPlayingBt.setImageResource(R.drawable.selector_music_bar_pause);
    }

    @Override
    public void onPlayerPause() {
        mBarPlayingBt.setImageResource(R.drawable.selector_music_bar_playing);
    }

    @Override
    public void onPublishProgress(int progress) {
        if (mSeekbar != null)
            mSeekbar.setProgress(progress);
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
    public void onProgressChanged(SeekBar seekBar, int position, boolean press) {
        if (press){
            getPlayService().seekTo(position);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
