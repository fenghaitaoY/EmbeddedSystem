package com.android.blue.smarthomefunc.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;

import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            ShowEnterAnimation();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 进入显示动画效果
     */
    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition); //进入效果

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

    public void animateRevealShow(){
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd,cvAdd.getWidth()/2,0,mFab.getWidth()/2,cvAdd.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());//设置动画播放速度, 加速度
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                cvAdd.setVisibility(View.VISIBLE);
            }
        });
        mAnimator.start();

    }

    public void animateRevealClose(){
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth()/2,0,cvAdd.getHeight(),mFab.getWidth()/2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                cvAdd.setVisibility(View.INVISIBLE);
                mFab.setImageResource(R.drawable.plus);
                onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        animateRevealClose();
    }

    @OnClick({R.id.bt_go, R.id.fab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_go:
                LogUtils.i("--注册--");
                break;
            case R.id.fab:
                LogUtils.i("--关闭注册--");
                animateRevealClose();
                break;
        }
    }
}
