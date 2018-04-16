package com.android.blue.smarthomefunc.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BleConnectLoadingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BleConnectLoadingFragment extends Fragment {
    @BindView(R.id.av_loading_view)
    AVLoadingIndicatorView avLoadingView;
    @BindView(R.id.loading_tv)
    TextView loadingTv;

    private String mParam1;
    private String mParam2;

    private View view;
    private Unbinder unbinder;

    private static BleConnectLoadingFragment fragment;
    private BleConnectLoadingFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BleConnectLoadingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BleConnectLoadingFragment newInstance() {
        if (fragment == null) {
            fragment = new BleConnectLoadingFragment();
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LogUtils.i("");
        view = inflater.inflate(R.layout.fragment_ble_connect_loading, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
