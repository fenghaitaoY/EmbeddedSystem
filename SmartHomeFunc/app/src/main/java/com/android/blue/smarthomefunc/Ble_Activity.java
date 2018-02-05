package com.android.blue.smarthomefunc;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.service.BluetoothLeService;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Ble_Activity extends AppCompatActivity implements View.OnClickListener{
//蓝牙４.0的ＵＵＩＤ其中ffe1是广州汇承HC-08蓝牙模块的uuid
    public static String HEART_RATE_MEASUREMENT = "0000ffe1-0000-1000-8000-00805f9b34fb";

    public static String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static String EXTRAS_DEVICE_RSSI = "RSSI";

    //蓝牙连接状态
    private boolean mConnected = false;
    private String status = "disconnected";

    private String mDeviceName;
    private String mDeviceAddress;
    private String mRssi;

    private Bundle mBundle;

    private String revString = "";
    //蓝牙服务
    private BluetoothLeService mBluetoothLeService;
    //Textview　显示接收的内容
    private TextView revTextView;
    private TextView connectState;
    private Button mSendButton;
    private EditText mSendEdit;
    private ScrollView mScrollView;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    //蓝牙特征值
    private static BluetoothGattCharacteristic targetChara = null;
    private Handler mHandler = new Handler();
    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    String state = msg.getData().getString("connect_state");
                    connectState.setText(state);
                    break;
            }
        }
    };

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder)iBinder).getService();
            if (!mBluetoothLeService.initialize()){
                LogUtils.i("Unable to initialized Bluetooth");
                finish();
            }
            LogUtils.i(" 蓝牙连接　");
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
        mBundle = getIntent().getExtras();
        //从上一层获得传过来的参数
        mDeviceAddress = mBundle.getString(EXTRAS_DEVICE_ADDRESS);
        mDeviceName = mBundle.getString(EXTRAS_DEVICE_NAME);
        mRssi = mBundle.getString(EXTRAS_DEVICE_RSSI);
        initView(); //初始化控件
        //开启蓝牙服务
        Intent gattService = new Intent(Ble_Activity.this, BluetoothLeService.class);
        bindService(gattService, mServiceConnection, BIND_AUTO_CREATE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
        mBluetoothLeService.disconnect();//断开连接
        mBluetoothLeService.close(); //关闭蓝牙连接
        mBluetoothLeService = null;
    }


    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mServiceConnection); //解绑服务
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpateIntentFilter());
        if (mBluetoothLeService !=null){
            //根据蓝牙地址建立连接
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            LogUtils.i("Connect request result ="+result);
        }
    }

    private void initView(){
        mScrollView = findViewById(R.id.rev_sv);
        revTextView = findViewById(R.id.rev_tv);
        connectState = findViewById(R.id.connect_state);
        mSendButton = findViewById(R.id.send_btn);
        mSendEdit = findViewById(R.id.send_et);
        connectState.setText(status);
        mSendButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
            LogUtils.i("targetchara ="+targetChara+" , mBluetoothLeService="+mBluetoothLeService);
            new SendMessageThread().start();
    }

    /**
     * 发送数据中文需要转化格式，
     * 引文ＨＣ蓝牙模块单次最大写入长度２０byte, 当edittext长度大于限制长度，需要循环发送
     * 本文采用判断块的方式，首先判断长度，大于20byte做分流，小于就直接发送出去
     */
    public class SendMessageThread extends Thread {
        @Override
        public void run() {
            int i,j;
            try {
                byte[] sendMessages = mSendEdit.getText().toString().getBytes("GB2312");
                byte[] msgByte=new byte[20];
                if (sendMessages.length >= 20){
                    for (i = 0; i < sendMessages.length/20;i++){ //因为蓝牙支持最大单次20字节，所以切成块传输
                        for (j=0;j<20;j++){
                            msgByte[j]=sendMessages[i*20+j];
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
                }else{
                    targetChara.setValue(sendMessages);
                    mBluetoothLeService.writeCharacteristic(targetChara);
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }



    /**
     * 注册广播监听
     */
    private static IntentFilter makeGattUpateIntentFilter(){
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    /**
     * 接收到的数据在scrollview上显示
     * @param rev_string
     */
    private void displayData(String rev_string){

            revString += rev_string+"\n";
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    revTextView.setText(revString);
                    revTextView.scrollTo(0,revTextView.getMeasuredHeight());
                    LogUtils.i("displayData rev:" + revString);
                }
            });

    }

    /**
     * 负责接收服务类发送的数据
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)){ //Gatt连接成功
                mConnected = true;
                status = "connected";
                updateConnectionState(status);
                LogUtils.i("broadcastreceiver : deviced connected");
            }else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)){//Gatt连接失败
                mConnected = false;
                status = "disconnected";
                updateConnectionState(status);
                LogUtils.i("BroadcastReceiver: device disconnected");
            }else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)){ //发现gatt服务
                //获取设备的所有蓝牙服务
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
                LogUtils.i("-----BroadcastReceiver: device services discovered");
            }else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)){//有效数据
                //处理发送过来的数据
                displayData(intent.getExtras().getString(BluetoothLeService.EXTRA_DATA));
                LogUtils.i("BroadcastReceiver onData:"+intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    /**
     * 处理蓝牙服务,解析出有哪些服务，服务里有哪些Characteristic，哪些Characteristic可读可写可发通知等等
     * @param gattServers
     */
    private void displayGattServices(List<BluetoothGattService> gattServices){
        if (gattServices ==null){
            return;
        }
        String uuid = null;

        for (BluetoothGattService gattService :gattServices){ //遍历所有服务
            //获取服务列表
            uuid = gattService.getUuid().toString();
            LogUtils.i(" Ble Activity displayGattServices uuid = "+uuid);
            //从当前循环所指向的服务中读取特征值列表
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();

            for (final BluetoothGattCharacteristic gattCharacteristic :gattCharacteristics){//遍历每条服务里的所有Characteristic

                uuid = gattCharacteristic.getUuid().toString();
                LogUtils.i(" Ble Activity uuid = "+uuid);
                if (uuid.equals(HEART_RATE_MEASUREMENT)){
                    //测试读取当前Characteristic数据, 会触发mOnDataAvailable.onCharacteristicRead();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mBluetoothLeService.readCharacteristic(gattCharacteristic);
                        }
                    },200);
                    //接受通知的UUID，收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
                    mBluetoothLeService.setCharacteristicNotification(gattCharacteristic,true);
                    targetChara = gattCharacteristic;
                }
                List<BluetoothGattDescriptor> descriptors = gattCharacteristic.getDescriptors();
                for (BluetoothGattDescriptor descriptor : descriptors){
                    LogUtils.i("Ble Activity descriptor UUID: "+descriptor.getUuid());
                    //获取特征值的描述, 可读的UUID
                    mBluetoothLeService.getCharacteristicDescriptor(descriptor);
                }
            }
        }
    }

    /**
     * 更新连接状态
     * @param status
     */
    private void updateConnectionState(String status){
        Message msg = new Message();
        msg.what = 1;
        Bundle b = new Bundle();
        b.putString("connect_state",status);
        msg.setData(b);
        myHandler.sendMessage(msg);
        LogUtils.i("connect_state :"+status);
    }
}
