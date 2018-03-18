package com.android.blue.smarthomefunc.adapter;

import android.view.View;

/**
 * RecycleAdapter 使用
 * Created by fht on 3/15/18.
 */

public interface OnItemClickListener {
    void onItemClick(View view, int position);
    void onItemLongClick(View view, int position);
}
