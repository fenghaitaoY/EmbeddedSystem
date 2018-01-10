package com.android.blue.smarthomefunc.entity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;

import com.android.blue.smarthomefunc.BluetoothLeService;
import com.android.blue.smarthomefunc.LogUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by root on 1/9/18.
 */

public class HCBluetoothControl {

    private List<BleDeviceEntity> mDevices = new ArrayList<>();
    //蓝牙适配器
    BluetoothAdapter mBluetoothAdapter;
    //蓝牙信号强度
    private ArrayList<Integer> rssis;
    private ArrayList<BluetoothDevice> mListDevice;
    private ArrayList<BleDeviceEntity> mControlDevies;

    //扫描蓝牙的状态
    private boolean mScanning;
    private Handler mHandler;
    private Context mContext;
    private String mSendMsg;

    //蓝牙扫描时间
    private static final long SCAN_PERIOD = 10000;

    //接收蓝牙状态切换广播
    private BluetoothStateChangeBroadcastReceiver mBlueChangeBroadcast;

    //蓝牙控制地址
    private ArrayList<String> blueAddress = new ArrayList<>();

    //蓝牙4.0的UUID,其中0000ffe1-0000-1000-8000-00805f9b34fb是广州汇承信息科技有限公司08蓝牙模块的UUID
    public static String HEART_RATE_MEASUREMENT = "0000ffe1-0000-1000-8000-00805f9b34fb";
    //蓝牙连接状态
    private boolean mConnected = false;
    private String status = "disconnected";

    //蓝牙服务
    private static BluetoothLeService mBluetoothLeService;

    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

    //蓝牙特征值
    private static BluetoothGattCharacteristic targetChara = null;

    //蓝牙回调
    private OnHcBluetoothListener mOnHcBluetoothListener;
    //定时一段时间停止扫描
    private ScanRunnable mScanRunnable;

    private static HCBluetoothControl INSTANCE = null;

