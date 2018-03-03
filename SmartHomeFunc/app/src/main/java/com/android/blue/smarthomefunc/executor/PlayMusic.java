package com.android.blue.smarthomefunc.executor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.model.Music;
import com.android.blue.smarthomefunc.utils.NetworkUtils;
import com.android.blue.smarthomefunc.utils.Preferences;

/**
 * Created by root on 3/2/18.
 */

public abstract class PlayMusic implements IExecutor<Music> {
    private Activity mActivity;
    protected Music music;
    private int mTotalStep;
    protected int mCounter = 0;

    public PlayMusic(Activity activity, int totalStep){
        mActivity = activity;
        mTotalStep = totalStep;
    }

    @Override
    public void execute() {
        checkNetwork();
    }

    private void checkNetwork() {
        boolean mobileNetworkPlay = Preferences.enableMobileNetworkPlay();
        if (NetworkUtils.isActiveNetworkMobile(mActivity) && !mobileNetworkPlay){
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle(R.string.tips);
            builder.setMessage(R.string.play_tips);
            builder.setPositiveButton(R.string.play_tips_sure, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Preferences.saveMobileNetworkPlay(true);
                    getPlayInfoWrapper();
                }
            });
            builder.setNegativeButton(R.string.cancel, null);
            builder.setCancelable(false);
            builder.create().show();
        }else{
            getPlayInfoWrapper();
        }
    }

    private void getPlayInfoWrapper(){
        onPrepare();
        getPlayInfo();
    }

    protected abstract void getPlayInfo();

    protected void checkCounter(){
        mCounter++;
        if (mCounter == mTotalStep){
            onExecuteSuccess(music);
        }
    }
}
