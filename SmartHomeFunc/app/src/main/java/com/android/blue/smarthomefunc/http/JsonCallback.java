package com.android.blue.smarthomefunc.http;

import com.android.blue.smarthomefunc.entity.LogUtils;
import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Response;


/**
 * Created by root on 1/28/18.
 */

public abstract class JsonCallback<T> extends Callback<T>{
    private  Class<T> mClass;
    private Gson mGson;

    public JsonCallback(Class<T> cls){
        this.mClass = cls;
        mGson = new Gson();
    }

    @Override
    public T parseNetworkResponse(Response response, int id) throws Exception {
        try {
            String jsonString = response.body().string();
            //LogUtils.i("parseNetworkResponse :" + jsonString);
            return mGson.fromJson(jsonString, mClass);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
