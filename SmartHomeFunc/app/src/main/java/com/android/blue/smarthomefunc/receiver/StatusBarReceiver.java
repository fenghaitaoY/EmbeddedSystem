package com.android.blue.smarthomefunc.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.android.blue.smarthomefunc.application.AppCache;
import com.android.blue.smarthomefunc.application.SmartHomeApplication;
import com.android.blue.smarthomefunc.entity.Actions;
import com.android.blue.smarthomefunc.service.PlayService;

import org.blinkenlights.jid3.io.IFileSource;

public class StatusBarReceiver extends BroadcastReceiver {
    public static final String ACTION_STATUS_BAR = "com.android.blue.STATUS_BAR_ACTIONS";
    public static final String EXTRA = "extra";
    public static final String EXTRA_NEXT = "next";
    public static final String EXTRA_PLAY_PAUSE = "play_pause";
    public static final String EXTRA_PREV ="prev";
    public static final String EXTRA_CLOSE = "close";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || TextUtils.isEmpty(intent.getAction())){
            return;
        }

        String extra = intent.getStringExtra(EXTRA);
        if (TextUtils.equals(extra, EXTRA_NEXT)){
            PlayService.startCommand(context, Actions.ACTION_MEDIA_NEXT);
        }else if (TextUtils.equals(extra, EXTRA_PLAY_PAUSE)){
            PlayService.startCommand(context, Actions.ACTION_MEDIA_PLAY_PAUSE);
        }else if (TextUtils.equals(extra, EXTRA_PREV)){
            PlayService.startCommand(context, Actions.ACTION_MEDIA_PREVIOUS);
        } else if (TextUtils.equals(extra, EXTRA_CLOSE)) {
            ((SmartHomeApplication)AppCache.get().getApplication()).removeAllActivity(); //finish 所有打开的activity
        }

    }
}
