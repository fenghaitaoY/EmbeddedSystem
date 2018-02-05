package com.android.blue.smarthomefunc.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.android.blue.smarthomefunc.entity.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Created by root on 12/18/17.
 */

public class BluetoothLeService extends Service {
    private List<Sensor> mEnabledSensors = new ArrayList<>();
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING =1;
    private static final int STATE_CONNECTED =2;


    public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";

    private OnDataAvailableListener mOnDataAvailableListener;

    private IBinder mBinder = new LocalBinder();

    public interface OnDataAvailableListener{
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);
    }

    public void setmOnDataAvailableListener(OnDataAvailableListener listener){
        mOnDataAvailableListener = listener;
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {//连接成功
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                //通过广播通知activity更新连接状态
                LogUtils.i("Connected to GATT server");
                broadcastUpdate(intentAction);
                LogUtils.i("Attempting to start service discovery ");
                //此处调用会回调onServicesDiscovered
                mBluetoothGatt.discoverServices(); //扫描所有服务
            }else if (newState == BluetoothProfile.STATE_DISCONNECTED){//连接失败
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                LogUtils.i("Disconnected from GATT server");
                broadcastUpdate(intentAction);
            }
        }

        /**
         * 重写，发现蓝牙服务
         * @param gatt
         * @param status
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS){ //发现服务
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                LogUtils.i("---onServicesDiscovered called ---");
            }else{
                LogUtils.i("---onServicesDisCovered received = "+status);

            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS){
                LogUtils.i("---onCharacteristicRead called---");
                byte[] sucChars = characteristic.getValue();
                String strChar = new String(sucChars);
                LogUtils.i("---onCharacteristicRead strchar = "+strChar);
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            LogUtils.i("---onCharacteristicChanged----");
            broadcastUpdate(ACTION_DATA_AVAILABLE,characteristic);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            LogUtils.i("----onCharacteristicWrite-- status ="+status);
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            LogUtils.i("-----onDescriptorRead---");
            byte[] desc = descriptor.getValue();
            LogUtils.i("---onDescriptorRead ---value = "+new String(desc));
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            LogUtils.i("---------onDescriptorWrite-------");
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            LogUtils.i("-------onReadRemoteRssi----");
            broadcastUpdate(ACTION_DATA_AVAILABLE, rssi);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            LogUtils.i("---onReliableWriteCompleted---status=="+status);
        }
    };

    private void broadcastUpdate(final String action, int rssi){
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_DATA, String.valueOf(rssi));
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action){
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic){
        final Intent intent = new Intent(action);
        //拿到传输数据
        final byte[] data = characteristic.getValue();
        if(data !=null && data.length >0){
            final StringBuilder mStringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data){
                mStringBuilder.append(String.format("%02X ",byteChar));
                LogUtils.i("-----broadcastUpdate: byteChar ="+byteChar);
            }
            try {
                intent.putExtra(EXTRA_DATA, new String(data, 0, data.length, "GB2312"));//手机跟单片机传输编码方式不同　汉字
            }catch (Exception e){

            }
        }
        sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.i("BluetoothService bind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    public class LocalBinder extends Binder{
        public BluetoothLeService getService(){
            return BluetoothLeService.this;
        }
    }

    /**
     * service 蓝牙初始化
     * @return
     */
    public boolean initialize(){
        if (mBluetoothManager == null){
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null){
                LogUtils.e("unable to initialize BluetoothManager");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter(); //通过BluetoothManager获得BluetoothAdapter
        if (mBluetoothAdapter == null){
            LogUtils.e("unable to obtain a BluetoothAdapter");
            return false;
        }
        return true;
    }

    /**
     * 连接蓝牙
     * @param address
     * @return
     */
    public boolean connect(final String address){
        LogUtils.i("------connect address :"+address);
        if (mBluetoothAdapter == null || address == null){
            LogUtils.e("BluetoothAdapter not initialized or unspecified address");
            return false;
        }
        //之前连接过设备，　重新连接
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null){
            LogUtils.i("Trying to use an existing mBluetoothGatt for connection");
            if (mBluetoothGatt.connect()) { //连接蓝牙，其实就是调用BluetoothGatt的连接方法
                mConnectionState = STATE_CONNECTED;
                return true;
            }else{
                return false;
            }
        }
        //获取远端的蓝牙设备
        final BluetoothDevice mDevice = mBluetoothAdapter.getRemoteDevice(address);
        if (mDevice == null){
            LogUtils.e("Device not found , unable to connect");
            return false;
        }

        mBluetoothGatt = mDevice.connectGatt(this, false, mGattCallback); //建立连接
        LogUtils.d("Trying to create a new connection");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTED;
        mDevice.getBondState();
        return true;
    }

    /**
     * 断开蓝牙连接
     */
    public void disconnect(){
        if (mBluetoothAdapter == null || mBluetoothGatt == null){
            LogUtils.i("BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * 关闭蓝牙连接
     */
    public void close(){
        if (mBluetoothGatt == null){
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * 读取特征值
     * @param characteristic
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic){
        if (mBluetoothAdapter == null || mBluetoothGatt == null){
            LogUtils.i("BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * 写入特征值
     * @param characteristic
     */
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic){
        if (mBluetoothAdapter == null || mBluetoothGatt == null){
            LogUtils.i("BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    /**
     * 读取Rssi
     */
    public void readRssi(){
        if (mBluetoothAdapter == null || mBluetoothGatt == null){
            LogUtils.i("BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readRemoteRssi();
    }

    /**
     * 设置特征值变化通知
     * @param characteristic
     * @param enabled
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled){
        if (mBluetoothAdapter == null || mBluetoothGatt == null){
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));

        if (enabled){
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        }else{
            descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        }
        mBluetoothGatt.writeDescriptor(descriptor);
    }

    /**
     * 得到特征值下的描述值
     * @param descriptor
     */
    public void getCharacteristicDescriptor(BluetoothGattDescriptor descriptor){
        if (mBluetoothAdapter == null || mBluetoothGatt == null){
            LogUtils.i("Bluetoothadapter not initialized");
            return;
        }
        mBluetoothGatt.readDescriptor(descriptor);
    }

    /**
     * 获取服务列表
     * @return
     */
    public List<BluetoothGattService> getSupportedGattServices(){
        if (mBluetoothGatt == null){
            return null;
        }
        return mBluetoothGatt.getServices();
    }

}
