package com.android.blue.smarthomefunc;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.blue.smarthomefunc.entity.LogUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button scan_btn;
    //蓝牙适配器
    BluetoothAdapter mBluetoothAdapter;
    //蓝牙信号强度
    private ArrayList<Integer> rssis;
    //自定义Adapter
    LeDeviceListAdapter mLeDeviceListAdapter;
    //listview显示扫描的蓝牙信息
    ListView mListView;
    //扫描蓝牙的状态
    private boolean mScanning;
    private boolean scan_flag;
    private Handler mHandler;
    int REQUEST_ENABLE_BT = 1;
    //蓝牙扫描时间
    private static final long SCAN_PERIOD = 10000;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    //接收蓝牙状态切换广播
    private BluetoothStateChangeBroadcastReceiver mBlueChangeBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }
        initView(); //初始化控件

        initBluetooth(); //初始化蓝牙
        scan_flag = true;
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        mListView.setAdapter(mLeDeviceListAdapter); //ListView 添加适配器

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
                if (device == null){
                    return;
                }
                Intent intent = new Intent(MainActivity.this, Ble_Activity.class);
                intent.putExtra(Ble_Activity.EXTRAS_DEVICE_NAME, device.getName());
                intent.putExtra(Ble_Activity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
                intent.putExtra(Ble_Activity.EXTRAS_DEVICE_RSSI, rssis.get(position).toString());
                if (mScanning){
                    //停止扫描
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mScanning = false;
                }
                startActivity(intent);
            }
        });

        //注册蓝牙状态广播
        IntentFilter mBlueStateFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mBlueChangeBroadcast = new BluetoothStateChangeBroadcastReceiver();
        registerReceiver(mBlueChangeBroadcast, mBlueStateFilter);

    }

    private void initBluetooth() {
        //检测硬件是否支持
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            Toast.makeText(this, "不支持BLE", Toast.LENGTH_SHORT).show();
            finish();
        }
        //获取手机本地蓝牙适配
        final BluetoothManager mBlueManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBlueManager.getAdapter();
        //打开蓝牙
        if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private void initView() {
        scan_btn = findViewById(R.id.scan_dev_btn);
        scan_btn.setOnClickListener(this);
        mListView = findViewById(R.id.lv);
        mHandler = new Handler();
    }

    @Override
    public void onClick(View view) {
        if (scan_flag){
            mLeDeviceListAdapter = new LeDeviceListAdapter();
            mListView.setAdapter(mLeDeviceListAdapter);
            scanLeDevice(true);
            LogUtils.i("onClick scanLeDevice true");
        }else{
            scanLeDevice(false);
            scan_btn.setText("扫描设备");
            LogUtils.i("onClick scanLeDevice false");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBlueChangeBroadcast);
    }

    public void scanLeDevice(final boolean enable){
        if (enable){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    scan_flag = true;
                    scan_btn.setText("扫描设备");
                    LogUtils.i("定时一段时间后　stop scan le device");
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);

                }
            }, SCAN_PERIOD);
            LogUtils.i("scan le begin..");
            mScanning = true;
            scan_flag = false;
            scan_btn.setText("停止扫描");
            mBluetoothAdapter.startLeScan(mLeScanCallback);

        }else{
            LogUtils.i("stoping scan ");
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            scan_flag = true;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT){
            if (resultCode == RESULT_OK){
                LogUtils.i("蓝牙已经开启");
                String localBlueName = mBluetoothAdapter.getName();
                String localBlueAddress = mBluetoothAdapter.getAddress();
                Toast.makeText(getApplicationContext(),"Local name:"+localBlueName+" ,Local Address:"+localBlueAddress, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // TODO request success
                }
                break;
        }
    }

    /**
     * 蓝牙扫描回调函数，　实现扫描蓝牙设备
     */
    public BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice bluetoothDevice, final int rssi, byte[] bytes) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceListAdapter.addDevice(bluetoothDevice,rssi);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            });
            LogUtils.i("Address : "+bluetoothDevice.getAddress()+", Name:"+bluetoothDevice.getName()+" , rssi:"+rssi);
        }
    };

    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflater;

        public LeDeviceListAdapter(){
            rssis = new ArrayList<>();
            mLeDevices = new ArrayList<>();
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        public void addDevice(BluetoothDevice device, int rssi){
            //因为扫描会重复出现所以要过滤下
            LogUtils.i(" Adapter device ="+device.toString());
            if (!mLeDevices.contains(device)){
                mLeDevices.add(device);
                rssis.add(rssi);
            }
        }

        public BluetoothDevice getDevice(int position){
            return mLeDevices.get(position);
        }

        public void clear(){
            mLeDevices.clear();
            rssis.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null){
                holder = new ViewHolder();
                view = mInflater.inflate(R.layout.listitem,null);
                holder.deviceAddress = view.findViewById(R.id.tv_deviceAddr);
                holder.deviceName = view.findViewById(R.id.tv_deviceName);
                holder.rssi = view.findViewById(R.id.tv_rssi);
                view.setTag(holder);
            }else{
                holder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            holder.deviceAddress.setText(device.getAddress());
            holder.deviceName.setText(device.getName());
            holder.rssi.setText(""+rssis.get(i));


            return view;
        }
    }

    public final class ViewHolder{
        public TextView deviceAddress;
        public TextView deviceName;
        public TextView rssi;
    }


    public class BluetoothStateChangeBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,-1);
            switch (state){
                case BluetoothAdapter.STATE_ON:
                    LogUtils.i("蓝牙打开");
                    break;
                case BluetoothAdapter.STATE_OFF:
                    LogUtils.i("蓝牙关闭");
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    LogUtils.i("蓝牙正在打开");
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    LogUtils.i("蓝牙正在关闭");
                    break;

            }
        }
    }


}
