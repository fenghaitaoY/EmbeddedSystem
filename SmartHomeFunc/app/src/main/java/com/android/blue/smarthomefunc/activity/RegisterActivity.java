package com.android.blue.smarthomefunc.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.jninative.SmartHomeNativeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.et_username)
    EditText mUsernameEdit;
    @BindView(R.id.et_email)
    EditText mEmailEdit;
    @BindView(R.id.et_password)
    EditText mPasswordEdit;
    @BindView(R.id.bt_go)
    Button mRegistBt;
    @BindView(R.id.cv_add)
    CardView cvAdd;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    Transition transition;
    @BindView(R.id.tv_toast)
    TextView tvToast;
    @BindView(R.id.frame_layout)
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition); //进入效果

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ShowEnterAnimation();
                }
            });

        }
        LogUtils.i("");

        mPasswordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                if (count > 0) {
                    hideErrorToast(tvToast);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mUsernameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                if (count > 0) {
                    hideErrorToast(tvToast);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 进入显示动画效果
     */
    private void ShowEnterAnimation() {


        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                LogUtils.i("--onTransitionStart---");
                cvAdd.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                LogUtils.i("--onTransitionEnd--");
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });

    }

    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth() / 2, 0, mFab.getWidth() / 2, cvAdd.getHeight());
        mAnimator.setDuration(200);
        mAnimator.setInterpolator(new AccelerateInterpolator());//设置动画播放速度, 加速度
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cvAdd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        LogUtils.i("----------");
        mAnimator.start();

    }

    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth() / 2, 0, cvAdd.getHeight(), mFab.getWidth() / 2);
        mAnimator.setDuration(200);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvAdd.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                mFab.setImageResource(R.drawable.plus);
                RegisterActivity.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        LogUtils.i("----------");
        mAnimator.start();
    }

    @Override
    public void onBackPressed() {
        animateRevealClose();
    }

    @OnClick({R.id.bt_go, R.id.fab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_go:
                LogUtils.i("--注册--");
                int result = -1;
                if (!TextUtils.isEmpty(mUsernameEdit.getText().toString())
                        && !TextUtils.isEmpty(mPasswordEdit.getText().toString())) {
                    result = SmartHomeNativeUtils.save(mUsernameEdit.getText().toString(), mPasswordEdit.getText().toString());
                } else {
                    if (TextUtils.isEmpty(mUsernameEdit.getText().toString())) {
                        showErrorToast(tvToast, getString(R.string.login_name_error));
                    } else if (TextUtils.isEmpty(mPasswordEdit.getText().toString())) {
                        showErrorToast(tvToast, getString(R.string.login_password_error));
                    }
                }
                if (result == 0) {
                    animateRevealClose();
                }
                break;
            case R.id.fab:
                LogUtils.i("--关闭注册--");
                animateRevealClose();
                break;
        }
    }



    private void showErrorToast(TextView tv, String errStr){
        tv.setVisibility(View.VISIBLE);
        tv.setText(errStr);
    }

    private void hideErrorToast(TextView tv){
        tv.setVisibility(View.INVISIBLE);
        tv.setText("");
    }
}
