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
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.blue.smarthomefunc.BluetoothLeService;
import com.android.blue.smarthomefunc.LogUtils;
import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.activity.SearchAddDeviceActivity;
import com.android.blue.smarthomefunc.adapter.GrideAdapter;
import com.android.blue.smarthomefunc.entity.BluetoothControlDevice;

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

    @BindView(R.id.cmd_et)
    EditText mCmdEdit;
    @BindView(R.id.device_control_bt)
    Button mSendButton;
    @BindView(R.id.gridView_layout)
    GridView mGridView;
    @BindView(R.id.add_device_tv)
    Button mAddDevice;

    private View mRootView;
    private Context mContext;

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    private BluetoothAdapter mBluetoothAdapter;
    int REQUEST_ENABLE_BT = 1;

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
        getDatas();
        //mGridView.setAdapter(new GrideAdapter(mContext, mDevices));
        return mRootView;
    }

    public void getDatas(){
        for (int i =0;i<3;i++){
            BluetoothControlDevice device = new BluetoothControlDevice();
            device.setModeName("MODE"+i);
            device.setDeviceName("device"+i);
            device.setStatus("-"+i);
            device.setDeviceSwitch(true);
        }
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

    @OnClick({R.id.device_control_bt, R.id.add_device_tv})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.device_control_bt:
                LogUtils.i("send et text");
                break;
            case R.id.add_device_tv:
                Intent intent = new Intent(mContext, SearchAddDeviceActivity.class);
                startActivity(intent);
                break;

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





}
