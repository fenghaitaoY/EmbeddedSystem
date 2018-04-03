package com.android.blue.smarthomefunc.http;

import android.os.Build;

import com.android.blue.smarthomefunc.entity.LogUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 拦截器
 * Created by root on 1/28/18.
 */

public class HttpInterceptor implements Interceptor {
    private static final String UA = "User-Agent";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request()
                .newBuilder()
                .addHeader(UA, makeUA())
                .build();
        LogUtils.i("发送请求　url ="+request.url()+" , connect = "+chain.connection()+" , header ="+request.headers());

        Response response = chain.proceed(request);

        LogUtils.i("响应请求　url = "+response.request().url()+" , header ="+response.headers());
        return response;
    }

    private String makeUA(){
        return Build.BRAND+"/"+Build.MODEL+"/"+Build.VERSION.RELEASE;
    }
}
