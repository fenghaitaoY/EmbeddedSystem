package com.android.blue.smarthomefunc.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.entity.Actions;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.jninative.SmartHomeNativeUtils;
import com.android.blue.smarthomefunc.utils.Preferences;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {


    @BindView(R.id.et_username)
    TextInputLayout etUsername;
    @BindView(R.id.et_password)
    TextInputLayout etPassword;
    @BindView(R.id.bt_go)
    Button btGo;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.tv_toast)
    TextView tvToast;

    private boolean loginStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);//注解

        etUsername.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                if (count > 0){
                    tvToast.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                if (count > 0){
                    tvToast.setVisibility(View.INVISIBLE);
                    tvToast.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.i(" loginstatus = "+loginStatus);
        if (loginStatus){
            Preferences.saveLoginStatus(true);
        }else{
            Preferences.saveLoginStatus(false);
        }
    }

    @OnClick({R.id.bt_go, R.id.fab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_go:
                LogUtils.i("登录");
                tvToast.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(etUsername.getEditText().getText().toString())){
                    tvToast.setText(getString(R.string.login_name_error));
                    return;
                }
                if (TextUtils.isEmpty(etPassword.getEditText().getText().toString())) {
                    tvToast.setText(getString(R.string.login_password_error));
                    return;
                }


                int ret = SmartHomeNativeUtils.verify(etUsername.getEditText().getText().toString(), etPassword.getEditText().getText().toString());

                if (ret == 0) {
                    Intent intent = new Intent(Actions.USER_LOGIN_ACTION);
                    intent.putExtra("name", etUsername.getEditText().getText().toString().trim());
                    sendBroadcast(intent);
                    loginStatus = true;
                    finish();
                } else {
                    loginStatus = false;
                    tvToast.setText(getString(R.string.login_name_password_verify_error));
                    Preferences.saveLoginStatus(false);
                }

                break;
            case R.id.fab:
                LogUtils.i("注册");
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //平滑的将一个控件平移的过渡到第二个activity
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, fab, fab.getTransitionName());
                    startActivity(new Intent(this, RegisterActivity.class), options.toBundle());
                } else {
                    startActivity(new Intent(this, RegisterActivity.class));
                }
                break;
        }
    }
}
