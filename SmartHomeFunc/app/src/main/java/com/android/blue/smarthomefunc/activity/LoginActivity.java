package com.android.blue.smarthomefunc.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.android.blue.smarthomefunc.LogUtils;
import com.android.blue.smarthomefunc.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class LoginActivity extends BaseActivity {


    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.bt_go)
    Button btGo;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);//注解

    }


    @OnClick({R.id.bt_go, R.id.fab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_go:
                LogUtils.i("登录");

                Intent loginIntent = new Intent(this, LoginSuccessActivity.class);
                startActivity(loginIntent);
                break;
            case R.id.fab:
                LogUtils.i("注册");
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    //平滑的将一个控件平移的过渡到第二个activity
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,fab,fab.getTransitionName());
                    startActivity(new Intent(this, RegisterActivity.class),options.toBundle());
                }else{
                    startActivity(new Intent(this, RegisterActivity.class));
                }
                break;
        }
    }
}
