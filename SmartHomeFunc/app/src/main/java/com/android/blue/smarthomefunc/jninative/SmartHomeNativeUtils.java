package com.android.blue.smarthomefunc.jninative;

/**
 * Created by fht on 4/28/18.
 */

public class SmartHomeNativeUtils {

    static {
        System.loadLibrary("smarthome");
    }



    public static native int initPath(String path); //设置存储路径

    public static native int save(String name, String password); //保存密码, 保存成功返回0, 失败返回-1;

    public static native int verify(String name, String password); //验证密码, 正确返回0, 错误返回-1;

    public static native int destroy(); //退出应用时调用
}
