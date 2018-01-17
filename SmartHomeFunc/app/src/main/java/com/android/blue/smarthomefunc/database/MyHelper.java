package com.android.blue.smarthomefunc.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.blue.smarthomefunc.LogUtils;

/**
 * Created by root on 1/4/18.
 */

public class MyHelper extends SQLiteOpenHelper {

    public MyHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        LogUtils.i("create db");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBinfo.Table.CREAT_DEVICE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DBinfo.Table.DROP_TABLE);
        onCreate(sqLiteDatabase);
    }
}
