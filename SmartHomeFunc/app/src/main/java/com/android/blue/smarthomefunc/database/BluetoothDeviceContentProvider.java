package com.android.blue.smarthomefunc.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.blue.smarthomefunc.LogUtils;

/**
 * Created by root on 1/4/18.
 */

public class BluetoothDeviceContentProvider extends ContentProvider {
    private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int DEVICES = 1;
    private static final int DEVICE  = 2;


    private DBHelper mDBHelper = null;

    static {
        //content://xxxxxxx/device
        matcher.addURI("com.android.blue.smarthome", "device", DEVICES);
        matcher.addURI("com.android.blue.smarthome", "device/#",DEVICE);
    }

    @Override
    public boolean onCreate() {
        LogUtils.i("device database create");
        mDBHelper = new DBHelper(getContext(), DBinfo.DB.DB_NAME, null, DBinfo.DB.DB_VERSION);
        return (mDBHelper == null)? false : true;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        Uri insertUri = null;
        try {
            if (matcher.match(uri) == DEVICES) {
                LogUtils.i("insert data");
                SQLiteDatabase db = mDBHelper.getWritableDatabase();
                long rowid = db.insert(DBinfo.Table.TABLE_NAME, null, contentValues);
                insertUri = ContentUris.withAppendedId(uri, rowid);
            } else {
                throw new IllegalArgumentException("PATH ERROR, DO NOT INSET DATA");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return insertUri;
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] strings) {
        int result = 0;
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        try{
            if (matcher.match(uri)==DEVICES) {
                result = db.delete(DBinfo.Table.TABLE_NAME, selection, strings);
            }else if (matcher.match(uri) == DEVICE){
                long id = ContentUris.parseId(uri);
                String where = "_id="+id;
                if (selection != null && !"".equals(selection)){
                    where = selection +"and" +where;
                }
                result = db.delete(DBinfo.Table.TABLE_NAME,where, strings);
            }else{
                throw new IllegalArgumentException("path error, do not delete data");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] strings) {
        int result =0;
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        try{
            if (matcher.match(uri)==DEVICES) {
                result = db.update(DBinfo.Table.TABLE_NAME, contentValues, selection, strings);
            }else if (matcher.match(uri) == DEVICE){
                long id = ContentUris.parseId(uri);
                String where = "_id"+id;
                if (selection != null && !"".equals(selection)){
                    where = selection+"and"+where;
                }
                result = db.update(DBinfo.Table.TABLE_NAME, contentValues, where,strings);
            }else{
                throw new IllegalArgumentException("path error, do not update data");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor = null;
        try {
            if (matcher.match(uri) == DEVICES){
                LogUtils.i("query data");
                SQLiteDatabase db = mDBHelper.getWritableDatabase();
                cursor = db.query(DBinfo.Table.TABLE_NAME, projection,selection,selectionArgs,null, null,null);
            }else if (matcher.match(uri) == DEVICE){
                SQLiteDatabase db = mDBHelper.getWritableDatabase();
                long id = ContentUris.parseId(uri);
                String where = "_id ="+id;
                if (!"".equals(selection) && selection != null){
                    where = selection + "and" +where;
                }
                cursor = db.query(DBinfo.Table.TABLE_NAME, projection, where, selectionArgs,null, null,sortOrder);
            }else{
                throw new IllegalArgumentException("path error, do not query data");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return cursor;
    }

    public String getType(Uri uri){
        if (matcher.match(uri) == DEVICES){
            return "com.android.blue.smarthome";
        }
        return null;
    }
}
