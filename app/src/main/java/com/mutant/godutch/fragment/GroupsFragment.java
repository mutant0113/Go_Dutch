package com.mutant.godutch.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mutant.godutch.AdapterGroup;
import com.mutant.godutch.NewGroupActivity;
import com.mutant.godutch.R;
import com.mutant.godutch.model.Group;

import java.util.ArrayList;

/**
 * Created by evanfang102 on 2017/3/30.
 */

public class GroupsFragment extends Fragment {

    RecyclerView mRecycleViewGroup;
    AdapterGroup mAdapterGroup;
    private DatabaseReference mDatabaseGroup;

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
        setupFirebase();
        setupGroups(view);
        setupFabNewGroup(view);
    }

    private void setupFabNewGroup(View view) {
        view.findViewById(R.id.fab_new_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(NewGroupActivity.getIntent(getActivity()));
            }
        });
    }

    private void setupGroups(View view) {
        mRecycleViewGroup = (RecyclerView) view.findViewById(R.id.recycler_view_group);
        mAdapterGroup = new AdapterGroup(getActivity(), new ArrayList<Group>());
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycleViewGroup.setAdapter(mAdapterGroup);
        mRecycleViewGroup.setLayoutManager(MyLayoutManager);
    }

    private void setupFirebase() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseGroup = FirebaseDatabase.getInstance().getReference().child("group").child(firebaseUser.getUid());
        mDatabaseGroup.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mAdapterGroup.addItem(dataSnapshot.getValue(Group.class));
                mRecycleViewGroup.scrollToPosition(0);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // TODO
            }

            @Override
            public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {
                // TODO
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                // TODO
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        mDatabaseGroup.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
//                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//                if (firebaseUser != null) {
//                    for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
//                        mGroups.add(groupSnapshot.getValue(Group.class));
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }
}
