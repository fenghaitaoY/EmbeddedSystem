package com.android.blue.smarthomefunc.application;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.INotificationSideChannel;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.activity.LoginSuccessActivity;
import com.android.blue.smarthomefunc.activity.SplashActivity;
import com.android.blue.smarthomefunc.model.Music;
import com.android.blue.smarthomefunc.receiver.StatusBarReceiver;
import com.android.blue.smarthomefunc.service.PlayService;
import com.android.blue.smarthomefunc.utils.MusicCoverLoaderUtils;

import static android.support.v4.app.NotificationCompat.VISIBILITY_PRIVATE;

/**
 * Notification　应用通知
 * Created by fht on 3/28/18.
 */

public class Notifier {
    private static PlayService playService;
    private static final int NOTIFICATION_ID= 0x111;
    private static NotificationManager notificationManager;
    private static final String EXTRA_NOTIFICATION = "notification_smart";

    public static void init(PlayService playService){
        Notifier.playService = playService;
        notificationManager = (NotificationManager) playService.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static void showPlay(Music music){
        playService.startForeground(NOTIFICATION_ID, buildNotification(playService, music, true));
    }

    public static void showPause(Music music){
        playService.stopForeground(false);
        notificationManager.notify(NOTIFICATION_ID, buildNotification(playService, music, false));
    }

    public static void cancelAll(){
        notificationManager.cancelAll();
    }

    private static Notification buildNotification(Context context, Music music, boolean isPlaying){
        Intent intent = new Intent(context, LoginSuccessActivity.class);
        intent.putExtra(EXTRA_NOTIFICATION, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.smarthome)
                .setCustomBigContentView(getRemoteViews(context, music,isPlaying))
                .setVisibility(VISIBILITY_PRIVATE); //锁屏通知
        return builder.build();
    }

    private static RemoteViews getRemoteViews(Context context, Music music, boolean isPlaying){
        String title = music.getTitle();
        String artist = music.getArtist();
        Bitmap cover = MusicCoverLoaderUtils.getInstance().loadThumbnail(music);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_music_layout);
        if (cover != null){
            remoteViews.setImageViewBitmap(R.id.notification_cover, cover);
        }else{
            remoteViews.setImageViewResource(R.id.notification_cover, R.drawable.default_cover);
        }

        remoteViews.setTextViewText(R.id.notification_title, title);
        remoteViews.setTextViewText(R.id.notification_artist, artist);

        //播放，暂停
        Intent playIntent = new Intent(StatusBarReceiver.ACTION_STATUS_BAR);
        playIntent.putExtra(StatusBarReceiver.EXTRA, StatusBarReceiver.EXTRA_PLAY_PAUSE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setImageViewResource(R.id.notification_play_or_pause, getPlayIconRes(isPlaying));
        remoteViews.setOnClickPendingIntent(R.id.notification_play_or_pause, pendingIntent);

        //下一首
        Intent nextIntent = new Intent(StatusBarReceiver.ACTION_STATUS_BAR);
        nextIntent.putExtra(StatusBarReceiver.EXTRA, StatusBarReceiver.EXTRA_NEXT);
        PendingIntent nextpendingIntent = PendingIntent.getBroadcast(context, 1, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setImageViewResource(R.id.notification_next, R.drawable.selector_notification_next);
        remoteViews.setOnClickPendingIntent(R.id.notification_next, nextpendingIntent);

        //上一首
        Intent prevIntent = new Intent(StatusBarReceiver.ACTION_STATUS_BAR);
        prevIntent.putExtra(StatusBarReceiver.EXTRA, StatusBarReceiver.EXTRA_PREV);
        PendingIntent prevpendingIntent = PendingIntent.getBroadcast(context, 2, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setImageViewResource(R.id.notification_prev, R.drawable.selector_notification_prev);
        remoteViews.setOnClickPendingIntent(R.id.notification_prev, prevpendingIntent);

        //关闭应用
        Intent closeIntent = new Intent(StatusBarReceiver.ACTION_STATUS_BAR);
        closeIntent.putExtra(StatusBarReceiver.EXTRA, StatusBarReceiver.EXTRA_CLOSE);
        PendingIntent closependingIntent = PendingIntent.getBroadcast(context, 3, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setImageViewResource(R.id.notification_close, R.drawable.selector_notification_close);
        remoteViews.setOnClickPendingIntent(R.id.notification_close, closependingIntent);


        return remoteViews;
    }

    private static int getPlayIconRes(boolean isPlaying) {
        if (isPlaying){
            return R.drawable.selector_notification_pause;
        }else{
            return R.drawable.selector_notification_play;
        }
    }


}
