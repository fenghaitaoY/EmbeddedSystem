package com.android.blue.smarthomefunc.activity;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.android.blue.smarthomefunc.LogUtils;
import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.adapter.RecycleAdapter;
import com.android.blue.smarthomefunc.entity.BleDeviceEntity;
import com.android.blue.smarthomefunc.entity.HCBluetoothControl;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchAddDeviceActivity extends BaseActivity {
    @BindView(R.id.search_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.search_device)
    Button mSearchBt;

    @BindView(R.id.search_content_view)
    RecyclerView mRecyclerView;

    List<BleDeviceEntity> mListEntitys = new ArrayList<>();
    boolean loading=false;
    RecycleAdapter mAdapter;

    private HCBluetoothControl mHcControl;

    //显示搜索框
    private Dialog mSearchDialog;

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

        //RecyclerView
        mAdapter = new RecycleAdapter(this, mListEntitys);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mHcControl = HCBluetoothControl.getInstance(this);
        mHcControl.setOnHcBluetoothListener(new BluetoothControlListener());

        mSearchDialog = createDialog();


        mAdapter.setmOnItemClickListener(new RecycleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LogUtils.i("recycadapter item click");
            }

            @Override
            public void onItemLongClick(View view, int position) {
                LogUtils.i("recycadapter item long click");
            }
        });

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
                mListEntitys.clear();
                mAdapter.notifyDataSetChanged();
                mSearchDialog.show();
                mHcControl.scanLeDevice(true);

            }
        });

        mSearchDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                LogUtils.i(" cancel dialog ");
                mHcControl.scanLeDevice(false);
            }
        });

        /*for (int i =0;i<5;i++){
            BleDeviceEntity be= new BleDeviceEntity();
            be.setDeviceName("dd"+i);
            be.setStatusRssi("-"+i);
            be.setDeviceAddress("address"+i);
            mListEntitys.add(be);
        }*/
    }



    public Dialog createDialog(){
        Dialog mDialog = new Dialog(this);
        LayoutInflater inflater= getLayoutInflater();
        View view = inflater.inflate(R.layout.search_device_dialog, null);
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.alpha = 0.3f;
        mDialog.getWindow().setAttributes(lp);
        mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//去掉这句话，背景会变暗
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mDialog.setContentView(view);


        return mDialog;
    }


    class BluetoothControlListener implements HCBluetoothControl.OnHcBluetoothListener {
        @Override
        public void scanBluetoothDevice(BluetoothDevice device, int rssi) {
            LogUtils.i(" scan blue device "+device.getAddress());
            BleDeviceEntity mEntity = new BleDeviceEntity();
            mEntity.setDeviceName(device.getName());
            mEntity.setDeviceAddress(device.getAddress());
            mEntity.setStatusRssi(rssi+"");
            if (!mListEntitys.contains(mEntity)) {
                mListEntitys.add(mEntity);
                mAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void stopScan() {
            LogUtils.i("stop scan");
            mSearchDialog.dismiss();
        }

        @Override
        public void startScan() {

        }
    }
}
