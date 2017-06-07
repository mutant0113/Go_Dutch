package com.mutant.godutch;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
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
        final Friend friend = friends.get(position);
        // TODO fetch image from web
        Glide.with(activity).load(friend.getProPicUrl()).error(R.drawable.profile_pic).into(holder.mImageViewPhoto);
        holder.mTextViewName.setText(friend.getName());
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                activity.startActivity(EventsActivity.getIntent(activity, friend.getId()));
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public void addItem(Friend friend) {
        friends.add(0, friend);
        notifyItemInserted(0);
    }
}
