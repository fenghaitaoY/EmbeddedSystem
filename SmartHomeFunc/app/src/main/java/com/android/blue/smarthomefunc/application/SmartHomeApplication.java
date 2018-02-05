package com.android.blue.smarthomefunc.application;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.http.HttpInterceptor;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * 退出所有界面
 * Created by root on 12/30/17.
 */

public class SmartHomeApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private List<Activity> mActivityList; //存储所有启动的activity集合

    @Override
    public void onCreate() {
        super.onCreate();
        mActivityList = new ArrayList<>();

        registerActivityLifecycleCallbacks(this);

        AppCache.get().init(this);
        initOkHttpUtils();
    }

    private void initOkHttpUtils(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new HttpInterceptor())
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    @Override
    public int checkPermission(String permission, int pid, int uid) {
        return super.checkPermission(permission, pid, uid);
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

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        LogUtils.i("onCreated :"+activity.getClass().getSimpleName());
        addActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        LogUtils.i("onDestroy :"+activity.getClass().getSimpleName());
        removeActivity(activity);
    }
}
