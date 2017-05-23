package com.mutant.godutch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.mutant.godutch.fragment.GroupsFragment;
import com.mutant.godutch.fragment.SettingsFragment;

// TODO 登入系統
// TODO 建立個人Group
// TODO 加入他人Group
// TODO 輸入花費
// TODO 輸出清單以及分帳總結
public class MainActivity extends AppCompatActivity {

    ViewPager mViewPager;
    BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViewPager();
        setupBottomNavigationView();
    }

    private void setupViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new GroupsFragment());
        viewPagerAdapter.addFragment(new SettingsFragment());
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(viewPagerAdapter);
    }

    public static final int VIEW_PAGER_GROUPS = 0;
    public static final int VIEW_PAGER_SETTINGS = VIEW_PAGER_GROUPS + 1;

    private void setupBottomNavigationView() {
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_groups:
                        mViewPager.setCurrentItem(VIEW_PAGER_GROUPS);
                        return true;
                    case R.id.action_settings:
                        mViewPager.setCurrentItem(VIEW_PAGER_SETTINGS);
                        return true;
                }
                return false;
            }
        });
    }

}
