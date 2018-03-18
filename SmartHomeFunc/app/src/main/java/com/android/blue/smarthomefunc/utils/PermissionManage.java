package com.android.blue.smarthomefunc.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;



/**
 * 运行时权限管理类
 * Created by root on 1/31/18.
 */

public class PermissionManage {

    private static AtomicInteger sRequestCode = new AtomicInteger(0);
    private static SparseArray<Result> sResultArray = new SparseArray<>();
    private static Set<String> sManifestPermissionSet;

    public interface Result{
        void onGranted();
        void onDenied();
    }

    private Object mObject;
    private String[] mPermissions;
    private Result mResult;

    private PermissionManage(Object object){
        mObject = object;
    }

    public static PermissionManage with(@NonNull Activity activity){
        return new PermissionManage(activity);
    }

    public static PermissionManage with(@NonNull Fragment fragment){
        return new PermissionManage(fragment);
    }

    public PermissionManage permissions(@NonNull String... permissions){
        mPermissions = permissions;
        return this;
    }
    
    public PermissionManage result(@NonNull Result result){
        mResult = result;
        return this;
    }
    
    public void request(){
        Activity activity = getActivity(mObject);
        if (activity == null){
            throw new IllegalArgumentException(mObject.getClass().getName()+"is not supported");
        }

        initManifestPermission(activity);
        for (String permission : mPermissions){
            if (!sManifestPermissionSet.contains(permission)){
                if (mResult != null){
                    mResult.onDenied();
                }
                return;
            }
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            if (mResult != null){
                mResult.onGranted();
            }
            return;
        }

        List<String> deniedPermissionList = getDeniedPermissions(activity, mPermissions);
        if (deniedPermissionList.isEmpty()){
            if (mResult!= null){
                mResult.onGranted();
            }
            return;
        }

        int requestCode = genRequestCode();
        String [] deniedPermissions = deniedPermissionList.toArray(new String[deniedPermissionList.size()]);
        requestPermission(mObject, deniedPermissions, requestCode);
        sResultArray.put(requestCode, mResult);
    }

    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        Result result = sResultArray.get(requestCode);
        if (result == null){
            return;
        }

        sResultArray.remove(requestCode);
        for (int grantResult: grantResults){
            if (grantResult != PackageManager.PERMISSION_GRANTED){
                result.onDenied();
                return;
            }
        }

        result.onGranted();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static void requestPermission(Object object, String[] permissions, int requestCode) {
        if (object instanceof Activity)
            ((Activity) object).requestPermissions(permissions, requestCode);
        else if (object instanceof Fragment){
            ((Fragment) object).requestPermissions(permissions, requestCode);
        }
    }

    private int genRequestCode() {
        return sRequestCode.incrementAndGet();
    }

    private static List<String> getDeniedPermissions(Context context, String[] permissions) {
        List<String> deniedPermissionList = new ArrayList<>();
        for (String permission : permissions){
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
                deniedPermissionList.add(permission);
            }
        }
        return deniedPermissionList;
    }

    private void initManifestPermission(Context context) {
        if (sManifestPermissionSet == null){
            sManifestPermissionSet = new HashSet<>();
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
                String[] permissions = packageInfo.requestedPermissions;
                Collections.addAll(sManifestPermissionSet, permissions);
            }catch (PackageManager.NameNotFoundException e){
                e.printStackTrace();
            }
        }
    }

    private Activity getActivity(Object object) {
        if (object != null){
            if (object instanceof Activity){
                return (Activity) object;
            }else if (object instanceof Fragment){
                return ((Fragment) object).getActivity();
            }
        }
        return null;
    }
}