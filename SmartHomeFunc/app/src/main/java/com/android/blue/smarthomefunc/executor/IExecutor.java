package com.android.blue.smarthomefunc.executor;

/**
 * Created by root on 3/2/18.
 */

public interface IExecutor<T> {
    void execute();
    void onPrepare();
    void onExecuteSuccess(T t);
    void onExecuteFail(Exception e);
}
