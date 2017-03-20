package com.mutant.godutch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

// TODO 登入系統
// TODO 建立個人Group
// TODO 加入他人Group
// TODO 輸入花費
// TODO 輸出清單以及分帳總結
public class MainActivity extends AppCompatActivity {

    public static final String FIREBASE_URL = "https://godutch-c22a5.firebaseio.com/group";

    ListView mListViewGroup;
    ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListViewGroup = (ListView) findViewById(R.id.listView_group);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        mListViewGroup.setAdapter(mAdapter);
        Firebase.setAndroidContext(this);
        initFirebase();
    }

    private void initFirebase() {
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
