package com.android.blue.smarthomefunc.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.activity.LoginActivity;
import com.android.blue.smarthomefunc.entity.Actions;
import com.android.blue.smarthomefunc.utils.Preferences;
import com.android.blue.smarthomefunc.view.CircleImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PeopleSetitingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PeopleSetitingFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.settings_user_cover)
    CircleImageView mSettingsUserCover;
    @BindView(R.id.settings_user_name)
    TextView mSettingsUserName;
    @BindView(R.id.settings_user)
    LinearLayout mSettingsUser;
    @BindView(R.id.settings_notify)
    LinearLayout mSettingsNotify;
    @BindView(R.id.settings_set)
    LinearLayout mSettingsSet;
    @BindView(R.id.settings_about)
    LinearLayout mSettingsAbout;
    @BindView(R.id.settings_login_tv)
    TextView mSettingsLoginTv;
    @BindView(R.id.settings_login)
    LinearLayout mSettingsLogin;

    private String mParam1;
    private String mParam2;


    private View mView;
    private Unbinder mUnbinder;
    private LoginBroadcast mLoginBroadcast;


    public PeopleSetitingFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PeopleSetitingFragment.
     */
    public static PeopleSetitingFragment newInstance(String param1, String param2) {
        PeopleSetitingFragment fragment = new PeopleSetitingFragment();
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
        mView = inflater.inflate(R.layout.fragment_people_setiting, container, false);
        mUnbinder = ButterKnife.bind(this, mView);

        if (Preferences.getLoginStatus()){
            mSettingsLoginTv.setText(getString(R.string.setting_exit));
            mSettingsUserName.setText(Preferences.getLoginName());
        }else {
            mSettingsLoginTv.setText(getString(R.string.setting_login));
            mSettingsUserName.setText(getString(R.string.setting_user_name));
        }


        IntentFilter filter = new IntentFilter(Actions.USER_LOGIN_ACTION);
        mLoginBroadcast = new LoginBroadcast();
        getActivity().registerReceiver(mLoginBroadcast, filter);
        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mUnbinder.unbind();
    }

    @OnClick({R.id.settings_user, R.id.settings_notify, R.id.settings_set, R.id.settings_about, R.id.settings_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.settings_user:
                break;
            case R.id.settings_notify:
                break;
            case R.id.settings_set:
                break;
            case R.id.settings_about:
                break;
            case R.id.settings_login:
                if (Preferences.getLoginStatus()){ //退出登录
                    mSettingsLoginTv.setText(getString(R.string.setting_login));
                    mSettingsUserName.setText(getString(R.string.setting_user_name));
                    Preferences.saveLoginStatus(false);
                }else { //登录
                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(loginIntent);
                }
                break;
        }
    }

    class LoginBroadcast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            mSettingsLoginTv.setText(getString(R.string.setting_exit));
            //记录登录成功
            Preferences.saveLoginStatus(true);

            String name = intent.getStringExtra("name");

            mSettingsUserName.setText(name);
            Preferences.saveLoginName(name);
        }
    }
}
