package com.android.blue.smarthomefunc.http;

/**
 * Created by root on 1/28/18.
 */

public abstract class HttpCallback<T> {
    public abstract void onSuccess(T t);
    public abstract void onFail(Exception e);
    public void onFinish(){

    }
}
