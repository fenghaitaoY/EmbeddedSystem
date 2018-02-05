package com.android.blue.smarthomefunc.utils;

import android.view.View;

import com.android.blue.smarthomefunc.enums.LoadStateEnum;

/**
 * Created by root on 1/28/18.
 */

public class ViewUtils {
    public static void changViewState(View loadSuccess, View loading, View loadFail, LoadStateEnum state){
        switch (state){
            case LOADING:
                loadSuccess.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                loadFail.setVisibility(View.GONE);
                break;
            case LOAD_SUCCESS:
                loadSuccess.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
                loadFail.setVisibility(View.GONE);
                break;
            case LOAD_FAIL:
                loadSuccess.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);
                loadFail.setVisibility(View.VISIBLE);
                break;
        }
    }
}
