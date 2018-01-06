package com.android.blue.smarthomefunc.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.android.blue.smarthomefunc.LogUtils;
import com.android.blue.smarthomefunc.R;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchAddDeviceActivity extends BaseActivity {
    @BindView(R.id.search_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.search_device)
    Button mSearchBt;


    boolean loading=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_add_device);
        ButterKnife.bind(this);
        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }

        //添加Toolbar设置
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.add_device));
        //返回键设置
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtils.i("点击");
                finish();
            }
        });

        mSearchBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(loading){
                    loading=false;
                    showDialog();
                }else{
                    loading = true;
                    showDialog();
                }
            }
        });

    }



    public void showDialog(){
        Dialog mDialog = new Dialog(this);
        LayoutInflater inflater= getLayoutInflater();
        View view = inflater.inflate(R.layout.search_device_dialog, null);
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.alpha = 0.3f;
        mDialog.getWindow().setAttributes(lp);
        mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//去掉这句话，背景会变暗
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mDialog.setContentView(view);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                LogUtils.i("cancel dialog");
            }
        });
        if (!loading) {
            mDialog.show();
        }else {
            mDialog.dismiss();
        }
    }
}
