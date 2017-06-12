package com.mutant.godutch;

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
import com.mutant.godutch.model.Friend;

import java.util.ArrayList;

/**
 * Created by evanfang102 on 2017/6/7.
 */

public class FriendsFragment extends Fragment {

    RecyclerView mRecycleViewFriends;
    AdapterFriend mAdapterFriend;
    private DatabaseReference mDatabaseFriend;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupFirebase();
        setupFriends(view);
        setupFabNewGroup(view);
    }

    private void setupFabNewGroup(View view) {
        view.findViewById(R.id.fab_new_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(NewFriendActivity.getIntent(getActivity()));
            }
        });
    }

    private void setupFriends(View view) {
        mRecycleViewFriends = (RecyclerView) view.findViewById(R.id.recycler_view_friends_shared);
        mAdapterFriend = new AdapterFriend(getActivity(), new ArrayList<Friend>());
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycleViewFriends.setAdapter(mAdapterFriend);
        mRecycleViewFriends.setLayoutManager(MyLayoutManager);
    }

    private void setupFirebase() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseFriend = FirebaseDatabase.getInstance().getReference().child("friends").child(firebaseUser.getUid());
        mDatabaseFriend.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mAdapterFriend.addItem(dataSnapshot.getValue(Friend.class));
                mRecycleViewFriends.scrollToPosition(0);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                mAdapterFriend.changeItemState(dataSnapshot.getValue(Friend.class));
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
    }
}
