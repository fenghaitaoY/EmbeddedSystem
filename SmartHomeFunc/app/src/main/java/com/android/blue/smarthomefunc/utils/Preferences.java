package com.android.blue.smarthomefunc.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.android.blue.smarthomefunc.R;

/**
 * SharedPreference 工具类
 * Created by root on 1/24/18.
 */

public class Preferences {
    private static final String MUSIC_ID = "music_id";
    private static final String PLAY_MODE = "play_mode";
    private static final String SPLASH_URL = "splash_url";
    private static final String NIGHT_MODE = "night_mode";

    private static Context mContext;

    public static void init(Context context){
        mContext = context;
    }

    public static long getCurrentSongId(){
        return getLong(MUSIC_ID, -1);
    }

    public static void saveCurrentSongId(long id){
        saveLong(MUSIC_ID, id);
    }

    public static int getPlayMode(){
        return getInt(PLAY_MODE, 0);
    }

    public static void savePlayMode(int mode){
        saveInt(PLAY_MODE, mode);
    }

    public static String getSplashUrl(){
        return getString(SPLASH_URL, "");
    }

    public static void saveSplashUrl(String url){
        saveString(SPLASH_URL, url);
    }

    public static boolean enableMobileNetworkPlay(){
        return getBoolean(mContext.getString(R.string.setting_key_mobile_network_play), false);
    }

    public static void saveMobileNetworkPlay(boolean enable){
        saveBoolean(mContext.getString(R.string.setting_key_mobile_network_play), enable);
    }

    public static boolean enableMoblieNetworkDownload(){
        return getBoolean(mContext.getString(R.string.setting_key_mobile_network_download), false);
    }

    public static boolean isNightMode(){
        return getBoolean(NIGHT_MODE, false);
    }

    public static void saveNightMode(boolean on){
        saveBoolean(NIGHT_MODE, on);
    }

    public static String getFilterSize(){
        return getString(mContext.getString(R.string.setting_key_filter_size),"0");
    }

    public static void saveFilterSize(String value){
        saveString(mContext.getString(R.string.setting_key_filter_size), value);
    }

    public static String getFilterTime(){
        return getString(mContext.getString(R.string.setting_key_filter_time), "0");
    }

    public static void saveFilterTime(String value){
        saveString(mContext.getString(R.string.setting_key_filter_time), value);
    }

    private static void saveBoolean(String key, boolean value){
        getPreferences().edit().putBoolean(key, value).apply();
    }

    private static boolean getBoolean(String key, boolean defValue){
        return getPreferences().getBoolean(key, defValue);
    }

    private static void saveInt(String key, int vaule){
        getPreferences().edit().putInt(key, vaule).apply();
    }

    private static int getInt(String key, int defVaule){
        return getPreferences().getInt(key, defVaule);
    }

    private static void saveLong(String key, long value){
        getPreferences().edit().putLong(key, value).apply();
    }

    private static long getLong(String key, long defvalue){
        return getPreferences().getLong(key, defvalue);
    }

    private static void saveString(String key,@Nullable String value){
        getPreferences().edit().putString(key, value).apply();
    }

    private static String getString(String key, String defValue){
        return getPreferences().getString(key, defValue);
    }

    private static SharedPreferences getPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }
}
