package com.android.blue.smarthomefunc.entity;

import android.util.Log;

/**
 * Created by root on 12/18/17.
 */

public class LogUtils {
    private LogUtils()
    {
        throw new UnsupportedOperationException("cannot be instantiated");
    }
    // 是否需要打印bug，可以在application的onCreate函数里面初始化
    public static boolean isDebug = true;
    private static final String TAG = "smart_home";


    /**
     * 获取有类名与方法名的logString
     * @param rawMessage
     * @return
     */
    private static String createMessage(String rawMessage) {
        /**
         * Throwable().getStackTrace()获取的是程序运行的堆栈信息，也就是程序运行到此处的流程，以及所有方法的信息
         * 这里我们为什么取2呢？0是代表createMessage方法信息，1是代表上一层方法的信息，这里我们
         * 取它是上两层方法的信息
         */
        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[2];
        String fullClassName = stackTraceElement.getClassName();
        String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        return className + "." + stackTraceElement.getMethodName() + "(): " + rawMessage;
    }

    // 下面四个是默认tag的函数
    public static void i(String msg)
    {
        if (isDebug)
            Log.i(TAG, createMessage(msg));
    }

    public static void d(String msg)
    {
        if (isDebug)
            Log.d(TAG, createMessage(msg));
    }

    public static void e(String msg)
    {
        if (isDebug)
            Log.e(TAG, createMessage(msg));
    }

    public static void v(String msg)
    {
        if (isDebug)
            Log.v(TAG, createMessage(msg));
    }

    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg)
    {
        if (isDebug)
            Log.i(tag, createMessage(msg));
    }

    public static void d(String tag, String msg)
    {
        if (isDebug)
            Log.i(tag, createMessage(msg));
    }

    public static void e(String tag, String msg)
    {
        if (isDebug)
            Log.i(tag, msg);
    }

    public static void v(String tag, String msg)
    {
        if (isDebug)
            Log.i(tag, msg);
    }
}

