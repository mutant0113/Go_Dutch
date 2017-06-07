package com.mutant.godutch;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mutant.godutch.model.Event;
import com.mutant.godutch.model.Friend;
import com.mutant.godutch.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class EventsActivity extends BaseActivity {

    public static final String BUNDLE_KEY_GROUP_ID = "BUNDLE_KEY_GROUP_ID";
    String mGroupId;

    RecyclerView mRecycleViewEvent;
    RecycleViewAdapterEvent mAdapterEvent;
    DatabaseReference mDatabaseEvents;

    public static Intent getIntent(Activity activity, String groupId) {
        Intent intent = new Intent(activity, EventsActivity.class);
        intent.putExtra(BUNDLE_KEY_GROUP_ID, groupId);
        return intent;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_events;
    }

    @Override
    public void findViews() {

    }

    @Override
    public void setup() {
        mGroupId = getIntent().getStringExtra(BUNDLE_KEY_GROUP_ID);
        setupFireBase();
        setupEvents();
        setupFabNewEvent();
    }

    private void setupFireBase() {
        mDatabaseEvents = FirebaseDatabase.getInstance().getReference().child("events").child(mGroupId);
        mDatabaseEvents.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mAdapterEvent.addItem(dataSnapshot.getValue(Event.class));
                mRecycleViewEvent.scrollToPosition(0);
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
    }

    private void setupFabNewEvent() {
        findViewById(R.id.fab_new_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(NewEventActivity.getIntent(EventsActivity.this, mGroupId));
            }
        });
    }

    private void setupEvents() {
        mRecycleViewEvent = (RecyclerView) findViewById(R.id.recycler_view_event);
        // TODO fetch from web;
//        events.add(new Event("住宿", "六天住宿錢", friendWithPays));
//        events.add(new Event("早餐", "好吃的懷石料理", friendWithPays));
//        events.add(new Event("門票錢", "直接登上東京鐵塔!!", friendWithPays));
//        events.add(new Event("喔米阿給", "三大待喔米阿給YA~~", friendWithPays));
        mAdapterEvent = new RecycleViewAdapterEvent(this, new ArrayList<Event>());
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(this);
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycleViewEvent.setAdapter(mAdapterEvent);
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_item_event, parent, false);
            ViewHolderEvent holder = new ViewHolderEvent(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolderEvent holder, int position) {
            final Event event = events.get(position);
            holder.mTextViewTitle.setText(event.getTitle());
            holder.mTextViewDate.setText(Utility.getRelativeTimeSpanDate(event.getTimestampCreated()));
            holder.mTextViewDescription.setText(event.getDescription());
            holder.mTextViewTotalPaid.setText("$" + event.getTotalPaid());
            RecycleViewAdapterFriendsWhoPaid adapterFriendsWhoPaid = new RecycleViewAdapterFriendsWhoPaid(event.getFriends());
            holder.mRecycleViewFriendsWhoPaid.setAdapter(adapterFriendsWhoPaid);
            holder.mRecycleViewFriendsWhoPaid.setLayoutManager(new GridLayoutManager(EventsActivity.this, 2));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startActivity(NewEventActivity.getIntent(activity, mGroupId));
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    removeEvent(event);
                    return false;
                }
            });
        }

        private void removeEvent(final Event event) {
            new AlertDialog.Builder(activity).setTitle("系統提示").setMessage("確定要刪除此筆？")
                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mDatabaseEvents.child(event.getId()).removeValue();
                        }
                    }).setNeutralButton("取消", null).show();
        }

        @Override
        public int getItemCount() {
            return events.size();
        }

        public void addItem(Event event) {
            events.add(0, event);
            notifyItemInserted(0);
        }
    }

    class RecycleViewAdapterFriendsWhoPaid extends RecyclerView.Adapter<ViewHolderFriend> {

        List<Friend> friendWithPays;

        public RecycleViewAdapterFriendsWhoPaid(List<Friend> friendWithPays) {
            this.friendWithPays = friendWithPays;
        }

        @Override
        public ViewHolderFriend onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_item_friend_with_pay, parent, false);
            ViewHolderFriend holder = new ViewHolderFriend(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolderFriend holder, int position) {
            Friend friendWithPay = friendWithPays.get(position);
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
        public AppCompatTextView mTextViewDate;
        public AppCompatTextView mTextViewDescription;
        public AppCompatTextView mTextViewTotalPaid;
        public RecyclerView mRecycleViewFriendsWhoPaid;

        public ViewHolderEvent(View itemView) {
            super(itemView);
            findViews(itemView);
        }

        private void findViews(View itemView) {
            mTextViewTitle = (AppCompatTextView) itemView.findViewById(R.id.textView_title);
            mTextViewDate = (AppCompatTextView) itemView.findViewById(R.id.textView_date);
            mTextViewDescription = (AppCompatTextView) itemView.findViewById(R.id.textView_description);
            mTextViewTotalPaid = (AppCompatTextView) itemView.findViewById(R.id.textView_total_paid);
            mRecycleViewFriendsWhoPaid = (RecyclerView) itemView.findViewById(R.id.recycler_view_friends_with_pay);
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
