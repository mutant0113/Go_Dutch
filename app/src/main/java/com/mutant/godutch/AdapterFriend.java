package com.mutant.godutch;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mutant.godutch.model.Friend;

import java.util.List;

/**
 * Created by evanfang102 on 2017/3/30.
 */

public class AdapterFriend extends RecyclerView.Adapter<ViewHolderFriend> {

    private Activity activity;
    private List<Friend> friends;

    public AdapterFriend(Activity activity, List<Friend> friends) {
        this.activity = activity;
        this.friends = friends;
    }

    @Override
    public ViewHolderFriend onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_item_friend, parent, false);
        ViewHolderFriend holder = new ViewHolderFriend(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolderFriend holder, int position) {
    }

    @Override
    public void onBindViewHolder(ViewHolderFriend holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        final Friend friend = friends.get(position);
        if(payloads.isEmpty()) {
            Glide.with(activity).load(friend.getProPicUrl()).error(R.drawable.profile_pic).into(holder.mImageViewProPic);
            holder.mTextViewName.setText(friend.getName());
            setupFriendState(holder, friend);
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                activity.startActivity(EventsActivity.getIntent(activity, friend.getId()));
//            }
//        });
        } else {
            int type = (int) payloads.get(0);
            if (type == 0) {
                setupFriendState(holder, friend);
            }
        }
    }

    private void setupFriendState(final ViewHolderFriend holder, final Friend friend) {
        final TextView textView = holder.mTextViewInvitationState;
        final int state = friend.getState();
        switch (state) {
            case Friend.STATE_ACCEPTED:
                textView.setTextColor(Color.BLACK);
                textView.setText("朋友");
                break;
            case Friend.STATE_NOT_BE_ACCEPTED:
                textView.setTextColor(Color.RED);
                textView.setText("等待接受");
                break;
            case Friend.STATE_BE_INVITED:
                textView.setTextColor(Color.GRAY);
                textView.setText("接受");
                break;
        }

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(state == Friend.STATE_BE_INVITED) {
                    textView.setTextColor(Color.GREEN);
                    sendAcceptMsgToServer(holder, friend);
                }
            }
        });
    }

    private void sendAcceptMsgToServer(ViewHolderFriend holder, Friend friend) {
        DatabaseReference databaseFriends = FirebaseDatabase.getInstance().getReference().child("friends");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null) {
            // TODO check failed
            databaseFriends.child(firebaseUser.getUid()).child(friend.getUid()).child("state").setValue(Friend.STATE_ACCEPTED);
            databaseFriends.child(friend.getUid()).child(firebaseUser.getUid()).child("state").setValue(Friend.STATE_ACCEPTED);
        } else {
            holder.mTextViewInvitationState.setTextColor(Color.GRAY);
        }
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public void addItem(Friend friend) {
        friends.add(0, friend);
        notifyItemInserted(0);
    }

    public void changeItemState(Friend friend) {
        int position = -1;
        for(int i = 0; i < friends.size(); i++) {
            Friend child = friends.get(i);
            if(child.getName().equals(friend.getName())) {
                position = i;
                break;
            }
        }

        friends.set(position, friend);
        notifyItemChanged(position, 0);
    }
}
