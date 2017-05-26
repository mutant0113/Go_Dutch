package com.mutant.godutch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mutant.godutch.model.Event;
import com.mutant.godutch.model.FriendWithPay;

import java.util.ArrayList;
import java.util.List;

public class EventsActivity extends AppCompatActivity {

    RecyclerView mRecycleViewEvent;
    RecycleViewAdapterEvent mRecycleViewAdapter;

    public static Intent getIntent(Activity activity) {
        // TODO input group_id
        Intent intent = new Intent(activity, EventsActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        setupEvents();
    }

    private void setupEvents() {
        mRecycleViewEvent = (RecyclerView) findViewById(R.id.recycler_view_event);
        // TODO fetch from web;
        List<Event> events = new ArrayList<>();
        List<FriendWithPay> friendWithPays = new ArrayList<>();
        friendWithPays.add(new FriendWithPay("小美", "http://i.epochtimes.com/assets/uploads/2016/07/05c1348a7d53f02a1cc861f01d21878e-600x400.jpg", 500, 1000));
        friendWithPays.add(new FriendWithPay("小明", "http://i.epochtimes.com/assets/uploads/2016/07/05c1348a7d53f02a1cc861f01d21878e-600x400.jpg", 250, 1000));
        friendWithPays.add(new FriendWithPay("小剛", "http://i.epochtimes.com/assets/uploads/2016/07/05c1348a7d53f02a1cc861f01d21878e-600x400.jpg", 0, 1000));
        friendWithPays.add(new FriendWithPay("小智", "http://i.epochtimes.com/assets/uploads/2016/07/05c1348a7d53f02a1cc861f01d21878e-600x400.jpg", 0, 1000));
        events.add(new Event("住宿", "六天住宿錢", friendWithPays));
        events.add(new Event("早餐", "好吃的懷石料理", friendWithPays));
        events.add(new Event("門票錢", "直接登上東京鐵塔!!", friendWithPays));
        events.add(new Event("喔米阿給", "三大待喔米阿給YA~~", friendWithPays));
        mRecycleViewAdapter = new RecycleViewAdapterEvent(this, events);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(this);
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycleViewEvent.setAdapter(mRecycleViewAdapter);
        mRecycleViewEvent.setLayoutManager(MyLayoutManager);
    }

    class RecycleViewAdapterEvent extends RecyclerView.Adapter<ViewHolderEvent> {

        Activity activity;
        List<Event> events;

        public RecycleViewAdapterEvent(Activity activity, List<Event> events) {
            this.activity = activity;
            this.events = events;
        }

        @Override
        public ViewHolderEvent onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_item_event, parent, false);
            ViewHolderEvent holder = new ViewHolderEvent(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolderEvent holder, int position) {
            Event event = events.get(position);
            holder.mTextViewTitle.setText(event.getTitle());
            holder.mTextViewDescription.setText(event.getDescription());

            RecycleViewAdapterFriendsWithPay adapterFriendsWithPay = new RecycleViewAdapterFriendsWithPay(event.getFriendWithPays());
            holder.mRecycleViewFriendsWithPay.setAdapter(adapterFriendsWithPay);
            holder.mRecycleViewFriendsWithPay.setLayoutManager(new GridLayoutManager(EventsActivity.this, 2));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startActivity(NewEventActivity.getIntent(activity));
                }
            });
        }

        @Override
        public int getItemCount() {
            return events.size();
        }

    }

    class RecycleViewAdapterFriendsWithPay extends RecyclerView.Adapter<ViewHolderFriend> {

        List<FriendWithPay> friendWithPays;

        public RecycleViewAdapterFriendsWithPay(List<FriendWithPay> friendWithPays) {
            this.friendWithPays = friendWithPays;
        }

        @Override
        public ViewHolderFriend onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_item_friend_with_pay, parent, false);
            ViewHolderFriend holder = new ViewHolderFriend(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolderFriend holder, int position) {
            FriendWithPay friendWithPay = friendWithPays.get(position);
            holder.mTextViewName.setText(friendWithPay.getName());
            // TODO
//            holder.mImageViewProPic.setImageURI();
            holder.mTextViewPaidAndTotal.setText(friendWithPay.getPaid() + "/" + friendWithPay.getTotal());
        }

        @Override
        public int getItemCount() {
            return friendWithPays.size();
        }
    }

    class ViewHolderEvent extends RecyclerView.ViewHolder {

        public AppCompatTextView mTextViewTitle;
        public AppCompatTextView mTextViewDescription;
        public RecyclerView mRecycleViewFriendsWithPay;

        public ViewHolderEvent(View itemView) {
            super(itemView);
            findViews(itemView);
        }

        private void findViews(View itemView) {
            mTextViewTitle = (AppCompatTextView) itemView.findViewById(R.id.textView_title);
            mTextViewDescription = (AppCompatTextView) itemView.findViewById(R.id.textView_description);
            mRecycleViewFriendsWithPay = (RecyclerView) itemView.findViewById(R.id.recycler_view_friends_with_pay);
        }
    }

    class ViewHolderFriend extends RecyclerView.ViewHolder {

        public AppCompatImageView mImageViewProPic;
        public AppCompatTextView mTextViewName;
        public AppCompatTextView mTextViewPaidAndTotal;

        public ViewHolderFriend(View itemView) {
            super(itemView);
            findViews(itemView);
        }

        private void findViews(View itemView) {
            mImageViewProPic = (AppCompatImageView) itemView.findViewById(R.id.imageView_pro_pic);
            mTextViewName = (AppCompatTextView) itemView.findViewById(R.id.textView_name);
            mTextViewPaidAndTotal = (AppCompatTextView) itemView.findViewById(R.id.textView_paid_and_total);
        }
    }
}
