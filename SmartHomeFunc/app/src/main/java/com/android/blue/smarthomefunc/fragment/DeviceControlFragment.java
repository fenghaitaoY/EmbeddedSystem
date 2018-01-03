package com.android.blue.smarthomefunc.fragment;

import android.Manifest;
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
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.blue.smarthomefunc.BluetoothLeService;
import com.android.blue.smarthomefunc.LogUtils;
import com.android.blue.smarthomefunc.R;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.BIND_AUTO_CREATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DeviceControlFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceControlFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.tv_control_device_name)
    TextView mControlDeviceName;
    @BindView(R.id.tv_status)
    TextView mDeviceStatus;
    @BindView(R.id.tv_name)
    TextView mDevicetName;
    @BindView(R.id.cmd_et)
    EditText mCmdEdit;
    @BindView(R.id.device_control_bt)
    Button mSendButton;

    private View mRootView;
    private Context mContext;

    //蓝牙适配器
    BluetoothAdapter mBluetoothAdapter;
    //蓝牙信号强度
    private ArrayList<Integer> rssis;
    private ArrayList<BluetoothDevice> mListDevice;

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

    //蓝牙控制地址
    private String blueAddress = "34:15:13:1C:A1:9F";

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
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DeviceControlFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeviceControlFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeviceControlFragment newInstance(String param1, String param2) {
        DeviceControlFragment fragment = new DeviceControlFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        LogUtils.i("Device control onCreate ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }

        scan_flag = true;
        mHandler = new Handler();
        //注册蓝牙状态广播
        IntentFilter mBlueStateFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mBlueChangeBroadcast = new BluetoothStateChangeBroadcastReceiver();
        getActivity().registerReceiver(mBlueChangeBroadcast, mBlueStateFilter);
        getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        /* 启动蓝牙service */
        Intent gattServiceIntent = new Intent(getActivity(), BluetoothLeService.class);
        getActivity().bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private void initBluetooth() {
        //检测硬件是否支持
        if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(getActivity().getApplication(), "不支持BLE", Toast.LENGTH_SHORT).show();
        }
        //获取手机本地蓝牙适配
        final BluetoothManager mBlueManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBlueManager.getAdapter();
        //打开蓝牙
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LogUtils.i("Device control onCreateView");
        mRootView = inflater.inflate(R.layout.fragment_device_control, container, false);
        mContext = getActivity();
        //fragment ButterKnife 使用这种方法，直接bind(view)不生效
        ButterKnife.bind(this,mRootView);
        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.i("Device control onResume");
        initBluetooth(); //初始化蓝牙
        rssis = new ArrayList<>();
        mListDevice = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (scan_flag) {
                    scanLeDevice(true);
                } else {
                    scanLeDevice(false);
                }
            }
        }).start();

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogUtils.i("Device control onAttach ");
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtils.i("Device control onDetach ");
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        LogUtils.i("Device control onDestroyView ");
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                LogUtils.i("蓝牙已经开启");
                String localBlueName = mBluetoothAdapter.getName();
                String localBlueAddress = mBluetoothAdapter.getAddress();
                Toast.makeText(getActivity().getApplicationContext(), "Local name:" + localBlueName + " ,Local Address:" + localBlueAddress, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.device_control_bt)
    public void onViewClicked() {
        LogUtils.i("send et text");
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
            int i, j;
            try {
                byte[] sendMessages = mCmdEdit.getText().toString().getBytes("GB2312");
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


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    scan_flag = true;
                    LogUtils.i("定时一段时间后　stop scan le device");
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);

                }
            }, SCAN_PERIOD);
            LogUtils.i("scan le begin..");
            mScanning = true;
            scan_flag = false;
            mBluetoothAdapter.startLeScan(mLeScanCallback);

        } else {
            LogUtils.i("stoping scan ");
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            scan_flag = true;
        }

    }

    /**
     * 蓝牙扫描回调函数，　实现扫描蓝牙设备
     */
    public BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice bluetoothDevice, final int rssi, byte[] bytes) {
            if (!mListDevice.contains(bluetoothDevice)) {
                mListDevice.add(bluetoothDevice);
                rssis.add(rssi);
            }
            if (bluetoothDevice.getAddress().equals(blueAddress)) {
                scanLeDevice(false);
                LogUtils.i("NAME :" + bluetoothDevice.getName());
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mBluetoothLeService != null && mBluetoothLeService.initialize()) {
                            LogUtils.i("--connect--");
                            mBluetoothLeService.connect(bluetoothDevice.getAddress());
                        }
                    }
                }, 200);

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
}
