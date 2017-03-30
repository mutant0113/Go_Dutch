package com.mutant.godutch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.mutant.godutch.Fragment.GroupsFragment;
import com.mutant.godutch.Fragment.SettingsFragment;

// TODO 登入系統
// TODO 建立個人Group
// TODO 加入他人Group
// TODO 輸入花費
// TODO 輸出清單以及分帳總結
public class MainActivity extends AppCompatActivity {

    public static final String FIREBASE_URL = "https://godutch-c22a5.firebaseio.com/group";

    ListView mListViewGroup;
    ArrayAdapter<String> mAdapter;
    ViewPager mViewPager;
    BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListViewGroup = (ListView) findViewById(R.id.listView_group);
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        mListViewGroup.setAdapter(mAdapter);
        Firebase.setAndroidContext(this);
        setupFirebase();
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

    private void setupFirebase() {
        new Firebase(FIREBASE_URL).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mAdapter.add((String) dataSnapshot.child("name").getValue());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                mAdapter.remove((String) dataSnapshot.child("name").getValue());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
