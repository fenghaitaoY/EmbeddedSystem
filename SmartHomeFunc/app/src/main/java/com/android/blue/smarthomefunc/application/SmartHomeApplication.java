package com.android.blue.smarthomefunc.application;

import android.app.Activity;
import android.app.Application;

import com.android.blue.smarthomefunc.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 退出所有界面
 * Created by root on 12/30/17.
 */

public class SmartHomeApplication extends Application {
    private List<Activity> mActivityList; //存储所有启动的activity集合

    @Override
    public void onCreate() {
        super.onCreate();
        mActivityList = new ArrayList<>();
    }

    /**
     * 将启动的activity加入到list列表
     * @param activity
     */
    public void addActivity(Activity activity){
        if (!mActivityList.contains(activity)){
            LogUtils.i("add activity"+activity.getComponentName().getClassName());
            mActivityList.add(activity);
        }
    }

    /**
     * 销毁activity，从集合中移除
     * @param activity
     */
    public void removeActivity(Activity activity){
        if (mActivityList.contains(activity)) {
            mActivityList.remove(activity);
            LogUtils.i("remove activity");
            activity.finish();
        }
    }

    public void removeAllActivity(){
        for (Activity activity : mActivityList){
            LogUtils.i("remove all activity");
            activity.finish();
        }
        System.exit(0);
    }
}
