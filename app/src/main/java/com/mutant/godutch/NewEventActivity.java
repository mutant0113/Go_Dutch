package com.mutant.godutch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mutant.godutch.model.Friend;

import java.util.ArrayList;
import java.util.List;

public class NewEventActivity extends AppCompatActivity {

    RecyclerView mRecycleViewFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        setupFriends();
    }

    private void setupFriends() {
        mRecycleViewFriends = (RecyclerView) findViewById(R.id.recycler_view_friends);
        // TODO fetch from web;
        List<Friend> friends = new ArrayList<>();
        friends.add(new Friend("小美", "http://i.epochtimes.com/assets/uploads/2016/07/05c1348a7d53f02a1cc861f01d21878e-600x400.jpg"));
        friends.add(new Friend("小明", "http://i.epochtimes.com/assets/uploads/2016/07/05c1348a7d53f02a1cc861f01d21878e-600x400.jpg"));
        friends.add(new Friend("小剛", "http://i.epochtimes.com/assets/uploads/2016/07/05c1348a7d53f02a1cc861f01d21878e-600x400.jpg"));
        friends.add(new Friend("小智", "http://i.epochtimes.com/assets/uploads/2016/07/05c1348a7d53f02a1cc861f01d21878e-600x400.jpg"));
        RecycleViewAdapterFriends adapter = new RecycleViewAdapterFriends(friends);
        mRecycleViewFriends.setAdapter(adapter);
        mRecycleViewFriends.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private class RecycleViewAdapterFriends extends RecyclerView.Adapter<ViewHolder> {

        List<Friend> mFriends;

        public RecycleViewAdapterFriends(List<Friend> friends) {
            this.mFriends = friends;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_item_friend, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            Friend friend = mFriends.get(position);
            // TODO set image
//            holder.mImageViewProPic.setImageURI();
            holder.mTextViewName.setText(friend.getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO setbackground
//                    holder.mLinearLayoutCompat.setBackground();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mFriends.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayoutCompat mLinearLayoutCompat;
        AppCompatImageView mImageViewProPic;
        AppCompatTextView mTextViewName;

        public ViewHolder(View itemView) {
            super(itemView);
            findViews(itemView);
        }

        private void findViews(View itemView) {
            mLinearLayoutCompat = (LinearLayoutCompat) itemView.findViewById(R.id.linearLayoutCompat_friend);
            mImageViewProPic = (AppCompatImageView) itemView.findViewById(R.id.appCompat_imageView_pro_pic);
            mTextViewName = (AppCompatTextView) itemView.findViewById(R.id.appCompat_textView_name);
        }
    }

}
