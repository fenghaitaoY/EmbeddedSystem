package com.android.blue.smarthomefunc.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;

import com.android.blue.smarthomefunc.application.SmartHomeApplication;

public class BaseActivity extends AppCompatActivity {
    private SmartHomeApplication application;
    private BaseActivity mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        mContext = this;
        application = (SmartHomeApplication)getApplication();
        addActivity();
    }

    public void addActivity(){
        application.addActivity(mContext);
    }

    public void removeActivity(){
        application.removeActivity(mContext);
    }

    public void removeAllActivity(){
        application.removeAllActivity();
    }


}
