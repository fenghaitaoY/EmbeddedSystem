package com.android.blue.smarthomefunc.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.activity.OnlineMusicActivity;
import com.android.blue.smarthomefunc.adapter.LeaderBoadsAdapter;
import com.android.blue.smarthomefunc.adapter.LocalMusicAdapter;
import com.android.blue.smarthomefunc.application.AppCache;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.entity.MusicExtrasPara;
import com.android.blue.smarthomefunc.enums.LoadStateEnum;
import com.android.blue.smarthomefunc.model.SongListInfo;
import com.android.blue.smarthomefunc.utils.NetworkUtils;
import com.android.blue.smarthomefunc.utils.ViewUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 *　排行榜
 * A simple {@link Fragment} subclass.
 * Use the {@link LeaderboardsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LeaderboardsFragment extends Fragment implements AdapterView.OnItemClickListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    @BindView(R.id.frag_song_list)
    ListView mSongListView;

    @BindView(R.id.frag_loading)
    LinearLayout mLoadingView;

    @BindView(R.id.frag_load_fail)
    LinearLayout mLoadFailView;

    private Unbinder butterKnife;
    private View mView;

    private List<SongListInfo> mSongLists;

    public LeaderboardsFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LeaderboardsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LeaderboardsFragment newInstance(String param1, String param2) {
        LeaderboardsFragment fragment = new LeaderboardsFragment();
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
        mView = inflater.inflate(R.layout.fragment_leaderboards_songlist, container, false);
        butterKnife= ButterKnife.bind(this, mView);


        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtils.i("onActivityCreated");
        if (!NetworkUtils.isNetworkAvailable(getContext())){
            ViewUtils.changViewState(mSongListView, mLoadingView, mLoadFailView, LoadStateEnum.LOAD_FAIL);
            return;
        }

        mSongLists = AppCache.get().getSongListInfos();
        if (mSongLists.isEmpty()){
            String[] titles = getResources().getStringArray(R.array.online_music_list_title);
            String[] types = getResources().getStringArray(R.array.online_music_list_type);
            for (int i = 0; i< titles.length;i++){
                SongListInfo info = new SongListInfo();
                info.setTitle(titles[i]);
                info.setType(types[i]);
                mSongLists.add(info);
            }
        }


        LeaderBoadsAdapter adapter = new LeaderBoadsAdapter(mSongLists);
        mSongListView.setAdapter(adapter);
        mSongListView.setOnItemClickListener(this);
        //listview 隐藏滚动条
        mSongListView.setVerticalScrollBarEnabled(false);
        mSongListView.setFastScrollEnabled(false);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        butterKnife.unbind();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        SongListInfo info = mSongLists.get(position);
        Intent onlineMusicIntent = new Intent(getContext(), OnlineMusicActivity.class);
        onlineMusicIntent.putExtra(MusicExtrasPara.MUSIC_LIST_TYPE, info);
        startActivity(onlineMusicIntent);
    }
}
