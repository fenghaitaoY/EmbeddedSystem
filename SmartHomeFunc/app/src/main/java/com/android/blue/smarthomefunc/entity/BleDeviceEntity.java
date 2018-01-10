package com.android.blue.smarthomefunc.entity;

/**
 * Created by root on 1/4/18.
 */

public class BleDeviceEntity {
   private String mDeviceName;
   private String mStatusRssi;
   private String mModeName;
   private String mDeviceAddress;
   private boolean mDeviceSwitch;



    public String getDeviceName() {
        return mDeviceName;
    }

    public void setDeviceName(String mDeviceName) {
        this.mDeviceName = mDeviceName;
    }

    public String getStatusRssi() {
        return mStatusRssi;
    }

    public void setStatusRssi(String mStatus) {
        this.mStatusRssi = mStatus;
    }

    public String getModeName() {
        return mModeName;
    }

    public void setModeName(String mModeName) {
        this.mModeName = mModeName;
    }

    public String getDeviceAddress(){
        return mDeviceAddress;
    }

    public void setDeviceAddress(String mDeviceAddress){
        this.mDeviceAddress = mDeviceAddress;
    }

    public boolean isDeviceSwitch() {
        return mDeviceSwitch;
    }

    public void setDeviceSwitch(boolean mDeviceSwitch) {
        this.mDeviceSwitch = mDeviceSwitch;
    }

    @Override
    public String toString() {
        return "MODENAME:"+mModeName+", DeviceName:"+mDeviceName+", Status:"+mStatusRssi+", switch:"+mDeviceSwitch;
    }
}
