package com.android.blue.smarthomefunc.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.entity.BleDeviceEntity;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.view.AlwaysMarqueeTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *  设备网格列表显示
 * Created by root on 1/4/18.
 */

public class GrideAdapter extends BaseAdapter {
    private Context mContext;
    private List<BleDeviceEntity> mDevices = new ArrayList<>();

    private OnItemClickListener listener;
    private OnCheckChangeListener changeListener;

    public GrideAdapter(Context context, List<BleDeviceEntity> datas){
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
    public View getView(final int position, View view, ViewGroup viewGroup) {
        LogUtils.i("  getview position = "+position);
        final ViewHolder holder;
        if (view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.gridview_item, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        view.clearFocus();
        holder.view.clearFocus();

        BleDeviceEntity mDevice = mDevices.get(position);
        holder.mModeName.setText(mDevice.getModeName());
        if (mDevice.getDeviceName()==null){
            holder.mDeviceName.setText("null");
        }else{
            holder.mDeviceName.setText(mDevice.getDeviceName());
        }

        holder.mDeviceAddress.setText(mDevice.getDeviceAddress());
        holder.mDeviceRssi.setText(mDevice.getStatusRssi());
        //holder.mDeviceSwitch.setChecked(mDevice.getDeviceSwitch());

        return  view;
    }


    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void setOnCheckChangeListener(OnCheckChangeListener listener){
        changeListener = listener;
    }

    class ViewHolder {
        @BindView(R.id.grid_mode_name)
        AlwaysMarqueeTextView mModeName;
        @BindView(R.id.grid_device_name)
        TextView mDeviceName;
        @BindView(R.id.grid_device_address)
        TextView mDeviceAddress;
        @BindView(R.id.grid_device_rssi)
        TextView mDeviceRssi;
        /*@BindView(R.id.grid_switch)
        SwitchCompat mDeviceSwitch;*/
        @BindView(R.id.device_cardView)
        LinearLayout view;

        public ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }

}
