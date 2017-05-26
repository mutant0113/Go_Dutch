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
import com.mutant.godutch.model.GroupModel;
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
        groupModels.add(new GroupModel("日本大阪七天六夜", "詳細描述", 34581, new String[] {"123", "123", "123"}));
        groupModels.add(new GroupModel("XXX生日 火鍋聚餐", "詳細描述 詳細描述 詳細描述 詳細描述 詳細描述", 1102, new String[] {"123", "123", "123"}));
        groupModels.add(new GroupModel("長灘島三天兩夜", "詳細描述 詳細描述 詳細描述 詳細描述 詳細描述 詳細描述 詳細描述 詳細描述 詳細描述", 12357, new String[] {"123", "123", "123"}));
        groupModels.add(new GroupModel("香港一天來回", "詳細描述 詳細描述 詳細描述", 5000, new String[] {"123", "123", "123"}));
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
