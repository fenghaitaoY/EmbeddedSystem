package com.android.blue.smarthomefunc.service;

/**
 * Created by root on 1/22/18.
 */

public interface EventCallback<T> {
    void onEvent(T t);
}
