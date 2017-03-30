package com.mutant.godutch.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.mutant.godutch.R;

/**
 * Created by evanfang102 on 2017/3/30.
 */

public class GroupsFragment extends Fragment {

    ListView mListViewGroup;
    ArrayAdapter<String> mAdapter;

    public GroupsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, container, false);
        setupGroups(view);
        return view;
    }

    private void setupGroups(View view) {
        mListViewGroup = (ListView) view.findViewById(R.id.listView_group);
        mAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1);
        mListViewGroup.setAdapter(mAdapter);
        setupFirebase();
    }

    public static final String FIREBASE_URL = "https://godutch-c22a5.firebaseio.com/group";

    private void setupFirebase() {
        Firebase.setAndroidContext(getContext());
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
