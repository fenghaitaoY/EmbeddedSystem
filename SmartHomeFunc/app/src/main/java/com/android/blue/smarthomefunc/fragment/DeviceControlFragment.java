package com.android.blue.smarthomefunc.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.blue.smarthomefunc.adapter.BleDeviceListRecycleAdapter;
import com.android.blue.smarthomefunc.adapter.OnCheckChangeListener;
import com.android.blue.smarthomefunc.adapter.OnItemClickListener;
import com.android.blue.smarthomefunc.application.AppCache;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.activity.SearchAddDeviceActivity;
import com.android.blue.smarthomefunc.adapter.GrideAdapter;
import com.android.blue.smarthomefunc.database.DBinfo;
import com.android.blue.smarthomefunc.entity.BleDeviceEntity;
import com.android.blue.smarthomefunc.entity.HCBluetoothControl;
import com.android.blue.smarthomefunc.service.BluetoothLeService;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DeviceControlFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceControlFragment extends Fragment implements OnItemClickListener, OnCheckChangeListener{

    private static final String CONTENT_PROVIDER_URI = "content://com.android.blue.smarthomeprovider/device";
    @BindView(R.id.device_recycle)
    RecyclerView mRecycleView;
    @BindView(R.id.add_device_tv)
    Button mAddDevice;

    private View mRootView;
    private Context mContext;

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    private BluetoothAdapter mBluetoothAdapter;
    private boolean deleteLongClick = false;
    int REQUEST_ENABLE_BT = 1;
    private BleDeviceListRecycleAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private GridLayoutManager gridLayoutManager;

    private Handler mHandler = new Handler();
    private BleConnectBroadcastReceiver mBroadcastReceiver;
    private Dialog mBleConnectDialog;
    private Dialog mSendMsgDialog;

    private String modeName;
    private TextView loadingTv;

    private EditText inputCMDmsg;
    private TextView titleTV;

    private int clickItemPosition;

    //蓝牙扫描，连接操作
    private HCBluetoothControl mHCBleControl;

    private static DeviceControlFragment fragment;
    private DeviceControlFragment() {
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
    public static DeviceControlFragment newInstance() {
        if (fragment == null) {
            fragment = new DeviceControlFragment();
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i("Device control onCreate ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }



    }

    private void initBluetooth() {
        //检测硬件是否支持
        if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(getActivity().getApplication(), "不支持BLE", Toast.LENGTH_SHORT).show();
        }
        //获取手机本地蓝牙适配
        final BluetoothManager mBlueManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBlueManager.getAdapter();
        //判断蓝牙功能是否打开，　未打开则打开蓝牙
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

        mHCBleControl = HCBluetoothControl.getInstance(getActivity());

        mAdapter = new BleDeviceListRecycleAdapter(AppCache.get().getBleDeviceList());
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        gridLayoutManager = new GridLayoutManager(getActivity(),2, GridLayoutManager.VERTICAL, false);

        mRecycleView.setLayoutManager(gridLayoutManager);
        mRecycleView.setItemAnimator(new DefaultItemAnimator());
        mRecycleView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnCheckChangeListener(this);

        mBroadcastReceiver = new BleConnectBroadcastReceiver();
        mBleConnectDialog = createBleConnectLoadingDialog();
        mSendMsgDialog = createBleSendMessageDialog();
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        //监听蓝牙连接广播
        registerBroadcastBleConnect();
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.i("Device control onResume");
        initBluetooth(); //初始化蓝牙

        checkAlreadyRegistDevice();
    }

    /**
     * 创建蓝牙连接弹窗
     * @return
     */
    public Dialog createBleConnectLoadingDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.ble_loading_dialog, null);
        loadingTv = view.findViewById(R.id.loading_tv);
        loadingTv.setText(getString(R.string.ble_connect_loading, modeName));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setCancelable(false);
        return builder.create();
    }

    /**
     * 已经连接设备，　发送指令
     * @return
     */
    private Dialog createBleSendMessageDialog(){
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.ble_connect_send_message, null);
        inputCMDmsg = view.findViewById(R.id.et_send_msg);
        titleTV = view.findViewById(R.id.title_send_msg);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!TextUtils.isEmpty(inputCMDmsg.getText().toString())){
                    mHCBleControl.startSendMsg(inputCMDmsg.getText().toString().trim());
                    inputCMDmsg.setText(""); //清空输入信息
                }

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                inputCMDmsg.setText("");
            }
        });
        return  builder.create();
    }

    /**
     * 显示正在连接蓝牙
     */
    private void showLoadingView(){
        loadingTv.setText(getString(R.string.ble_connect_loading, modeName));
        mBleConnectDialog.show();

    }

    /**
     * 隐藏蓝牙连接view
     */
    private void hideLoadingView(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBleConnectDialog.hide();
            }
        }, 1000);

    }

    /**
     * 注册监听广播
     */
    private void registerBroadcastBleConnect(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        filter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        getActivity().registerReceiver(mBroadcastReceiver, filter);
    }



    /**
     * 蓝牙连接广播
     */
    class BleConnectBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtils.i("action = "+action);
            if (action.equals(BluetoothLeService.ACTION_GATT_CONNECTED)){
                AppCache.get().getBleDeviceList().get(clickItemPosition).setDeviceSwitch(true);
                updateDevice(AppCache.get().getBleDeviceList().get(clickItemPosition));
                mAdapter.notifyDataSetChanged();
                hideLoadingView();
            }else if (action.equals(BluetoothLeService.ACTION_GATT_DISCONNECTED)){
                AppCache.get().getBleDeviceList().get(clickItemPosition).setDeviceSwitch(false);
                updateDevice(AppCache.get().getBleDeviceList().get(clickItemPosition));
                mAdapter.notifyDataSetChanged();
                hideLoadingView();
                Toast.makeText(context, "连接失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 扫描已经保存的蓝牙设备
     */
    private void checkAlreadyRegistDevice(){
        AppCache.get().getBleDeviceList().clear();
        Uri uri = Uri.parse(CONTENT_PROVIDER_URI);

        Cursor cursor = mContext.getContentResolver().query(uri,null, null, null,null);
        LogUtils.i(" database count"+cursor.getCount());
        if (cursor != null && cursor.getCount() > 0) {
            LogUtils.i("ColumnCount:"+cursor.getColumnCount());
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                for (int i=1;i<cursor.getColumnCount();i++){
                    LogUtils.i(cursor.getColumnName(i)+": "+cursor.getString(i));
                }
                BleDeviceEntity entity = new BleDeviceEntity();
                entity.setModeName(cursor.getString(cursor.getColumnIndex(DBinfo.Table.TABLE_COLUMN_MODE_NAME)));
                entity.setDeviceName(cursor.getString(cursor.getColumnIndex(DBinfo.Table.TABLE_COLUMN_DEVICE_NAME)));
                entity.setDeviceAddress(cursor.getString(cursor.getColumnIndex(DBinfo.Table.TABLE_COLUMN_DEVICE_ADDRESS)));
                entity.setStatusRssi(cursor.getInt(cursor.getColumnIndex(DBinfo.Table.TABLE_COLUMN_RSSI))+"");
                entity.setDeviceSwitch(cursor.getInt(cursor.getColumnIndex(DBinfo.Table.TABLE_COLUMN_SWITCH_STATUS)) == 1);

                AppCache.get().getBleDeviceList().add(entity);

            }

        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 删除蓝牙设备
     * @param BleDeviceEntity entity
     */
    private void deleteDevice(BleDeviceEntity entity){
        Uri uri = Uri.parse(CONTENT_PROVIDER_URI);
        mContext.getContentResolver().delete(uri, DBinfo.Table.TABLE_COLUMN_DEVICE_ADDRESS + "=?", new String[]{entity.getDeviceAddress()});
    }

    /**
     * 更新开关状态
     * @param entity
     * @return
     */
    private int updateDevice(BleDeviceEntity entity){
        int result = 0;
        Uri uri = Uri.parse(CONTENT_PROVIDER_URI);
        ContentValues values = new ContentValues();
        values.put(DBinfo.Table.TABLE_COLUMN_SWITCH_STATUS, entity.getDeviceSwitch() ? 1 : 0);
        result = mContext.getContentResolver().update(uri, values, DBinfo.Table.TABLE_COLUMN_DEVICE_ADDRESS+ "=?", new String[]{entity.getDeviceAddress()});
        return result;
    }

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
        //蓝牙关闭
        mHCBleControl.disconnectBle();
        mHCBleControl.closeBleGAT();
        getActivity().unregisterReceiver(mBroadcastReceiver);
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

    @OnClick({ R.id.add_device_tv})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.add_device_tv:
                Intent intent = new Intent(mContext, SearchAddDeviceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); //解决多次点击出现多次界面
                startActivity(intent);
                break;

        }

    }

    @Override
    public void onCheckChange(int position, boolean checked) {
        LogUtils.i(" position = " + position + " , checked = " + checked);
        //已经连接，按钮关闭
        //未连接，　按钮打开
        if (mHCBleControl.isBluetoothConnect(AppCache.get().getBleDeviceList().get(position).getDeviceAddress()) && !checked) {
            LogUtils.i("已经连接, 关闭连接");
            if (!checked){
            mHCBleControl.disconnectBle();
            mHCBleControl.closeBleGAT();
            AppCache.get().getBleDeviceList().get(position).setDeviceSwitch(false);
            updateDevice(AppCache.get().getBleDeviceList().get(position));
            mAdapter.notifyDataSetChanged();
            }
        }else if (!mHCBleControl.isBluetoothConnect(AppCache.get().getBleDeviceList().get(position).getDeviceAddress()) && checked){
            connectDevice(position);
        }


    }

    @Override
    public void onItemClick(View view, final int position) {
        LogUtils.i(" position = "+AppCache.get().getBleDeviceList().get(position).getDeviceName()
                +" switch = "+AppCache.get().getBleDeviceList().get(position).getDeviceSwitch());

        clickItemPosition = position;
        modeName = AppCache.get().getBleDeviceList().get(position).getModeName();

        connectDevice(position);

    }

    /**
     * 蓝牙连接
     * @param position
     */
    private void connectDevice(final int position){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mHCBleControl.isBluetoothConnect(AppCache.get().getBleDeviceList().get(position).getDeviceAddress())){
                    LogUtils.i("已经连接");
                    Toast.makeText(getActivity().getApplicationContext(), "已经连接", Toast.LENGTH_SHORT).show();
                    titleTV.setText(modeName);
                    mSendMsgDialog.show();
                    return;
                }
                //如果已经有连接，　先关闭之前的，　再连接现在的
                if (mHCBleControl.getConnectDevice().size() > 0){
                    mHCBleControl.disconnectBle();
                    mHCBleControl.closeBleGAT();
                }

                //蓝牙连接
                mHCBleControl.connectDevice(AppCache.get().getBleDeviceList().get(position).getDeviceAddress());

                showLoadingView();
            }
        });
    }

    @Override
    public void onItemLongClick(View view, final int position) {
        LogUtils.i(" ");
        deleteLongClick = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(AppCache.get().getBleDeviceList().get(position).getModeName())
                .setMessage("删除该设备")
                .setIcon(R.drawable.ble_search_icon)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //数据库中删除
                        deleteDevice(AppCache.get().getBleDeviceList().get(position));
                        //内存中删除
                        AppCache.get().getBleDeviceList().remove(AppCache.get().getBleDeviceList().get(position));
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                            }
                        }, 1000);

                    }
                });
        builder.create().show();
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





}
