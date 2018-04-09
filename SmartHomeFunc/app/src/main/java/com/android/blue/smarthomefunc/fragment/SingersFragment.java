package com.android.blue.smarthomefunc.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.activity.SingerArtistActivity;
import com.android.blue.smarthomefunc.adapter.OnItemClickListener;
import com.android.blue.smarthomefunc.adapter.SingerListRecycleAdapter;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.entity.MusicExtrasPara;
import com.android.blue.smarthomefunc.enums.LoadStateEnum;
import com.android.blue.smarthomefunc.http.HttpCallback;
import com.android.blue.smarthomefunc.http.HttpClient;
import com.android.blue.smarthomefunc.model.SingerLIst;
import com.android.blue.smarthomefunc.utils.NetworkUtils;
import com.android.blue.smarthomefunc.utils.ViewUtils;
import com.android.blue.smarthomefunc.view.CustomDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * onAttach -> onCreate -> onCreateView -> onActivityCreate -> onStart
 * -> onResume -> onPause -> onStop -> onDestroyView ->onDestroy-> onDetach
 * <p>
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SingersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingersFragment extends Fragment  implements OnItemClickListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    @BindView(R.id.singers_recycler_view)
    RecyclerView singersRecyclerView;
    @BindView(R.id.lv_loading)
    LinearLayout lvLoading;
    @BindView(R.id.fail_tv)
    TextView failTv;
    @BindView(R.id.lv_load_fail)
    LinearLayout lvLoadFail;

    private String mParam1;
    private String mParam2;


    private View layoutView;
    private Unbinder unbinder;
    private List<SingerLIst.Singer> mSingers = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private SingerListRecycleAdapter mAdapter;
    private CustomDividerItemDecoration mDivider;

    public SingersFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SingersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SingersFragment newInstance(String param1, String param2) {
        SingersFragment fragment = new SingersFragment();
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
        layoutView = inflater.inflate(R.layout.fragment_singers, container, false);
        ButterKnife.bind(this, layoutView);
        return layoutView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new SingerListRecycleAdapter(mSingers);
        layoutManager = new LinearLayoutManager(getActivity());
        singersRecyclerView.setLayoutManager(layoutManager);
        singersRecyclerView.setAdapter(mAdapter);
        singersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter.notifyDataSetChanged();

        mAdapter.setOnItemClickListener(this);
        mDivider = new CustomDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, 180,0,0,0);
        mDivider.setDrawable(getActivity().getDrawable(R.drawable.recycle_divider_line_shape));

        if (!NetworkUtils.isNetworkAvailable(getContext())) {
            ViewUtils.changViewState(singersRecyclerView, lvLoading, lvLoadFail, LoadStateEnum.LOAD_FAIL);
            return;
        }else {
            ViewUtils.changViewState(singersRecyclerView, lvLoading, lvLoadFail, LoadStateEnum.LOADING);
        }
        getSingerList();


    }

    private void getSingerList(){
        HttpClient.getSingerList(0, 50, new HttpCallback<SingerLIst>() {
            @Override
            public void onSuccess(SingerLIst singerLIst) {
                if (singerLIst == null || singerLIst.getSingers() == null){
                    ViewUtils.changViewState(singersRecyclerView, lvLoading, lvLoadFail, LoadStateEnum.LOAD_FAIL);
                }
                ViewUtils.changViewState(singersRecyclerView, lvLoading, lvLoadFail, LoadStateEnum.LOAD_SUCCESS);
                if (singerLIst != null){
                    for (SingerLIst.Singer singer : singerLIst.getSingers()){
                        LogUtils.i(" \n tinguid: "+singer.getTing_uid()+", name: "+singer.getName()
                                +", firstchar :"+singer.getFirstchar() +", country :"+singer.getCountry()
                                +",avatar_big:"+singer.getAvatar_big()+", albums_total :"+singer.getAlbums_total());
                    }
                }

                mSingers.addAll(singerLIst.getSingers());
                singersRecyclerView.addItemDecoration(mDivider);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(Exception e) {
                ViewUtils.changViewState(singersRecyclerView, lvLoading, lvLoadFail, LoadStateEnum.LOAD_FAIL);
            }

            @Override
            public void onFinish() {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (unbinder != null)
            unbinder.unbind();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    /**
     * RecycleView Item click listen
     */
    @Override
    public void onItemClick(View view, int position) {
        LogUtils.i(" name ="+mSingers.get(position).getName());
        Intent singerIntent = new Intent(getActivity(), SingerArtistActivity.class);
        singerIntent.putExtra(MusicExtrasPara.SINGER, mSingers.get(position));
        startActivity(singerIntent);
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }
}
