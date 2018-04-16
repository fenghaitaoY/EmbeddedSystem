package com.android.blue.smarthomefunc.activity;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.blue.smarthomefunc.adapter.OnItemClickListener;
import com.android.blue.smarthomefunc.application.AppCache;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.adapter.RecycleAdapter;
import com.android.blue.smarthomefunc.database.DBinfo;
import com.android.blue.smarthomefunc.entity.BleDeviceEntity;
import com.android.blue.smarthomefunc.entity.HCBluetoothControl;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchAddDeviceActivity extends BaseActivity {

    @BindView(R.id.search_device)
    Button mSearchBt;

    @BindView(R.id.search_content_view)
    RecyclerView mRecyclerView;

    List<BleDeviceEntity> mListEntitys = new ArrayList<>();
    boolean loading = false;
    RecycleAdapter mAdapter;

    //添加设备弹窗
    EditText mAddDevieModeEt;
    TextView mCancelBt;
    TextView mAddBt;

    private HCBluetoothControl mHcControl;

    //显示搜索框
    private Dialog mSearchDialog;

    private Dialog mAddDeviceDialog;
    private int mSelectPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_add_device);

        //RecyclerView
        mAdapter = new RecycleAdapter(this, mListEntitys);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mHcControl = HCBluetoothControl.getInstance(this);
        mHcControl.setOnHcBluetoothListener(new BluetoothControlListener());

        mSearchDialog = createSearchDialog();

        mAddDeviceDialog = createAddDeviceDialog();

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LogUtils.i("recycadapter item click position=" + position + ", address=" + mListEntitys.get(position).getDeviceAddress());
                boolean isexist = false;
                if (AppCache.get().getBleDeviceList().size() > 0) {
                    for (BleDeviceEntity entity : AppCache.get().getBleDeviceList()) {
                        if (entity.getDeviceAddress().equals(mListEntitys.get(position).getDeviceAddress())) {
                            isexist = true;
                            break;
                        } else {
                            isexist = false;
                        }
                    }
                }
                if (isexist){
                    Toast.makeText(getApplicationContext(), "已经存在", Toast.LENGTH_SHORT).show();
                }else{
                    mAddDeviceDialog.show();
                    mSelectPosition = position;
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                LogUtils.i("recycadapter item long click");
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

    }


    public Dialog createSearchDialog() {
        Dialog mDialog = new Dialog(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.search_device_dialog, null);
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.alpha = 0.3f;
        mDialog.getWindow().setAttributes(lp);
        mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//去掉这句话，背景会变暗
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mDialog.setContentView(view);


        return mDialog;
    }

    public Dialog createAddDeviceDialog() {
        Dialog dialog = new Dialog(this, R.style.BottomDialogStyle);
        LayoutInflater inflater = getLayoutInflater();
        View rootView = inflater.inflate(R.layout.search_add_model, null);
        dialog.setContentView(rootView);
        mCancelBt = rootView.findViewById(R.id.search_dialog_add_device_cancel_bt);
        mAddBt = rootView.findViewById(R.id.search_dialog_add_device_add_bt);
        mAddDevieModeEt = rootView.findViewById(R.id.search_dialog_add_device_et);

        mCancelBt.setOnClickListener(dialogListener);
        mAddBt.setOnClickListener(dialogListener);

        ViewGroup.LayoutParams lp = rootView.getLayoutParams();
        lp.width = getResources().getDisplayMetrics().widthPixels;
        rootView.setLayoutParams(lp);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);

        return dialog;
    }


    private View.OnClickListener dialogListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.search_dialog_add_device_cancel_bt:
                    LogUtils.i("cancel");

                    break;
                case R.id.search_dialog_add_device_add_bt:
                    LogUtils.i("add");
                    String modelName = mAddDevieModeEt.getText().toString();
                    if(!TextUtils.isEmpty(modelName)){
                        BleDeviceEntity bleEntity = mListEntitys.get(mSelectPosition);
                        ContentValues values = new ContentValues();
                        values.put(DBinfo.Table.TABLE_COLUMN_MODE_NAME, modelName);
                        values.put(DBinfo.Table.TABLE_COLUMN_DEVICE_NAME, bleEntity.getDeviceName());
                        values.put(DBinfo.Table.TABLE_COLUMN_DEVICE_ADDRESS, bleEntity.getDeviceAddress());
                        values.put(DBinfo.Table.TABLE_COLUMN_RSSI, Integer.valueOf(bleEntity.getStatusRssi()));
                        values.put(DBinfo.Table.TABLE_COLUMN_SWITCH_STATUS, 0);
                        Uri uri = Uri.parse("content://com.android.blue.smarthomeprovider/device");

                        //读出数据库已经存在的，与之对比蓝牙地址，如果已经存在，取消添加并提示已经加入
                        Cursor cursor = getContentResolver().query(uri,null, null, null,null);
                        LogUtils.i(" database count"+cursor.getCount());
                        boolean isAlreadyRegister = false;
                        if (cursor != null) {
                            if (cursor.getCount()>0){
                                for (cursor.moveToFirst(); !cursor.isAfterLast();cursor.moveToNext()){
                                    if(bleEntity.getDeviceAddress().equals(
                                            cursor.getString(cursor.getColumnIndex(DBinfo.Table.TABLE_COLUMN_DEVICE_ADDRESS)))){

                                        isAlreadyRegister = true;
                                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_already_register_device),Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }

                            if (cursor.getCount() ==0 || !isAlreadyRegister){
                                LogUtils.i("insert database");
                                getContentResolver().insert(uri, values);
                            }
                        }
                    }
                    break;
            }
            mAddDevieModeEt.setText("");
            mAddDeviceDialog.dismiss();
        }
    };



    class BluetoothControlListener implements HCBluetoothControl.OnHcBluetoothListener {
        @Override
        public void scanBluetoothDevice(BluetoothDevice device, int rssi) {
            LogUtils.i(" scan blue device " + device.getAddress());
            BleDeviceEntity mEntity = new BleDeviceEntity();
            mEntity.setDeviceName(device.getName());
            mEntity.setDeviceAddress(device.getAddress());
            mEntity.setStatusRssi(rssi + "");
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
