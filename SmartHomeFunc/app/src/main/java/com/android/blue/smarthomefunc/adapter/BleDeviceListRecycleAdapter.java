package com.android.blue.smarthomefunc.adapter;

import android.media.Image;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.entity.BleDeviceEntity;
import com.android.blue.smarthomefunc.view.AlwaysMarqueeTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fht on 4/13/18.
 */

public class BleDeviceListRecycleAdapter extends RecyclerView.Adapter<BleDeviceListRecycleAdapter.BleDeviceHolder> {

    private List<BleDeviceEntity> mDevices = new ArrayList<>();
    private OnItemClickListener onItemClickListener;


    public BleDeviceListRecycleAdapter(List<BleDeviceEntity> data){
        mDevices = data;
    }

    @Override
    public BleDeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BleDeviceHolder holder = new BleDeviceHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.gridview_item, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final BleDeviceHolder holder, final int position) {
        BleDeviceEntity mDevice = mDevices.get(position);
        holder.mModeName.setText(mDevice.getModeName());
        if (mDevice.getDeviceName()==null){
            holder.mDeviceName.setText("null");
        }else{
            holder.mDeviceName.setText(mDevice.getDeviceName());
        }

        holder.mDeviceAddress.setText(mDevice.getDeviceAddress());
        holder.mDeviceRssi.setText(mDevice.getStatusRssi());

        if (mDevice.getDeviceSwitch()){
            holder.statusImage.setImageResource(R.drawable.bluetooth_connect);
        }else{
            holder.statusImage.setImageResource(R.drawable.bluetooth_disconnect);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(holder.view, position);
            }
        });

        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (onItemClickListener != null){
                    onItemClickListener.onItemLongClick(holder.view, position);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void setOnItemClickListener(OnItemClickListener listener){
        this.onItemClickListener = listener;
    }

    class BleDeviceHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.grid_mode_name)
        AlwaysMarqueeTextView mModeName;
        @BindView(R.id.grid_device_name)
        TextView mDeviceName;
        @BindView(R.id.grid_device_address)
        TextView mDeviceAddress;
        @BindView(R.id.grid_device_rssi)
        TextView mDeviceRssi;
        @BindView(R.id.device_cardView)
        CardView view;
        @BindView(R.id.grid_device_bluetooth_status)
        ImageView statusImage;

        public BleDeviceHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
