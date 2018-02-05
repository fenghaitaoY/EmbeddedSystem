package com.android.blue.smarthomefunc.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.media.session.PlaybackStateCompat;

import com.android.blue.smarthomefunc.entity.Actions;
import com.android.blue.smarthomefunc.service.PlayService;

public class NoisyAudioStreamReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        PlayService.startCommand(context, Actions.ACTION_MEDIA_PLAY_PAUSE);
    }
}
