package com.android.blue.smarthomefunc.adapter;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.entity.BluetoothControlDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 1/4/18.
 */

public class GrideAdapter extends BaseAdapter {
    private Context mContext;
    private List<BluetoothControlDevice> mDevices = new ArrayList<>();

    public GrideAdapter(Context context, List<BluetoothControlDevice> datas){
        mContext = context;
        mDevices = datas;
    }

    @Override
    public int getCount() {
        return mDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return mDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null){
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.gridview_item, null);
            holder.mModeName = view.findViewById(R.id.grid_mode_name);
            holder.mDeviceName = view.findViewById(R.id.grid_device_name);
            holder.mStatus = view.findViewById(R.id.grid_status);
            holder.mDeviceSwitch = view.findViewById(R.id.grid_switch);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        BluetoothControlDevice mDevice = mDevices.get(position);
        holder.mModeName.setText(mDevice.getModeName());
        holder.mDeviceName.setText(mDevice.getDeviceName());
        holder.mStatus.setText(mDevice.getStatusRssi());
        holder.mDeviceSwitch.setChecked(mDevice.isDeviceSwitch());

        return  view;
    }


    class ViewHolder {
        TextView mModeName;
        TextView mDeviceName;
        TextView mStatus;
        SwitchCompat mDeviceSwitch;
    }

}
