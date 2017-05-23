package com.mutant.godutch.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.mutant.godutch.GroupModel;
import com.mutant.godutch.R;
import com.mutant.godutch.RecycleViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by evanfang102 on 2017/3/30.
 */

public class GroupsFragment extends Fragment {

    RecyclerView mRecycleViewGroup;
    RecycleViewAdapter mRecycleViewAdapter;

    public GroupsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_groups, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupGroups(view);
        setupFirebase();
    }

    private void setupGroups(View view) {
        mRecycleViewGroup = (RecyclerView) view.findViewById(R.id.recycler_view_group);
        // TODO fetch from web;
        List<GroupModel> groupModels = new ArrayList<>();
        groupModels.add(new GroupModel("test1", "this is test", 200, new String[] {"123", "123", "123"}));
        groupModels.add(new GroupModel("test2", "this is test this is test this is test this is test", 200, new String[] {"123", "123", "123"}));
        groupModels.add(new GroupModel("test3", "this is test this is test this is test ", 200, new String[] {"123", "123", "123"}));
        groupModels.add(new GroupModel("test3", "this is test this is test this is test this is test this is test this is test this is test", 200, new String[] {"123", "123", "123"}));
        mRecycleViewAdapter = new RecycleViewAdapter(groupModels);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycleViewGroup.setAdapter(mRecycleViewAdapter);
        mRecycleViewGroup.setLayoutManager(MyLayoutManager);
//        mRecycleViewGroup.setLayoutManager(new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL));
//        mRecycleViewGroup.setLayoutManager(new GridLayoutManager(getContext(), 2));
    }

    public static final String FIREBASE_URL = "https://godutch-c22a5.firebaseio.com/group";

    private void setupFirebase() {
        Firebase.setAndroidContext(getContext());
        new Firebase(FIREBASE_URL).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                mAdapter.add((String) dataSnapshot.child("name").getValue());
//                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                mAdapter.remove((String) dataSnapshot.child("name").getValue());
//                mAdapter.notifyDataSetChanged();
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