    private HCBluetoothControl(Context context) {
        mContext = context;

        mHandler = new Handler();
        rssis = new ArrayList<>();
        mListDevice = new ArrayList<>();
        mControlDevies = new ArrayList<>();

        mScanRunnable = new ScanRunnable();

        //注册蓝牙状态广播
        IntentFilter mBlueStateFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mBlueChangeBroadcast = new BluetoothStateChangeBroadcastReceiver();
        mContext.registerReceiver(mBlueChangeBroadcast, mBlueStateFilter);
        mContext.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        //获取手机本地蓝牙适配
        final BluetoothManager mBlueManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBlueManager.getAdapter();

        /* 启动蓝牙service */
        Intent gattServiceIntent = new Intent(mContext, BluetoothLeService.class);
        mContext.bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    public static HCBluetoothControl getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (HCBluetoothControl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HCBluetoothControl(context);
                }
            }
        }
        return INSTANCE;
    }


    /* BluetoothLeService绑定的回调函数 */
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) iBinder).getService();
            if (!mBluetoothLeService.initialize()) {
                LogUtils.i("Unable to initialized Bluetooth");
                //finish();
            }
            LogUtils.i(" 蓝牙连接　");
            //mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }

    };

    /**
     * 发送指令
     *
     * @param msg
     */
    public void startSendMsg(String msg) {
        mSendMsg = msg;
        new SendMessageThread().start();
    }

    /**
     * 获得蓝牙设备列表
     *
     * @return
     */
    public List<BleDeviceEntity> getBluetoothHcDevice() {

        return mControlDevies;
    }

    /**
     * 添加用户需要控制的蓝牙地址，绑定控制
     *
     * @param address
     * @return -1 添加失败　size 目前设备数量
     */
    public int addBluetoothAdress(String address) {
        if (address.isEmpty() && blueAddress.contains(address)) {
            return -1;
        }

        blueAddress.add(address);
        return blueAddress.size();
    }


    /**
     * 删除用户控制的蓝牙设备地址，解绑控制
     *
     * @param address
     * @return　-1 删除失败　size 目前存在设备数量
     */
    public int removeBluetoothAdress(String address) {
        if (address.isEmpty() && !blueAddress.contains(address)) {
            return -1;
        }

        blueAddress.remove(address);
        return blueAddress.size();
    }

    /**
     * 设备连接
     * @return
     */
    public boolean connectDevice(final String address) {
        if (blueAddress.isEmpty()) {
            return false;
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mBluetoothLeService != null && mBluetoothLeService.initialize()) {
                    LogUtils.i("--connect--");
                    mBluetoothLeService.connect(address);
                }
            }
        }, 200);

        return true;
    }



    public boolean isBluetoothConnect(){
        return false;
    }

    /**
     * 发送数据中文需要转化格式，
     * 引文ＨＣ蓝牙模块单次最大写入长度２０byte, 当edittext长度大于限制长度，需要循环发送
     * 本文采用判断块的方式，首先判断长度，大于20byte做分流，小于就直接发送出去
     */
    public class SendMessageThread extends Thread {
        @Override
        public void run() {
            int i, j;
            if (mSendMsg.isEmpty()){
                LogUtils.i("send a empty cmd ");
                return;
            }
            try {
                byte[] sendMessages = mSendMsg.getBytes("GB2312");
                byte[] msgByte = new byte[20];
                if (sendMessages.length >= 20) {
                    for (i = 0; i < sendMessages.length / 20; i++) { //因为蓝牙支持最大单次20字节，所以切成块传输
                        for (j = 0; j < 20; j++) {
                            msgByte[j] = sendMessages[i * 20 + j];
                        }
                        targetChara.setValue(msgByte);
                        mBluetoothLeService.writeCharacteristic(targetChara);
                    }
                    if (sendMessages.length % 20 != 0) {   //判断传输内容长度有剩下不足一块的
                        for (i = 0; i < sendMessages.length % 20; i++) { //块传输剩下不足一块的
                            msgByte[i] = sendMessages[(sendMessages.length / 20) * 20 + i];
                        }
                        targetChara.setValue(msgByte);
                        mBluetoothLeService.writeCharacteristic(targetChara);
                    }
                } else {
                    targetChara.setValue(sendMessages);
                    mBluetoothLeService.writeCharacteristic(targetChara);
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }

    public void scanLeDevice(final boolean enable) {
        if (enable) {
            LogUtils.i("scan le begin..");
            mHandler.postDelayed(mScanRunnable, SCAN_PERIOD);
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            mOnHcBluetoothListener.startScan();
        } else {
            LogUtils.i("stoping scan ");
            mScanning = false;
            mHandler.removeCallbacks(mScanRunnable);
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mListDevice.clear();
            mOnHcBluetoothListener.stopScan();
        }

    }

    /**
     * 实现扫描runnable，解决重复调用scanLeDevice，延迟扔存在，导致短时间停止扫描问题
     */
    class ScanRunnable implements Runnable {
        @Override
        public void run() {
            if (mScanning) {
                mScanning = false;
                LogUtils.i("定时一段时间后　stop scan le device");
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mListDevice.clear();
                mOnHcBluetoothListener.stopScan();
            }
        }
    }

    /**
     * 蓝牙扫描回调函数，　实现扫描蓝牙设备
     */
    public BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice bluetoothDevice, final int rssi, byte[] bytes) {

            if (!mListDevice.contains(bluetoothDevice)) {
                if (mOnHcBluetoothListener != null){
                    mOnHcBluetoothListener.scanBluetoothDevice(bluetoothDevice, rssi);
                }
                mListDevice.add(bluetoothDevice);
                rssis.add(rssi);
            }

            LogUtils.i("  Address : " + bluetoothDevice.getAddress() + ", Name:" + bluetoothDevice.getName() + " , rssi:" + rssi);
        }
    };


    public class BluetoothStateChangeBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            switch (state) {
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


    /**
     * 广播接收器，负责接收BluetoothLeService类发送的数据
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action))//Gatt连接成功
            {
                mConnected = true;
                status = "connected";
                //更新连接状态
                updateConnectionState(status);
                LogUtils.i("BroadcastReceiver :" + "device connected");

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED//Gatt连接失败
                    .equals(action)) {
                mConnected = false;
                status = "disconnected";
                //更新连接状态
                updateConnectionState(status);
                LogUtils.i("BroadcastReceiver :"
                        + "device disconnected");

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED//发现GATT服务器
                    .equals(action)) {
                // Show all the supported services and characteristics on the
                // user interface.
                //获取设备的所有蓝牙服务
                displayGattServices(mBluetoothLeService
                        .getSupportedGattServices());
                LogUtils.i("BroadcastReceiver :"
                        + "device SERVICES_DISCOVERED");
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action))//有效数据
            {
                //处理发送过来的数据
                LogUtils.i("BroadcastReceiver onData:"
                        + intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    /* 更新连接状态 */
    private void updateConnectionState(String status) {

    }

    /* 意图过滤器 */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


    /**
     * 处理蓝牙服务,解析出有哪些服务，服务里有哪些Characteristic，哪些Characteristic可读可写可发通知等等
     *
     * @param gattServers
     */
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) {
            return;
        }
        String uuid = null;

        for (BluetoothGattService gattService : gattServices) { //遍历所有服务
            //获取服务列表
            uuid = gattService.getUuid().toString();
            LogUtils.i(" Ble Activity displayGattServices uuid = " + uuid);
            //从当前循环所指向的服务中读取特征值列表
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();

            for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {//遍历每条服务里的所有Characteristic

                uuid = gattCharacteristic.getUuid().toString();
                LogUtils.i(" Ble Activity uuid = " + uuid);
                if (uuid.equals(HEART_RATE_MEASUREMENT)) {
                    //测试读取当前Characteristic数据, 会触发mOnDataAvailable.onCharacteristicRead();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mBluetoothLeService.readCharacteristic(gattCharacteristic);
                        }
                    }, 200);
                    //接受通知的UUID，收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
                    mBluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);
                    targetChara = gattCharacteristic;
                }
                List<BluetoothGattDescriptor> descriptors = gattCharacteristic.getDescriptors();
                for (BluetoothGattDescriptor descriptor : descriptors) {
                    LogUtils.i("Ble Activity descriptor UUID: " + descriptor.getUuid());
                    //获取特征值的描述, 可读的UUID
                    mBluetoothLeService.getCharacteristicDescriptor(descriptor);
                }
            }
        }
    }

    public void setOnHcBluetoothListener(OnHcBluetoothListener listener){
        mOnHcBluetoothListener = listener;
    }

    public interface OnHcBluetoothListener{
        //扫描蓝牙设备
        void scanBluetoothDevice(BluetoothDevice device, int rssi);
        //蓝牙扫描结束通知
        void stopScan();
        //蓝牙扫描开始通知
        void startScan();
    }
}
