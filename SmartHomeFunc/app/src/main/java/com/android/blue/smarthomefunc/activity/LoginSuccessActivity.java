package com.android.blue.smarthomefunc.activity;

import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.blue.smarthomefunc.application.SmartHomeApplication;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.entity.HCBluetoothControl;
import com.android.blue.smarthomefunc.fragment.DeviceControlFragment;
import com.android.blue.smarthomefunc.fragment.MusicFragment;
import com.android.blue.smarthomefunc.fragment.PeopleSetitingFragment;
import com.android.blue.smarthomefunc.fragment.VideoFragment;
import com.android.blue.smarthomefunc.jninative.SmartHomeNativeUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginSuccessActivity extends BaseActivity implements DeviceControlFragment.OnFragmentInteractionListener {

    @BindView(R.id.viewpager)
    public ViewPager viewpager;

    @BindView(R.id.navigation_bottom)
    public BottomNavigationView bottomNavi;

    private long time;

    List<Fragment> fragments = new ArrayList<>();
    Fragment currentFragment;
    MyViewPagerAdapter viewPagerAdapter;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    LogUtils.i("select device");
                    viewpager.setCurrentItem(0);
                    return true;
                case R.id.navigation_music:
                    LogUtils.i("select music");
                    viewpager.setCurrentItem(1);
                    return true;
                case R.id.navigation_video:
                    LogUtils.i("select video");
                    viewpager.setCurrentItem(2);
                    return true;
                case R.id.navigation_people:
                    LogUtils.i("select people");
                    viewpager.setCurrentItem(3);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_success);
        setVolumeControlStream(AudioManager.STREAM_MUSIC); //设置基于BASE的activity 音量键是设置音乐音量

        bottomNavi.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //去除BottomNavigationView count>3时，切换viewpage滑动效果
        BottomNavigationMenuView bottomMenuView = (BottomNavigationMenuView) bottomNavi.getChildAt(0);
        try {
            Field shiftingMode = bottomMenuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(bottomMenuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < bottomMenuView.getChildCount(); i++) {
                BottomNavigationItemView itemView = (BottomNavigationItemView) bottomMenuView.getChildAt(i);
                itemView.setShiftingMode(false);
                itemView.setChecked(itemView.getItemData().isChecked());
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        viewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        viewpager.setAdapter(viewPagerAdapter);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bottomNavi.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        fragments.add(DeviceControlFragment.newInstance());
        fragments.add(MusicFragment.newInstance("music", "show"));
        fragments.add(VideoFragment.newInstance("video", "play"));
        fragments.add(PeopleSetitingFragment.newInstance("people", "setting"));
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void setActivityFullScreen(boolean flag) {

    }

    class MyViewPagerAdapter extends FragmentPagerAdapter {
        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭广播注册
        HCBluetoothControl.getInstance(this).unregisterBroadcast();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        currentFragment = viewPagerAdapter.getItem(viewpager.getCurrentItem());
        if (currentFragment instanceof DeviceControlFragment){
            ((DeviceControlFragment) currentFragment).onKeyDown(keyCode, event);
        }

        if (keyCode == event.KEYCODE_BACK){
            LogUtils.i("-------keycode back------");
            if (System.currentTimeMillis() - time  > 2000){
                time = System.currentTimeMillis();
                Toast.makeText(getApplicationContext(), "再点击一次退出应用程序",Toast.LENGTH_SHORT).show();
            }else{
                ((SmartHomeApplication)getApplication()).removeAllActivity(); //finish 所有打开的activity

            }
        }
        return  true;
    }
}
