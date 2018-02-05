package com.android.blue.smarthomefunc.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.application.AppCache;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.http.HttpCallback;
import com.android.blue.smarthomefunc.http.HttpClient;
import com.android.blue.smarthomefunc.model.Splash;
import com.android.blue.smarthomefunc.service.EventCallback;
import com.android.blue.smarthomefunc.service.PlayService;
import com.android.blue.smarthomefunc.utils.FileUtils;
import com.android.blue.smarthomefunc.utils.PermissionManage;
import com.android.blue.smarthomefunc.utils.Preferences;

import java.io.File;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashActivity extends BaseActivity {

    @BindView(R.id.splash_image)
    ImageView mSplashImage;
    @BindView(R.id.splash_bt)
    Button mSplashBt;
    @BindView(R.id.splash_tv)
    TextView mSplashTextView;

    private static final int PERMISSION_READ_STORAGE = 1;
    private static final int PERMISSION_WRITE_STORAGE = 2;

    private static final String SPLASH_FILE_NAME = "splash";

    private boolean isPermissonReadStorage = false;
    private boolean isPermissonWriteStorage = false;



    private ServiceConnection mPlayServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LogUtils.i("----ServiceConnected----");
            final PlayService playService = ((PlayService.PlayBinder) iBinder).getService();
            AppCache.get().setPlayService(playService);
            PermissionManage.with(SplashActivity.this)
                    .permissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
                    .result(new PermissionManage.Result() {
                        @Override
                        public void onGranted() {
                            LogUtils.i("onGranted");
                            scanMusic(playService);
                        }

                        @Override
                        public void onDenied() {
                            LogUtils.i("onDenied");
                            Toast.makeText(getApplicationContext(), "请授权", Toast.LENGTH_SHORT).show();
                            finish();
                            playService.quit();
                        }
                    }).request();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mPlayServiceConnection = null;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        setSplashCopyRight();
        mSplashBt.getBackground().setAlpha(50);
        checkService();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_STORAGE);
            }
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_STORAGE);
            }
        }
    }

    private void setSplashCopyRight(){
        int year = Calendar.getInstance().get(Calendar.YEAR);
        mSplashTextView.setText(getResources().getString(R.string.copyright, year));
    }


    @OnClick(R.id.splash_bt)
    public void onViewClicked() {
        startLoginSuccessActivity();
    }

    private void checkService(){
        if (AppCache.get().getPlayService() == null){
            LogUtils.i("-----checkService service = nulll");
            startPlayerService();
            showSplash();
            updateSplash();

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bindPlayerService();
                }
            }, 100);
        }else {
            LogUtils.i("-----checkService service ---");
            startLoginSuccessActivity();
            finish();
        }
    }


    private void startPlayerService(){
        Intent intent = new Intent(SplashActivity.this, PlayService.class);
        startService(intent);
    }

    private void bindPlayerService(){
        LogUtils.i("bindPlayService");
        Intent intent = new Intent();
        intent.setClass(this, PlayService.class);
        bindService(intent, mPlayServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void scanMusic(final PlayService playService){
        playService.updateMusicList(new EventCallback<Void>() {
            @Override
            public void onEvent(Void aVoid) {
                startLoginSuccessActivity();
                finish();
            }
        });
    }

    private void showSplash(){
        File splashImage = new File(FileUtils.getSplashDir(this), SPLASH_FILE_NAME);
        if (splashImage.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(splashImage.getPath());
            mSplashImage.setImageBitmap(bitmap);
        }
    }

    private void updateSplash(){
        HttpClient.getSplash(new HttpCallback<Splash>() {
            @Override
            public void onSuccess(Splash splash) {
                if (splash == null || TextUtils.isEmpty(splash.getUrl())){
                    return;
                }

                final String url = splash.getUrl();
                String lastImagUrl = Preferences.getSplashUrl();
                LogUtils.i("url : "+url+"\n, lastImageUrl : "+lastImagUrl);

                if (TextUtils.equals(lastImagUrl, url)){
                    return;
                }
                HttpClient.downloadFile(url, FileUtils.getSplashDir(AppCache.get().getContext()), SPLASH_FILE_NAME,
                        new HttpCallback<File>() {
                            @Override
                            public void onSuccess(File file) {
                                Preferences.saveSplashUrl(url);
                            }

                            @Override
                            public void onFail(Exception e) {

                            }
                        });
            }

            @Override
            public void onFail(Exception e) {

            }
        });
    }

    private void startLoginSuccessActivity(){
        LogUtils.i("startLoginSuccessActivity");
        Intent intent = new Intent();
        intent.setClass(this, LoginSuccessActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        //拦截返回按键
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayServiceConnection != null){
            unbindService(mPlayServiceConnection);
        }
    }
}
